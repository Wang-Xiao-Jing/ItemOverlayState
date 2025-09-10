package xiaojin.itemoverlaystate.mixinimod.client;

import com.google.gson.JsonElement;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public interface IModItemOverride {
	
	Map<ResourceLocation, JsonElement> getMapResourceValues();
	
	void setMapResourceValues(Map<ResourceLocation, JsonElement> sti$mapResourceValues);
}
