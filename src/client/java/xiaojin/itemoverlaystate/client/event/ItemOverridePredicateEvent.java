package xiaojin.itemoverlaystate.client.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import xiaojin.itemoverlaystate.IosMain;
import xiaojin.itemoverlaystate.client.override.IosItemOverride;

@Environment(EnvType.CLIENT)
public final class ItemOverridePredicateEvent {
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

	@Environment(EnvType.CLIENT)
	public interface NewOverridePredicate {
		Event<NewOverridePredicate> EVENT = EventFactory.createArrayBacked(NewOverridePredicate.class, listeners -> event -> {
			for (NewOverridePredicate listener : listeners) {
				listener.register(event);
			}
		});

		void register(ItemOverridePredicateEvent event);
	}
}
