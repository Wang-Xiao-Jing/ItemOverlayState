package xiaojin.itemoverlaystate.client.event;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import xiaojin.itemoverlaystate.IosMain;
import xiaojin.itemoverlaystate.client.override.IosItemOverride;

@OnlyIn(Dist.CLIENT)
public abstract class ItemOverridePredicateEvent extends Event implements IModBusEvent {

	@OnlyIn(Dist.CLIENT)
	public static class NewOverridePredicate extends ItemOverridePredicateEvent {
		public void registerPredicate(ResourceLocation predicateName, IosItemOverride.Predicate predicateTest) {
			if (IosItemOverride.PREDICATES.containsKey(predicateName)) {
				IosMain.LOGGER.warn("Predicate name '{}' is already register.", predicateName);
				return;
			}
			IosItemOverride.PREDICATES.put(predicateName, predicateTest);
		}

		public void registerPredicate(String id,String predicateName, IosItemOverride.Predicate predicateTest) {
			registerPredicate(ResourceLocation.fromNamespaceAndPath(id, predicateName), predicateTest);
		}

		public void registerIosPredicate(String predicateName, IosItemOverride.Predicate predicateTest) {
			registerPredicate(ResourceLocation.fromNamespaceAndPath("ios", predicateName), predicateTest);
		}
	}
}
