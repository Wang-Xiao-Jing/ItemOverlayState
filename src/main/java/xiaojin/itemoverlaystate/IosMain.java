package xiaojin.itemoverlaystate;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xiaojin.itemoverlaystate.client.event.ItemOverridePredicateEvent;
import xiaojin.itemoverlaystate.client.override.*;

import static xiaojin.itemoverlaystate.client.key.GetTheHandheldItemInformation.GET_THE_HANDHELD_ITEM_INFORMATION;

@Mod(value = IosMain.IOS_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = IosMain.IOS_ID, value = Dist.CLIENT)
public class IosMain {
	public static final String IOS_ID = "itemoverlaystate";
	public static final Logger LOGGER = LogManager.getLogger(IOS_ID);

	public IosMain(IEventBus modEventBus, ModContainer modContainer) {
		modEventBus.addListener(this::onClientSetup);
	}

	private void onClientSetup(FMLClientSetupEvent event) {
		IosItemOverride.init();
		IosMain.LOGGER.info("Item Overlay State is loaded");
	}

	public static ResourceLocation modRL(String name) {
		return ResourceLocation.fromNamespaceAndPath(IOS_ID, name);
	}

	@SubscribeEvent
	public static void registerBindings(RegisterKeyMappingsEvent event) {
		event.register(GET_THE_HANDHELD_ITEM_INFORMATION.get());
	}

	/**
	 * 注册物品物品覆盖谓词条件
	 */
	@SubscribeEvent
	public static void registerPredicates(ItemOverridePredicateEvent.NewOverridePredicate event) {
		event.registerIosPredicate("stacking", ItemStackConditionChecker::stacking);
		event.registerIosPredicate("damage", ItemStackConditionChecker::damage);
		event.registerIosPredicate("nbt", NBTConditionChecker::nbt);
		event.registerIosPredicate("include_nbt", NBTConditionChecker::includeNbt);
		event.registerIosPredicate("components", NBTConditionChecker::components);
		event.registerIosPredicate("include_components", NBTConditionChecker::includeComponents);
		event.registerIosPredicate("enchantment", EnchantmentConditionChecker::enchantment);
		event.registerIosPredicate("include_enchantment", EnchantmentConditionChecker::includeEnchantment);
		event.registerIosPredicate("name", ItemStackConditionChecker::name);
		event.registerIosPredicate("day", WorldConditionChecker::day);
		event.registerIosPredicate("time", WorldConditionChecker::time);
	}
}
