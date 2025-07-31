package xiaojin.stackingtextureitems.mixinimod.client;

import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public interface IModItemOverride {
	
	Map<ResourceLocation, String> getMapResourceValues();
	
	void setMapResourceValues(Map<ResourceLocation, String> sti$mapResourceValues);
}
