package xiaojin.itemoverlaystate.mixin;

import net.minecraft.nbt.NBTTagLongArray;
import org.spongepowered.asm.mixin.*;
import xiaojin.itemoverlaystate.mixinimod.IModNBTTagLongArray;

@Mixin(NBTTagLongArray.class)
@Implements(value = @Interface(iface = IModNBTTagLongArray.class, prefix = "iosIMod$"))
public class MixinNBTTagLongArray {
	@Shadow
	private long[] data;
	
	@Unique
	public long[] iosIMod$getLongArray() {
		return data;
	}
}
