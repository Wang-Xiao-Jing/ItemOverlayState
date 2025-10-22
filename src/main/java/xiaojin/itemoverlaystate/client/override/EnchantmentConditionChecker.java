package xiaojin.itemoverlaystate.client.override;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.CommonHooks;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.HashMap;

/**
 * 检查附魔相关条件的工具类
 */
public class EnchantmentConditionChecker {

	/**
	 * 检查物品附魔是否满足条件
	 */
	public static boolean enchantment(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity livingEntity, JsonElement json, int seed) {
		if (!json.isJsonObject()) {
			return true;
		}

		final HolderLookup.RegistryLookup<Enchantment> lookup;
		if ((lookup = CommonHooks.resolveLookup(Registries.ENCHANTMENT)) == null) {
			return true;
		}
		var jsonObject = json.getAsJsonObject();
		var enchantments = getEnchantmentMap(stack, lookup);
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
	public static boolean includeEnchantment(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity livingEntity, JsonElement json, int seed) {
		if ((!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) && !json.isJsonArray()) {
			return true;
		}

		final HolderLookup.RegistryLookup<Enchantment> lookup;
		if ((lookup = CommonHooks.resolveLookup(Registries.ENCHANTMENT)) == null) {
			return true;
		}

		if (json.isJsonPrimitive()) {
			var enchantments = getEnchantmentMap(stack, lookup);
			return enchantments.containsKey(ResourceLocation.parse(json.getAsString()));
		} else if (json.isJsonArray()) {
			for (JsonElement textElement : json.getAsJsonArray()) {
				if (!textElement.isJsonPrimitive() || !textElement.getAsJsonPrimitive().isString()) {
					continue;
				}
				var enchantments = getEnchantmentMap(stack, lookup);
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
	private static Map<ResourceLocation, Integer> getEnchantmentMap(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup) {
		var enchantmentsMap = new HashMap<ResourceLocation, Integer>();
		if (lookup == null) {
			return enchantmentsMap;
		}
		var enchantments = stack.getAllEnchantments(lookup);
		for (var entry : enchantments.entrySet()) {
			var enchantment = entry.getKey();
			var key = enchantment.getKey();
			if (key == null) {
				continue;
			}
			enchantmentsMap.put(key.location(), enchantments.getLevel(enchantment));
		}
		return enchantmentsMap;
	}
}