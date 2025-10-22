package xiaojin.itemoverlaystate.mixinimod.client;

import com.google.gson.JsonElement;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IModPredicate {

	static IModPredicate of(Object propertyMatcher) {
		return (IModPredicate) propertyMatcher;
	}

	JsonElement ios$getElementValue();

	void ios$setElementValue(JsonElement ios$elementValue);
}
