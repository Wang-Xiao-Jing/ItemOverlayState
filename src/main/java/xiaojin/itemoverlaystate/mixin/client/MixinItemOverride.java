package xiaojin.itemoverlaystate.mixin.client;

import com.google.gson.JsonElement;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xiaojin.itemoverlaystate.client.override.IosItemOverride;
import xiaojin.itemoverlaystate.mixinimod.client.IModItemOverride;

import java.util.Map;

import static xiaojin.itemoverlaystate.client.override.IosItemOverride.test;

@SideOnly(Side.CLIENT)
@Mixin(ItemOverride.class)
public abstract class MixinItemOverride implements IModItemOverride {
	@Shadow
	public abstract ResourceLocation getLocation();

	@Unique
	private Map<ResourceLocation, JsonElement> ios$mapResourceValues; // 新匹配属性Map集

	@Unique
	@Override
	public Map<ResourceLocation, JsonElement> ios$getMapResourceValues() {
		return ios$mapResourceValues;
	}

	@Unique
	@Override
	public void ios$setMapResourceValues(Map<ResourceLocation, JsonElement> ios$mapResourceValues) {
		this.ios$mapResourceValues = ios$mapResourceValues;
	}

	@Inject(at = @At("HEAD"), method = "matchesItemStack", cancellable = true)
	private void ios$matchesItemStack(ItemStack stack, World worldIn, EntityLivingBase livingEntity, CallbackInfoReturnable<Boolean> cir) {
		if (ios$mapResourceValues == null || ios$mapResourceValues.isEmpty()) {
			return;
		}

		Item item = stack.getItem();
		for (Map.Entry<ResourceLocation, JsonElement> entry : ios$mapResourceValues.entrySet()) {
			ResourceLocation key = entry.getKey();
			JsonElement value = entry.getValue();

			if (IosItemOverride.isPredicate(key)) {
				if (test(getLocation(), key, stack, worldIn, livingEntity, value)) {
					continue;
				}

				cir.setReturnValue(false);
				return;
			} else if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
				continue;
			}
			IItemPropertyGetter iitempropertygetter = item.getPropertyGetter(key);
			if (iitempropertygetter == null || iitempropertygetter.apply(stack, worldIn, livingEntity) < value.getAsFloat()) {
				cir.setReturnValue(false);
				return;
			}
		}

		cir.setReturnValue(true);
	}
}
