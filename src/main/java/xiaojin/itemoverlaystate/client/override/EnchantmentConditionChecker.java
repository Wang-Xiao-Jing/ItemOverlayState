package xiaojin.itemoverlaystate.client.override;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentConditionChecker {
	/**
	 * 根据json的值判断物品的附魔
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	public static boolean enchantment(ResourceLocation modelRL, ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase livingEntity, JsonElement json) {
		if (!json.isJsonObject()) {
			return false;
		}
		JsonObject jsonObject = json.getAsJsonObject();
		Map<ResourceLocation, Short> enchantments = getEnchantmentMap(stack);
		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			ResourceLocation key = new ResourceLocation(entry.getKey());
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
	 * 判断物品是否包含这些附魔
	 *
	 * @return 如果包含返回true，否则返回false
	 */
	public static boolean includeEnchantment(ResourceLocation modelRL, ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase livingEntity, JsonElement json) {
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
			ResourceLocation key = new ResourceLocation(json.getAsString());
			Map<ResourceLocation, Short> enchantments = getEnchantmentMap(stack);
			if (!enchantments.containsKey(key)) {
				return false;
			}
		} else if (!json.isJsonArray()) {
			return false;
		}
		for (JsonElement textElement : json.getAsJsonArray()) {
			if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
				continue;
			}
			ResourceLocation key = new ResourceLocation(textElement.getAsString());
			Map<ResourceLocation, Short> enchantments = getEnchantmentMap(stack);
			if (!enchantments.containsKey(key)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取物品的附魔
	 *
	 * @return Map集合，key为附魔名，value为附魔等级
	 */
	private static Map<ResourceLocation, Short> getEnchantmentMap(ItemStack stack) {
		NBTTagList enchantmentNbtList = stack.getEnchantmentTagList();
		List<NBTTagCompound> enchantmentNbts = new ArrayList<>();
		for (NBTBase nbtBase : enchantmentNbtList) {
			enchantmentNbts.add((NBTTagCompound) nbtBase);
		}
		Map<ResourceLocation, Short> enchantments = new HashMap<>();
		for (NBTTagCompound enchantmentNbt : enchantmentNbts) {
			Enchantment enchantment = Enchantment.getEnchantmentByID(enchantmentNbt.getShort("id"));
			short lvl = enchantmentNbt.getShort("lvl");
			ResourceLocation name;
			if (enchantment == null) {
				continue;
			}
			name = enchantment.getRegistryName();
			enchantments.put(name, lvl);
		}
		return enchantments;
	}
}
