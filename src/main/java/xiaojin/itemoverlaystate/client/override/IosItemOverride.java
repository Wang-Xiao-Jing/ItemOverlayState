package xiaojin.itemoverlaystate.client.override;

import com.google.gson.JsonElement;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModLoader;
import xiaojin.itemoverlaystate.client.event.ItemOverridePredicateEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * 物品堆叠纹理覆盖处理类
 */
public class IosItemOverride {
	public static final Map<ResourceLocation, Predicate> PREDICATES = new HashMap<>();

	public static void init() {
		ModLoader.postEvent(new ItemOverridePredicateEvent.NewOverridePredicate());
	}

	/**
	 * 判断自定义高级条件
	 *
	 * @return 如果满足自定义高级条件返回true，否则返回false
	 */
	public static boolean test(ResourceLocation predicateName, BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity livingEntity, JsonElement json, int seed) {
		if (json == null || json.isJsonNull()) {
			return false;
		}
		return !PREDICATES.containsKey(predicateName) ||
				PREDICATES.get(predicateName).test(model, stack, level, livingEntity, json, seed);
	}

	@FunctionalInterface
	public interface Predicate {
		boolean test(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity livingEntity, JsonElement json, int seed);
	}

	/**
	 * 判断给定的键是否为已注册的谓词
	 *
	 * @param key 要检查的键
	 */
	public static boolean isPredicate(String key) {
		return isPredicate(ResourceLocation.parse(key));
	}

	public static boolean isPredicate(ResourceLocation key) {
		return PREDICATES.containsKey(key);
	}
}