package xiaojin.itemoverlaystate.mixin.client;

import com.google.gson.JsonElement;
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
import xiaojin.itemoverlaystate.mixinimod.client.IModItemOverride;

import java.util.Map;

import static xiaojin.itemoverlaystate.client.IosItemOverride.judgeCustomAdvancedConditions;


@Mixin(ItemOverride.class)
@Implements(value = @Interface(iface = IModItemOverride.class, prefix = "iosIMod$"))
public abstract class MixinItemOverride {
	@Shadow
	@Final
	private Map<ResourceLocation, Float>       mapResourceValues;
	@Unique
	private Map<ResourceLocation, JsonElement> ios$mapResourceValues; // 新匹配属性Map集
	
	@Unique
	public Map<ResourceLocation, JsonElement> iosIMod$getMapResourceValues() {
		return ios$mapResourceValues;
	}
	
	@Unique
	public void iosIMod$setMapResourceValues(Map<ResourceLocation, JsonElement> ios$mapResourceValues) {
		this.ios$mapResourceValues = ios$mapResourceValues;
	}
	
	@Inject(at = @At("HEAD"), method = "matchesItemStack", cancellable = true)
	private void ios$matchesItemStack(ItemStack stack, World worldIn, EntityLivingBase livingEntity, CallbackInfoReturnable<Boolean> cir) {
		if (mapResourceValues == null || ios$mapResourceValues == null || mapResourceValues.isEmpty() || ios$mapResourceValues.isEmpty()) {
			return;
		}
		
		if (!judgeCustomAdvancedConditions(stack, ios$mapResourceValues)) {
			cir.setReturnValue(false);
			return;
		}
		Item item = stack.getItem();
		for (Map.Entry<ResourceLocation, Float> entry : mapResourceValues.entrySet()) {
			IItemPropertyGetter iitempropertygetter = item.getPropertyGetter(entry.getKey());
			if (ios$mapResourceValues == null && (iitempropertygetter == null || iitempropertygetter.apply(stack, worldIn, livingEntity) < entry.getValue().floatValue())) {
				cir.setReturnValue(false);
				return;
			}
		}
		
		cir.setReturnValue(true);
	}
}
