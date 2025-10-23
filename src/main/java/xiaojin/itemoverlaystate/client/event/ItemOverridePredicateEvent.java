package xiaojin.itemoverlaystate.client.event;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xiaojin.itemoverlaystate.client.override.IosItemOverride;

@SideOnly(Side.CLIENT)
public abstract class ItemOverridePredicateEvent extends Event {

	@SideOnly(Side.CLIENT)
	public static class NewOverridePredicate extends ItemOverridePredicateEvent {
		public void registerPredicate(ResourceLocation predicateName, IosItemOverride.Predicate predicateTest) {
			if (IosItemOverride.PREDICATES.containsKey(predicateName)) {
				throw new IllegalArgumentException(String.format("Predicate name '%s' is already register.", predicateName));
			}
			IosItemOverride.PREDICATES.put(predicateName, predicateTest);
		}

		public void registerPredicate(String id, String predicateName, IosItemOverride.Predicate predicateTest) {
			registerPredicate(new ResourceLocation(id, predicateName), predicateTest);
		}

		public void registerIosPredicate(String predicateName, IosItemOverride.Predicate predicateTest) {
			registerPredicate("ios", predicateName, predicateTest);
		}
	}
}
