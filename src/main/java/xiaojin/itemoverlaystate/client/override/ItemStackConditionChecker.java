package xiaojin.itemoverlaystate.client.override;

import com.google.gson.JsonElement;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

import static xiaojin.itemoverlaystate.util.IosUtil.numericalJudgment;
import static xiaojin.itemoverlaystate.util.IosUtil.percentageJudgment;

public class ItemStackConditionChecker {
	/**
	 * 根据物品名称判断是否满足条件
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	public static boolean name(ResourceLocation modelRL, ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase livingEntity, JsonElement json) {
		if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
			return false;
		}
		return Pattern.compile(json.getAsString()).matcher(stack.getDisplayName()).matches();
	}

	/**
	 * 根据物品损坏值判断是否满足条件
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	public static boolean damage(ResourceLocation modelRL, ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase livingEntity, JsonElement json) {
		if (!json.isJsonPrimitive()) {
			return false;
		}

		int damage = stack.getItemDamage();

		if (!numericalJudgment(json, damage)) {
			return percentageJudgment(json, damage, stack.getMaxDamage());
		}

		return true;
	}

	/**
	 * 根据物品堆叠数量判断是否满足条件
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	public static boolean stacking(ResourceLocation modelRL, ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase livingEntity, JsonElement json) {
		if (!json.isJsonPrimitive()) {
			return false;
		}

		final int count = stack.getCount();

		if (!numericalJudgment(json, count)) {
			return percentageJudgment(json, count, stack.getMaxStackSize());
		}

		return true;
	}
}
