package xiaojin.itemoverlaystate;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IosMain implements ModInitializer {
	public static final String IOS_ID = "itemoverlaystate";
	public static final Logger LOGGER = LogManager.getLogger(IOS_ID);

	@Override
	public void onInitialize() {
	}

	public static ResourceLocation modRL(String name) {
		return ResourceLocation.fromNamespaceAndPath(IOS_ID, name);
	}
}
