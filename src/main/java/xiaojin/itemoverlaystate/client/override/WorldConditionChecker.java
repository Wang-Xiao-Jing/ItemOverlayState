package xiaojin.itemoverlaystate.client.override;

import com.google.gson.JsonElement;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WorldConditionChecker {
	/**
	 * 根据时间判断是否满足条件
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	public static boolean time(ResourceLocation modelRL, ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase livingEntity, JsonElement json) {
		if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isNumber()) {
			return false;
		}
		if (worldIn == null) {
			return false;
		}
		return worldIn.getWorldTime() >= json.getAsLong();
	}

	/**
	 * 根据世界天数判断是否满足条件
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	public static boolean day(ResourceLocation modelRL, ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase livingEntity, JsonElement json) {
		if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isNumber()) {
			return false;
		}
		if (worldIn == null) {
			return false;
		}

		long day = (worldIn.getWorldInfo().getWorldTotalTime() / 24000) + 1;
		return day >= json.getAsLong();
	}

}
