package xiaojin.itemoverlaystate.mixin.client;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xiaojin.itemoverlaystate.IosMain;
import xiaojin.itemoverlaystate.client.override.IosItemOverride;
import xiaojin.itemoverlaystate.mixinimod.client.IModPredicate;
import xiaojin.itemoverlaystate.mixinimod.client.IModPropertyMatcher;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Mixin类，用于修改ItemOverrides的构造过程，以便支持自定义的PropertyMatcher创建逻辑。
 * 主要目的是在初始化ItemOverrides时，注入额外的属性值信息（如JsonElement）到PropertyMatcher中。
 */
@Mixin(ItemOverrides.class)
public abstract class MixinItemOverrides {
	@Unique
	private BakedModel ios$model;
	@Unique
	@Nullable
	private ItemStack ios$stack;
	@Unique
	@Nullable
	private ClientLevel ios$level;
	@Unique
	@Nullable
	private LivingEntity ios$livingEntity;
	@Unique
	private int ios$seed;
	@Shadow
	@Final
	private ResourceLocation[] properties;

	/**
	 * 拦截ItemOverrides构造函数中的第2个Stream.map调用，替换为自定义的PropertyMatcher创建逻辑。
	 *
	 * @param instance 原始的ItemOverride.Predicate流
	 * @param function 原本用于映射Predicate到PropertyMatcher的函数（被替换）
	 * @return 修改后的PropertyMatcher对象流
	 */
	@Redirect(method = "<init>(Lnet/minecraft/client/resources/model/ModelBaker;" +
			"Lnet/minecraft/client/resources/model/UnbakedModel;" +
			"Ljava/util/List;Ljava/util/function/Function;)V",
			at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;" +
					"map(Ljava/util/function/Function;)Ljava/util/stream/Stream;", ordinal = 1))
	private Stream<Object> ios$redirectPropertyMatcherCreation(Stream<ItemOverride.Predicate> instance, Function<ItemOverride.Predicate, Object> function) {
		// 构建属性名称到索引的映射表，用于快速查找属性在properties数组中的位置
		var object2intmap = new Object2IntOpenHashMap<ResourceLocation>();
		for (int i = 0; i < properties.length; ++i) {
			object2intmap.put(properties[i], i);
		}

		// 对每个Predicate创建对应的PropertyMatcher，并注入额外的JsonElement值
		return instance.map((predicate) -> {
			try {
				// 使用反射获取原版PropertyMatcher类及其构造器
				var property = predicate.getProperty();
				var propertyMatcher = ItemOverrides.PropertyMatcher.class
						.getDeclaredConstructor(int.class, float.class)
						.newInstance(object2intmap.getInt(property), predicate.getValue());

				// 如果value不为空则添加进propertyMatcher
				var value = IModPredicate.of(predicate).ios$getElementValue();
				var propertyMatcher1 = IModPropertyMatcher.of(propertyMatcher);
				propertyMatcher1.ios$setName(property);
				propertyMatcher1.ios$setElementValue(value);
				return propertyMatcher;
			} catch (Exception e) {
				IosMain.LOGGER.error("Failed to load PropertyMatcher class");
				throw new RuntimeException(e);
			}
		});
	}

	@Inject(method = "resolve", at = @At("HEAD"))
	private void ios$resolve(BakedModel model, ItemStack itemStack, ClientLevel level, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
		this.ios$model = model;
		this.ios$stack = itemStack;
		this.ios$level = level;
		this.ios$livingEntity = entity;
		this.ios$seed = seed;
	}

	@Redirect(method = "resolve", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/ItemOverrides$BakedOverride;test([F)Z"))
	private boolean ios$test(ItemOverrides.BakedOverride instance, float[] floats) {
		for (var matcher : instance.matchers) {
			float f = floats[matcher.index];
			var iMatcher = IModPropertyMatcher.of(matcher);
			var key = iMatcher.ios$getName();
			if (!IosItemOverride.isPredicate(key)) {
				if (f < matcher.value) {
					return false;
				}
			} else {
				if (!IosItemOverride.test(key, ios$model, ios$stack, ios$level, ios$livingEntity,
						iMatcher.ios$getElementValue(), ios$seed)) {
					return false;
				}
			}
		}

		return true;
	}
}
