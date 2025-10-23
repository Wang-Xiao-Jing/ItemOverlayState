package xiaojin.itemoverlaystate.mixin.client;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xiaojin.itemoverlaystate.IosMain;
import xiaojin.itemoverlaystate.client.override.IosItemOverride;
import xiaojin.itemoverlaystate.mixinimod.client.IModPredicate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 混入类，用于修改 Minecraft 原生 ItemOverride.Deserializer 的行为。
 * 主要目的是支持自定义的 ItemOverlayState（IOS）属性解析逻辑，
 * 允许在模型覆盖条件中使用特殊的字符串键作为 ResourceLocation，并处理其对应的 JSON 元素值。
 */
@Mixin(ItemOverride.Deserializer.class)
public abstract class MixinDeserializer implements JsonDeserializer<ItemOverride> {

	/**
	 * 调用原版 getPredicates 方法的混入接口方法。
	 *
	 * @param jsonObject 包含谓词信息的 JsonObject
	 * @return 解析后的 Predicate 列表
	 */
	@Shadow
	protected abstract List<ItemOverride.Predicate> getPredicates(JsonObject jsonObject);

	/**
	 * 重定向 GsonHelper.convertToFloat 方法调用，在特定条件下返回默认浮点数 0.0f。
	 * 当属性名符合 isIos 条件时，跳过原始转换逻辑以避免类型错误。
	 *
	 * @param json JSON 元素对象
	 * @param key 属性名称
	 * @return 浮点数值，若为 IOS 属性则固定返回 0.0f
	 */
	@Redirect(method = "getPredicates", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/GsonHelper;" +
			"convertToFloat(Lcom/google/gson/JsonElement;Ljava/lang/String;)F"))
	private float ios$getPredicates$redirectGetFloat(JsonElement json, String key) {
		if (IosItemOverride.isPredicate(key)) {
			return Float.MIN_VALUE;
		}
		return GsonHelper.convertToFloat(json, key);
	}

	/**
	 * 重定向 deserialize 过程中的 getPredicates 调用。
	 * 在获取原始谓词列表后，进一步提取并处理所有符合 isIos 条件的属性键值对，
	 * 并将其设置到对应谓词对象中供后续使用。
	 *
	 * @param instance   ItemOverride.Deserializer 实例
	 * @param jsonObject 包含谓词定义的 JsonObject
	 * @return 处理完成的 Predicate 列表
	 */
	@Redirect(method = "deserialize*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/ItemOverride$Deserializer;" +
			"getPredicates(Lcom/google/gson/JsonObject;)Ljava/util/List;"))
	private List<ItemOverride.Predicate> sti$deserialize$getPredicates(ItemOverride.Deserializer instance, JsonObject jsonObject) {
		// 获取原始谓词列表
		var predicates = getPredicates(jsonObject);

		// 遍历谓词列表，匹配满足条件的项并注入对应的 JsonElement 值
		Map<ResourceLocation, @NotNull JsonElement> jsonMap;
		try {
			jsonMap = GsonHelper.convertToJsonObject(jsonObject, "predicate").entrySet().stream()
					.map(a -> Map.entry(a.getKey(), a.getValue()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
					.get("predicate").getAsJsonObject().entrySet().stream()
					.collect(Collectors.toMap(e -> ResourceLocation.parse(e.getKey()), Map.Entry::getValue));
		} catch (Exception e) {
			IosMain.LOGGER.error("Failed to parse predicate json", e);
			throw new JsonSyntaxException(e);
		}
		predicates.stream().map(predicate -> Map.entry(predicate.getProperty(), IModPredicate.of(predicate)))
				.forEach(predicate -> predicate.getValue().ios$setElementValue(jsonMap.get(predicate.getKey())));

		return predicates;
	}
}
