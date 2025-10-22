package xiaojin.itemoverlaystate.client.override;

import com.google.gson.JsonElement;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * 检查世界状态相关条件的工具类
 */
public class WorldConditionChecker {

	/**
	 * 检查游戏时间是否满足条件
	 */
	public static boolean time(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity livingEntity, JsonElement json, int seed) {
		if (level == null) {
			return true;
		}
		if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isNumber()) {
			return false;
		}
		return level.getDayTime() >= json.getAsLong();
	}

	/**
	 * 检查游戏天数是否满足条件
	 */
	public static boolean day(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity livingEntity, JsonElement json, int seed) {
		if (level == null) {
			return true;
		}
		if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isNumber()) {
			return false;
		}

		long day = (level.getGameTime() / 24000) + 1;
		return day >= json.getAsLong();
	}
}