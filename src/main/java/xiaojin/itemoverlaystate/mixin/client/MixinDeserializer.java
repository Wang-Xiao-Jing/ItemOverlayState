package xiaojin.itemoverlaystate.mixin.client;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xiaojin.itemoverlaystate.client.override.IosItemOverride;
import xiaojin.itemoverlaystate.mixinimod.client.IModItemOverride;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * ItemOverride反序列化器的Mixin类，用于支持ios（Stacking Texture Items）功能
 *
 * <p>该类通过Mixin技术修改原版Minecraft的ItemOverride.Deserializer类，
 * 使其能够处理ios自定义的物品模型覆盖规则，特别是基于物品堆叠数量的纹理切换功能。</p>
 */
@SideOnly(Side.CLIENT)
@Mixin(targets = "net.minecraft.client.renderer.block.model.ItemOverride$Deserializer")
public abstract class MixinDeserializer implements JsonDeserializer<ItemOverride> {
	/**
	 * 原版makeMapResourceValues方法的Mixin影子方法
	 *
	 * @param p_188025_1_ JSON对象
	 * @return 资源位置到浮点值的映射
	 */
	@Shadow
	protected abstract Map<ResourceLocation, Float> makeMapResourceValues(JsonObject p_188025_1_);

	/**
	 * 重定向原始makeMapResourceValues方法中的JsonUtils.getFloat调用
	 *
	 * <p>对于ios命名空间的属性，返回0.0f作为占位值，因为ios使用字符串值而非浮点值</p>
	 *
	 * @param jsonElement JSON元素
	 * @param key         属性键名
	 * @return 如果是ios属性返回0.0f，否则返回解析的浮点值
	 */
	@Redirect(method = "makeMapResourceValues", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/JsonUtils;getFloat(Lcom/google/gson/JsonElement;Ljava/lang/String;)F"))
	private float sti$makeMapResourceValues$redirectGetFloat(JsonElement jsonElement, String key) {
		if (IosItemOverride.isPredicate(key)) {
			return Float.MIN_VALUE;
		}
		return JsonUtils.getFloat(jsonElement, key);
	}

	/**
	 * 拦截并重写ItemOverride的反序列化过程
	 *
	 * <p>该方法在原反序列化方法执行前被调用，创建支持STI功能的ItemOverride实例</p>
	 *
	 * @param element JSON元素
	 * @param type    反序列化的目标类型
	 * @param j       JSON反序列化上下文
	 * @param cir     回调信息，用于设置返回值和取消原方法执行
	 */
	@Inject(at = @At("HEAD"), method = "deserialize*", cancellable = true)
	private void ios$deserialize(JsonElement element, Type type, JsonDeserializationContext j, CallbackInfoReturnable<ItemOverride> cir) {
		JsonObject jsonObject = element.getAsJsonObject();
		// 提取ios相关的字符串映射
		Map<ResourceLocation, JsonElement> stringMap = Maps.newLinkedHashMap();
		// 从JSON对象中提取ios相关的资源值映射
		JsonObject jsonObject1 = JsonUtils.getJsonObject(jsonObject, "predicate");
		for (Map.Entry<String, JsonElement> entry : jsonObject1.entrySet()) {
			stringMap.put(new ResourceLocation(entry.getKey()), entry.getValue());
		}
		// 提取原版的浮点映射
		Map<ResourceLocation, Float> floatMap = makeMapResourceValues(jsonObject);
		// 获取模型资源位置
		ResourceLocation locationIn = new ResourceLocation(JsonUtils.getString(jsonObject, "model"));
		// 创建支持STI功能的ItemOverride实例
		IModItemOverride itemOverride = IModItemOverride.of(new ItemOverride(locationIn, floatMap));
		// 设置ios字符串映射
		itemOverride.ios$setMapResourceValues(stringMap);
		// 设置返回值并取消原方法执行
		cir.setReturnValue((ItemOverride) itemOverride);
	}
}
