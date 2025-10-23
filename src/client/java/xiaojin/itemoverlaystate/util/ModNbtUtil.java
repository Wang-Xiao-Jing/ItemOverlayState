package xiaojin.itemoverlaystate.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xiaojin.itemoverlaystate.IosMain;

public class ModNbtUtil {
	public static CompoundTag getDataComponentsNbt(DataComponentMap map, HolderLookup.Provider provider) {
		var component = new CompoundTag();
		for (var entry : map) {
			getDataComponentsNbt(provider, entry, component);
		}
		return component;
	}

	public static <T, D extends DataComponentType<T>> void getDataComponentsNbt(HolderLookup.Provider provider, TypedDataComponent<T> entry, CompoundTag compound) {
		var type = (D) entry.type();
		var key = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type);
		var tCodec = type.codec();
		if (key == null || tCodec == null) {
			return;
		}
		var value = entry.value();
		try {
			compound.put(key.toString(), tCodec.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), value).getOrThrow());
		} catch (IllegalStateException e) {
			IosMain.LOGGER.error("Failed to encode DataComponentType: {}", entry, e);
		}
	}

	public static  CompoundTag getDataComponentsNbt(ItemStack item, RegistryAccess provider) {
		return ModNbtUtil.getDataComponentsNbt(item.getComponents(), provider);
	}

	public static  CompoundTag getItemNbt(ItemStack item, HolderLookup.Provider provider) {
		return (CompoundTag) item.saveOptional(provider);
	}

	public static  CompoundTag getDefaultDataComponentsNbt(ItemStack item, HolderLookup.Provider provider) {
		return getDataComponentsNbt(item.getPrototype(), provider);
	}

	public static  CompoundTag getExtraDataComponentsNbt(ItemStack item, HolderLookup.Provider provider) {
		return getDataComponentsNbt(PatchedDataComponentMap.fromPatch(DataComponentMap.builder().build(),
				item.getComponentsPatch()), provider);
	}
}
