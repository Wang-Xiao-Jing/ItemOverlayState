package xiaojin.itemoverlaystate.client.override;

import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 检查世界状态相关条件的工具类
 */
@Environment(EnvType.CLIENT)
public class WorldConditionChecker {

	/**
	 * 检查游戏时间是否满足条件
	 */
	public static boolean time(BakedModel model, ItemStack stack,  ClientLevel level,  LivingEntity livingEntity, JsonElement json, int seed) {
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
	public static boolean day(BakedModel model, ItemStack stack,  ClientLevel level,  LivingEntity livingEntity, JsonElement json, int seed) {
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