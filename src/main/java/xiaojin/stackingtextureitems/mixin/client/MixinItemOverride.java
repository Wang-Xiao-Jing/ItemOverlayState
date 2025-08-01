package xiaojin.stackingtextureitems.mixin.client;

import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xiaojin.stackingtextureitems.mixinimod.client.IModItemOverride;

import java.util.Map;

import static xiaojin.stackingtextureitems.client.StiItemOverride.isSti;
import static xiaojin.stackingtextureitems.client.StiItemOverride.stacking;

@Mixin(ItemOverride.class)
@Implements(value = @Interface(iface = IModItemOverride.class, prefix = "stiIMod$"))
public abstract class MixinItemOverride implements IModItemOverride {
	@Shadow
	@Final
	private Map<ResourceLocation, Float>  mapResourceValues;
	@Unique
	private Map<ResourceLocation, String> sti$mapResourceValues;
	
	@Unique
	public Map<ResourceLocation, String> stiIMod$getMapResourceValues() {
		return sti$mapResourceValues;
	}
	
	@Unique
	public void stiIMod$setMapResourceValues(Map<ResourceLocation, String> sti$mapResourceValues) {
		this.sti$mapResourceValues = sti$mapResourceValues;
	}
	
	@Inject(at = @At("HEAD"), method = "matchesItemStack", cancellable = true)
	private void sti$matchesItemStack(ItemStack stack, World worldIn, EntityLivingBase livingEntity, CallbackInfoReturnable<Boolean> cir) {
		if (mapResourceValues == null || mapResourceValues.isEmpty()) {
			return;
		}
		
		for (Map.Entry<ResourceLocation, String> entry : sti$mapResourceValues.entrySet()) {
			ResourceLocation key = entry.getKey();
			if (!isSti(key.toString())) {
				continue;
			}
			String path = key.getPath();
			if (path.equals("stacking")) {
				if (stacking(stack, entry)) {
					cir.setReturnValue(false);
					return;
				}
			}
		}
		Item item = stack.getItem();
		for (Map.Entry<ResourceLocation, Float> entry : mapResourceValues.entrySet()) {
			IItemPropertyGetter iitempropertygetter = item.getPropertyGetter(entry.getKey());
			if (sti$mapResourceValues == null && (iitempropertygetter == null || iitempropertygetter.apply(stack, worldIn, livingEntity) < entry.getValue().floatValue())) {
				cir.setReturnValue(false);
				return;
			}
		}
		cir.setReturnValue(true);
	}
}
