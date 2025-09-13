package xiaojin.itemoverlaystate.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xiaojin.itemoverlaystate.mixinimod.IModNBTTagLongArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static net.minecraft.nbt.NBTBase.NBT_TYPES;
import static xiaojin.itemoverlaystate.client.IosItemOverrideUtils.*;

/**
 * 物品堆叠纹理覆盖处理类
 *
 */
@SideOnly(Side.CLIENT)
public class IosItemOverride {
	/**
	 * 判断自定义高级条件
	 *
	 * @return 如果满足自定义高级条件返回true，否则返回false
	 */
	public static boolean judgeCustomAdvancedConditions(ItemStack stack, Map<ResourceLocation, JsonElement> sti$mapResourceValues) {
		// 遍历STI资源值映射，处理自定义堆叠纹理逻辑
		for (Map.Entry<ResourceLocation, JsonElement> entry : sti$mapResourceValues.entrySet()) {
			ResourceLocation key = entry.getKey();
			if (!isIos(key.toString())) {
				continue;
			}
			JsonElement json = entry.getValue();
			if (json == null || json.isJsonNull()) {
				continue;
			}
			switch (key.getPath()) {
				case "stacking":
					return stacking(stack, json);
				case "damage":
					return damage(stack, json);
				case "nbt":
					return nbt(stack, json);
				case "includeNbt":
					return includeNbt(stack, json);
				case "enchantment":
					return enchantment(stack, json);
				case "includeEnchantment":
					return includeEnchantment(stack, json);
				case "name":
					return name(stack, json);
				case "day":
					return day(json);
				case "time":
					return time(json);
				default:
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 根据时间判断是否满足条件
	 * @return 如果条件不满足返回false，满足返回true
	 */
	private static boolean time(JsonElement json) {
		if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isNumber()) {
			return false;
		}
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc.world;
		if (world == null) {
			return false;
		}
		return world.getWorldTime() >= json.getAsLong();
	}
	
	/**
	 * 根据物品名称判断是否满足条件
	 * @return 如果条件不满足返回false，满足返回true
	 */
	private static boolean name(ItemStack stack, JsonElement json) {
		if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
			return false;
		}
		return Pattern.compile(json.getAsString()).matcher(stack.getDisplayName()).matches();
	}
	
	/**
	 * 根据世界天数判断是否满足条件
	 * @return 如果条件不满足返回false，满足返回true
	 */
	private static boolean day(JsonElement json) {
		if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isNumber()) {
			return false;
		}
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc.world;
		if (world == null) {
			return false;
		}
		
		long day = (world.getWorldInfo().getWorldTotalTime() / 24000) + 1;
		return day >= json.getAsLong();
	}
	
	/**
	 * 根据物品堆叠数量判断是否满足条件
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	private static boolean stacking(ItemStack stack, JsonElement json) {
		if (!json.isJsonPrimitive()) {
			return false;
		}
		
		final int count = stack.getCount();
		
		if (!numericalJudgment(json, count)) {
			return percentageJudgment(json, count, stack.getMaxStackSize());
		}
		
		return true;
	}
	
	/**
	 * 根据物品损坏值判断是否满足条件
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	private static boolean damage(ItemStack stack, JsonElement json) {
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
	 * 根据json的值判断物品的NBT
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	private static boolean nbt(ItemStack stack, JsonElement json) {
		NBTTagCompound nbt = stack.serializeNBT();
		return areNBTEquals(nbt, json);
	}
	
	/**
	 * NBT判断
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	private static boolean areNBTEquals(NBTBase itemNbt, JsonElement json) {
		if (json.isJsonArray()) { // JSON数组
			JsonArray array = json.getAsJsonArray();
			// 遍历JSON数组
			for (JsonElement element : array) {
				// 递归判断
				if (!areNBTEquals(itemNbt, element)) {
					return false;
				}
			}
		} else if (json.isJsonObject() && itemNbt instanceof NBTTagCompound itemNbt1) { // JSON对象
			JsonObject object = json.getAsJsonObject();
			// 遍历JSON对象
			for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
				if (!itemNbt1.hasKey(entry.getKey())) {
					return false;
				}
				NBTBase nbt = itemNbt1.getTag(entry.getKey());
				JsonElement value = entry.getValue();
				int typeId = nbt.getId();
				// 如果NBT类型不存在
				if (typeId == -1 || typeId > NBT_TYPES.length) {
					continue;
				}
				// 判断NBT类型
				switch (NBT_TYPES[typeId]) {
					case "END": {
						if (areNBTEqualsAEnd(nbt)) {
							return false;
						}
						break;
					}
					// 字节型 短整型 整型 长整型 单精度浮点型 双精度浮点型
					case "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE": {
						if (areNBTEqualsANumber(value, nbt)) {
							return false;
						}
						break;
					}
					// 字节数组 整型数组 长整型数组
					case "BYTE[]", "INT[]", "LONG[]", "LIST": {
						if (areNBTEqualsAArray(value, nbt)) {
							return false;
						}
						break;
					}
					// 字符串
					case "STRING": {
						if (areNBTEqualsAString(value, nbt)) {
							return false;
						}
						break;
					}
					// 复合标签(对象)
					case "COMPOUND": {
						if (areNBTEqualsACompound(value, nbt)) {
							return false;
						}
						break;
					}
				}
			}
		} else {
			return false;
		}
		return true;
	}
	
	/**
	 * 复合标签判断
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	private static boolean areNBTEqualsACompound(JsonElement value, NBTBase nbt) {
		if (!value.isJsonObject()) {
			return true;
		}
		if (nbt instanceof NBTTagCompound) {
			return !areNBTEquals(nbt, value);
		} else {
			return true;
		}
	}
	
	/**
	 * 字符串判断
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	private static boolean areNBTEqualsAString(JsonElement value, NBTBase nbt) {
		if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
			return true;
		}
		if (nbt instanceof NBTTagString) {
			return !value.getAsString().equals(((NBTTagString) nbt).getString());
		} else {
			return true;
		}
	}
	
	/**
	 * 数组判断
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	private static boolean areNBTEqualsAArray(JsonElement value, NBTBase nbt) {
		if (!value.isJsonArray()) {
			return true;
		}
		if (nbt instanceof NBTTagByteArray tagArray) {
			List<Byte> list = new ArrayList<>();
			for (byte b : tagArray.getByteArray()) {
				list.add(b);
			}
			for (JsonElement element : value.getAsJsonArray()) {
				if (!list.contains(element.getAsByte())) {
					return true;
				}
			}
		} else if (nbt instanceof NBTTagIntArray tagArray) {
			List<Integer> list = new ArrayList<>();
			for (int i : tagArray.getIntArray()) {
				list.add(i);
			}
			for (JsonElement element : value.getAsJsonArray()) {
				if (!list.contains(element.getAsInt())) {
					return true;
				}
			}
		} else if (nbt instanceof NBTTagLongArray tagArray) {
			List<Long> list = new ArrayList<>();
			for (long l : ((IModNBTTagLongArray) tagArray).getLongArray()) {
				list.add(l);
			}
			for (JsonElement element : value.getAsJsonArray()) {
				if (!list.contains(element.getAsLong())) {
					return true;
				}
			}
		} else if (nbt instanceof NBTTagList tagArray) {
			for (NBTBase tag : tagArray) {
				if (!areNBTEquals(tag, value)) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}
	
	/**
	 * 数字判断
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	private static boolean areNBTEqualsANumber(JsonElement value, NBTBase nbt) {
		if (!value.isJsonPrimitive()) {
			return true;
		}
		JsonPrimitive primitive = value.getAsJsonPrimitive();
		if (primitive.isBoolean()) {
			if (nbt instanceof NBTTagByte) {
				return ((NBTTagByte) nbt).getByte() == 0 == primitive.getAsBoolean();
			}
		} else if (primitive.isNumber()) {
			Number number = primitive.getAsNumber();
			if (nbt instanceof NBTTagByte) {
				return ((NBTTagByte) nbt).getByte() < number.byteValue();
			} else if (nbt instanceof NBTTagShort) {
				return ((NBTTagShort) nbt).getShort() < number.shortValue();
			} else if (nbt instanceof NBTTagInt tag) {
				return tag.getInt() < number.intValue();
			} else if (nbt instanceof NBTTagLong tag) {
				return tag.getLong() < number.longValue();
			} else if (nbt instanceof NBTTagFloat tag) {
				return tag.getFloat() < number.floatValue();
			} else if (nbt instanceof NBTTagDouble tag) {
				return tag.getDouble() < number.doubleValue();
			} else {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断NBT是否为END
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	private static boolean areNBTEqualsAEnd(NBTBase nbt) {
		if (nbt instanceof NBTTagEnd tag) {
		} else {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断物品是否包含这些NBT（要全部包含）
	 *
	 * @return 如果包含返回true，否则返回false
	 */
	private static boolean includeNbt(ItemStack stack, JsonElement json) {
		NBTTagCompound nbt = stack.serializeNBT();
		NBTTagCompound tag = nbt.getCompoundTag("tag");
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
			String text = json.getAsString();
			if (!nbt.hasKey(text)) {
				return false;
			} else if (nbt.hasKey("tag")) {
				if (!tag.hasKey(text)) {
					return false;
				}
			} else {
				return false;
			}
		} else if (!json.isJsonArray()) {
			return false;
		}
		for (JsonElement textElement : json.getAsJsonArray()) {
			if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
				continue;
			}
			String text = textElement.getAsString();
			if (!nbt.hasKey(text)) {
				return false;
			} else if (nbt.hasKey("tag")) {
				if (!tag.hasKey(text)) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 根据json的值判断物品的附魔
	 *
	 * @return 如果条件不满足返回false，满足返回true
	 */
	private static boolean enchantment(ItemStack stack, JsonElement json) {
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
	private static boolean includeEnchantment(ItemStack stack, JsonElement json) {
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
