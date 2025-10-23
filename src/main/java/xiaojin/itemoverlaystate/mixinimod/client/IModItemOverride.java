package xiaojin.itemoverlaystate.mixinimod.client;

import com.google.gson.JsonElement;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public interface IModItemOverride {
	static IModItemOverride of(ItemOverride o) {
		return (IModItemOverride) o;
	}

	Map<ResourceLocation, JsonElement> ios$getMapResourceValues();

	void ios$setMapResourceValues(Map<ResourceLocation, JsonElement> ios$mapResourceValues);
}
