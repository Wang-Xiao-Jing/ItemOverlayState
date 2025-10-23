package xiaojin.itemoverlaystate.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import xiaojin.itemoverlaystate.IosMain;
import xiaojin.itemoverlaystate.client.event.ItemOverridePredicateEvent;
import xiaojin.itemoverlaystate.client.key.KeyBindings;
import xiaojin.itemoverlaystate.client.override.*;


@Environment(EnvType.CLIENT)
public class IosMainClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		IosItemOverride.init();
		IosMain.LOGGER.info("Item Overlay State is loaded");
		KeyBindings.init();
		registerPredicates();
	}

	/**
	 * 注册物品物品覆盖谓词条件
	 */
	public static void registerPredicates() {
		ItemOverridePredicateEvent.NewOverridePredicate.EVENT.register(event1 -> {
			event1.registerIosPredicate("stacking", ItemStackConditionChecker::stacking);
			event1.registerIosPredicate("damage", ItemStackConditionChecker::damage);
			event1.registerIosPredicate("nbt", NBTConditionChecker::nbt);
			event1.registerIosPredicate("include_nbt", NBTConditionChecker::includeNbt);
			event1.registerIosPredicate("components", NBTConditionChecker::components);
			event1.registerIosPredicate("include_components", NBTConditionChecker::includeComponents);
			event1.registerIosPredicate("enchantment", EnchantmentConditionChecker::enchantment);
			event1.registerIosPredicate("include_enchantment", EnchantmentConditionChecker::includeEnchantment);
			event1.registerIosPredicate("name", ItemStackConditionChecker::name);
			event1.registerIosPredicate("day", WorldConditionChecker::day);
			event1.registerIosPredicate("time", WorldConditionChecker::time);
		});
	}
}
