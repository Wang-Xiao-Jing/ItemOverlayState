package xiaojin.itemoverlaystate.mixinimod.client;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IModPropertyMatcher {

	static IModPropertyMatcher of(Object propertyMatcher) {
		return (IModPropertyMatcher) propertyMatcher;
	}

	JsonElement ios$getElementValue();

	void ios$setElementValue(JsonElement ios$elementValue);

	ResourceLocation ios$getName();

	void ios$setName(ResourceLocation ios$name);
}
