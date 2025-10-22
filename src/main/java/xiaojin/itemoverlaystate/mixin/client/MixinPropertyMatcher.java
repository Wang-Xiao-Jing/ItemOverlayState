package xiaojin.itemoverlaystate.mixin.client;

import com.google.gson.JsonElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import xiaojin.itemoverlaystate.mixinimod.client.IModPropertyMatcher;

@Mixin(ItemOverrides.PropertyMatcher.class)
public abstract class MixinPropertyMatcher implements IModPropertyMatcher {
	@Mutable
	@Unique
	@Final
	private JsonElement ios$elementValue;
	@Mutable
	@Unique
	@Final
	private ResourceLocation ios$name;

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

	@Unique
	@Override
	public ResourceLocation ios$getName() {
		return ios$name;
	}

	@Unique
	@Override
	public void ios$setName(ResourceLocation ios$name) {
		this.ios$name = ios$name;
	}
}

