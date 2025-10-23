package xiaojin.itemoverlaystate.mixinimod.client;

import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public interface IModPropertyMatcher {

	static IModPropertyMatcher of(Object propertyMatcher) {
		return (IModPropertyMatcher) propertyMatcher;
	}

	JsonElement ios$getElementValue();

	void ios$setElementValue(JsonElement ios$elementValue);

	ResourceLocation ios$getName();

	void ios$setName(ResourceLocation ios$name);
}
