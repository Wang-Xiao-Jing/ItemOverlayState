package xiaojin.itemoverlaystate;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xiaojin.itemoverlaystate.client.event.ItemOverridePredicateEvent;
import xiaojin.itemoverlaystate.client.key.CustomKeyBindings;
import xiaojin.itemoverlaystate.client.override.*;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID, value = Side.CLIENT)
@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, clientSideOnly = true)
public class IosMain {
	public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if (!event.getSide().isClient()) {
			return;
		}
		IosItemOverride.init();
		IosMain.LOGGER.info("Item Overlay State is loaded");
	}

	public static ResourceLocation modRL(String name) {
		return new ResourceLocation(Tags.MOD_ID, name);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		if (!event.getSide().isClient()) {
			return;
		}
		CustomKeyBindings.init();
		MinecraftForge.EVENT_BUS.register(new CustomKeyBindings());
	}

	@SubscribeEvent
	public static void registerPredicates(ItemOverridePredicateEvent.NewOverridePredicate event) {
		event.registerIosPredicate("stacking", ItemStackConditionChecker::stacking);
		event.registerIosPredicate("damage", ItemStackConditionChecker::damage);
		event.registerIosPredicate("nbt", NBTConditionChecker::nbt);
		event.registerIosPredicate("include_nbt", NBTConditionChecker::includeNbt);
		event.registerIosPredicate("enchantment", EnchantmentConditionChecker::enchantment);
		event.registerIosPredicate("include_enchantment", EnchantmentConditionChecker::includeEnchantment);
		event.registerIosPredicate("name", ItemStackConditionChecker::name);
		event.registerIosPredicate("day", WorldConditionChecker::day);
		event.registerIosPredicate("time", WorldConditionChecker::time);
	}
}
