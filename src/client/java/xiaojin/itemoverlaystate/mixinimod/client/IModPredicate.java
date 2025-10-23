package xiaojin.itemoverlaystate.mixinimod.client;

import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IModPredicate {

	static IModPredicate of(Object propertyMatcher) {
		return (IModPredicate) propertyMatcher;
	}

	JsonElement ios$getElementValue();

	void ios$setElementValue(JsonElement ios$elementValue);
}
