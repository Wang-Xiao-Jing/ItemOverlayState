package xiaojin.itemoverlaystate.client.override;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xiaojin.itemoverlaystate.util.ModNbtUtil;

import java.util.ArrayList;

/**
 * 检查NBT数据相关条件的工具类
 */
@Environment(EnvType.CLIENT)
public class NBTConditionChecker {
	/**
	 * 检查物品NBT数据是否满足条件
	 */
	public static boolean nbt(BakedModel model, ItemStack stack,  ClientLevel level,  LivingEntity livingEntity, JsonElement json, int seed) {
		if (level == null || !json.isJsonObject()) {
			return true;
		}

		final var nbt = ModNbtUtil.getItemNbt(stack, level.registryAccess());
		for (var textElement : json.getAsJsonObject().entrySet()) {
			if (!nbt.contains(textElement.getKey())){
				return false;
			}
		}

		return areNBTEquals(nbt, json);
	}

	/**
	 * 检查物品NBT是否包含指定NBT
	 */
	public static boolean includeNbt(BakedModel model, ItemStack stack,  ClientLevel level,  LivingEntity livingEntity, JsonElement json, int seed) {
		if (level == null || (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) && !json.isJsonArray()) {
			return true;
		}

		final var nbt = ModNbtUtil.getItemNbt(stack, level.registryAccess());
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
			return nbt.contains(json.getAsString());
		} else if (json.isJsonArray()) {
			for (JsonElement textElement : json.getAsJsonArray()) {
				if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
					continue;
				}
				if (!nbt.contains(textElement.getAsString())){
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 检查物品是否具有指定的数据组件及数据
	 */
	public static boolean components(BakedModel model, ItemStack stack,  ClientLevel level,  LivingEntity livingEntity, JsonElement json, int seed) {
		if (level == null || !json.isJsonObject()) {
			return true;
		}

		// 判断是否包含数据组件
		for (var jsonElement : json.getAsJsonObject().entrySet()) {
			var key = BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.parse(jsonElement.getKey()));
			if (key == null) {
				continue;
			}
			if (!stack.getComponents().has(key)) {
				return false;
			}
		}

		return areNBTEquals(ModNbtUtil.getDataComponentsNbt(stack, level.registryAccess()), json);
	}

	/**
	 * 检查物品是否包含指定的数据组件（包含检查）
	 */
	public static boolean includeComponents(BakedModel model, ItemStack stack,  ClientLevel level,  LivingEntity livingEntity, JsonElement json, int seed) {
		if (!json.isJsonPrimitive() || !json.isJsonArray()) {
			return true;
		}

		final var components = stack.getComponents();
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
			var key = BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.parse(json.getAsString()));
			if (key == null) {
				return true;
			}
			return components.has(key);
		} else if (json.isJsonArray()) {
			for (var jsonElement : json.getAsJsonArray()) {
				if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
					continue;
				}
				var key = BuiltInRegistries.DATA_COMPONENT_TYPE.get(ResourceLocation.parse(jsonElement.getAsString()));
				if (key == null) {
					continue;
				}
				if (!components.has(key)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * 比较NBT数据与JSON数据是否相等
	 */
	public static boolean areNBTEquals(Tag itemNbt, JsonElement json) {
		if (json.isJsonArray()) { // JSON数组
			var array = json.getAsJsonArray();
			// 遍历JSON数组
			for (var element : array) {
				// 递归判断
				if (!areNBTEquals(itemNbt, element)) {
					return false;
				}
			}
		} else if (json.isJsonObject() && itemNbt instanceof CompoundTag itemNbt1) { // JSON对象
			var object = json.getAsJsonObject();
			// 遍历JSON对象
			for (var entry : object.entrySet()) {
				final var key = entry.getKey();
				if (!itemNbt1.contains(key)) {
					return false;
				}
				var nbt = itemNbt1.get(key);
				var value = entry.getValue();
				// 判断NBT类型
				switch (nbt) {
					case EndTag endTag -> {
						if (areNBTEqualsAEnd(endTag)) {
							return false;
						}
					}
					case NumericTag numericTag -> {
						if (areNBTEqualsANumber(value, numericTag)) {
							return false;
						}
					}
					case CollectionTag<?> collectionTag -> {
						if (areNBTEqualsAArray(value, collectionTag)) {
							return false;
						}
					}
					case StringTag stringTag -> {
						if (areNBTEqualsAString(value, stringTag)) {
							return false;
						}
					}
					case CompoundTag compoundTag -> {
						if (areNBTEqualsACompound(value, compoundTag)) {
							return false;
						}
					}
					case null, default -> {
					}
				}
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * 比较复合标签
	 */
	private static boolean areNBTEqualsACompound(JsonElement value, CompoundTag nbt) {
		return !value.isJsonObject() || !areNBTEquals(nbt, value);
	}

	/**
	 * 比较字符串标签
	 */
	private static boolean areNBTEqualsAString(JsonElement value, StringTag nbt) {
		return !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString() || !value.getAsString().equals(nbt.getAsString());
	}

	/**
	 * 比较数组标签
	 */
	private static boolean areNBTEqualsAArray(JsonElement value, CollectionTag<?> nbt) {
		if (!value.isJsonArray()) {
			return true;
		}
		switch (nbt) {
			case ByteArrayTag tagArray -> {
				var list = new ArrayList<>();
				for (byte b : tagArray.getAsByteArray()) {
					list.add(b);
				}
				for (JsonElement element : value.getAsJsonArray()) {
					if (!list.contains(element.getAsByte())) {
						return true;
					}
				}
			}
			case IntArrayTag tagArray -> {
				var list = new ArrayList<>();
				for (var i : tagArray.getAsIntArray()) {
					list.add(i);
				}
				for (var element : value.getAsJsonArray()) {
					if (!list.contains(element.getAsInt())) {
						return true;
					}
				}
			}
			case LongArrayTag tagArray -> {
				var list = new ArrayList<>();
				for (var l : tagArray.getAsLongArray()) {
					list.add(l);
				}
				for (var element : value.getAsJsonArray()) {
					if (!list.contains(element.getAsLong())) {
						return true;
					}
				}
			}
			case ListTag tagArray -> {
				for (var tag : tagArray) {
					if (!areNBTEquals(tag, value)) {
						return true;
					}
				}
			}
			case null, default -> {
				return true;
			}
		}
		return false;
	}

	/**
	 * 比较数值标签
	 */
	private static boolean areNBTEqualsANumber(JsonElement value, NumericTag nbt) {
		if (!value.isJsonPrimitive()) {
			return true;
		}
		JsonPrimitive primitive = value.getAsJsonPrimitive();
		if (primitive.isBoolean()) {
			if (nbt instanceof ByteTag tag) {
				return tag.getAsByte() == 0 == primitive.getAsBoolean();
			}
		} else if (primitive.isNumber()) {
			final var number = primitive.getAsNumber();
			return switch (nbt) {
				case ByteTag tag -> tag.getAsByte() < number.byteValue();
				case ShortTag tag -> tag.getAsShort() < number.shortValue();
				case IntTag tag -> tag.getAsInt() < number.intValue();
				case LongTag tag -> tag.getAsLong() < number.longValue();
				case FloatTag tag -> tag.getAsFloat() < number.floatValue();
				case DoubleTag tag -> tag.getAsDouble() < number.doubleValue();
				case null, default -> true;
			};
		} else {
			return true;
		}
		return false;
	}

	/**
	 * 检查是否为END标签
	 */
	private static boolean areNBTEqualsAEnd(EndTag nbt) {
		return false;
	}
}