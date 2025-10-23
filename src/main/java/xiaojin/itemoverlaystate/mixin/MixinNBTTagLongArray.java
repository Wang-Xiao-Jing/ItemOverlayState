package xiaojin.itemoverlaystate.mixin;

import net.minecraft.nbt.NBTTagLongArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import xiaojin.itemoverlaystate.mixinimod.IModNBTTagLongArray;

@Mixin(NBTTagLongArray.class)
public abstract class MixinNBTTagLongArray implements IModNBTTagLongArray {
	@Shadow
	private long[] data;

	@Unique
	@Override
	public long[] ios$getLongArray() {
		return data;
	}
}
