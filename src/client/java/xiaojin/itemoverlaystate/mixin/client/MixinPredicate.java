package xiaojin.itemoverlaystate.mixin.client;

import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.ItemOverride;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import xiaojin.itemoverlaystate.mixinimod.client.IModPredicate;

@Environment(EnvType.CLIENT)
@Mixin(ItemOverride.Predicate.class)
public abstract class MixinPredicate implements IModPredicate {
	@Mutable
	@Unique
	@Final
	private JsonElement ios$elementValue;

	@Unique
	@Override
	public JsonElement ios$getElementValue() {
		return ios$elementValue;
	}

	@Unique
	@Override
	public void ios$setElementValue(JsonElement ios$elementValue) {
		this.ios$elementValue = ios$elementValue;
	}
}
