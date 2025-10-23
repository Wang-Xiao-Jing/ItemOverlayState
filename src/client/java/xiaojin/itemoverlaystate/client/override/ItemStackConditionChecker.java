package xiaojin.itemoverlaystate.client.override;

import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xiaojin.itemoverlaystate.util.IosUtil;

/**
 * 检查物品相关条件的工具类
 */
@Environment(EnvType.CLIENT)
public class ItemStackConditionChecker {

	/**
	 * 检查物品堆叠数量是否满足条件
	 */
	public static boolean stacking(BakedModel model, ItemStack stack,  ClientLevel level,  LivingEntity livingEntity, JsonElement json, int seed) {
		if (!json.isJsonPrimitive()) {
			return false;
		}

		final int count = stack.getCount();

		if (!IosUtil.numericalJudgment(json, count)) {
			return IosUtil.percentageJudgment(json, count, stack.getMaxStackSize());
		}

		return true;
	}

	/**
	 * 检查物品耐久度是否满足条件
	 */
	public static boolean damage(BakedModel model, ItemStack stack,  ClientLevel level,  LivingEntity livingEntity, JsonElement json, int seed) {
		if (!json.isJsonPrimitive()) {
			return false;
		}

		int damage = stack.getDamageValue();

		if (!IosUtil.numericalJudgment(json, damage)) {
			return IosUtil.percentageJudgment(json, damage, stack.getMaxDamage());
		}

		return true;
	}

	/**
	 * 检查物品名称是否满足条件
	 */
	public static boolean name(BakedModel model, ItemStack stack,  ClientLevel level,  LivingEntity livingEntity, JsonElement json, int seed) {
		if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
			return false;
		}
		return java.util.regex.Pattern.compile(json.getAsString()).matcher(stack.getDisplayName().getString()).matches();
	}
}