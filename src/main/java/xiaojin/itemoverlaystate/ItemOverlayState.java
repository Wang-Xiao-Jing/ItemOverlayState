package xiaojin.itemoverlaystate;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xiaojin.itemoverlaystate.client.CustomKeyBindings;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class ItemOverlayState {
	public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);
	
	/**
	 * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
	 * Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
	 * </a>
	 */
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LOGGER.info("Hello From {}!", Tags.MOD_NAME);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		if (event.getSide() == Side.CLIENT) {
			CustomKeyBindings.init();
			MinecraftForge.EVENT_BUS.register(new CustomKeyBindings());
		}
	}
}
