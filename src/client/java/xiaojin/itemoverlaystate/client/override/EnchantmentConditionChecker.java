package xiaojin.itemoverlaystate.client.override;

import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 检查附魔相关条件的工具类
 */
@Environment(EnvType.CLIENT)
public class EnchantmentConditionChecker {

	/**
	 * 检查物品附魔是否满足条件
	 */
	public static boolean enchantment(BakedModel model, ItemStack stack,  ClientLevel level,  LivingEntity livingEntity, JsonElement json, int seed) {
		if (!json.isJsonObject()) {
			return true;
		}

		var jsonObject = json.getAsJsonObject();
		var enchantments = getEnchantmentMap(stack);
		for (var entry : jsonObject.entrySet()) {
			var key = ResourceLocation.parse(entry.getKey());
			if (!enchantments.containsKey(key)) {
				return false;
			}
			if (enchantments.get(key) < entry.getValue().getAsShort()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 检查物品是否包含指定附魔
	 */
	public static boolean includeEnchantment(BakedModel model, ItemStack stack,  ClientLevel level,  LivingEntity livingEntity, JsonElement json, int seed) {
		if ((!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) && !json.isJsonArray()) {
			return true;
		}

		if (json.isJsonPrimitive()) {
			var enchantments = getEnchantmentMap(stack);
			return enchantments.containsKey(ResourceLocation.parse(json.getAsString()));
		} else if (json.isJsonArray()) {
			for (JsonElement textElement : json.getAsJsonArray()) {
				if (!textElement.isJsonPrimitive() || !textElement.getAsJsonPrimitive().isString()) {
					continue;
				}
				var enchantments = getEnchantmentMap(stack);
				if (!enchantments.containsKey(ResourceLocation.parse(textElement.getAsString()))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 获取物品的附魔信息
	 */
	private static Map<ResourceLocation, Integer> getEnchantmentMap(ItemStack stack) {
		var enchantmentsMap = new HashMap<ResourceLocation, Integer>();
		var enchantments = stack.getEnchantments();
		for (var entry : enchantments.entrySet()) {
			var i = entry.getIntValue();
			Optional<ResourceKey<Enchantment>> enchantmentResourceKey = entry.getKey().unwrapKey();
			if (enchantmentResourceKey.isEmpty()) {
				continue;
			}
			enchantmentsMap.put(enchantmentResourceKey.get().location(), i);
		}
		return enchantmentsMap;
	}
}