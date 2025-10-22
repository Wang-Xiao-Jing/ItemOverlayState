package xiaojin.itemoverlaystate.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import xiaojin.itemoverlaystate.IosMain;
import xiaojin.itemoverlaystate.client.gui.screen.ItemInformationScreen;

import static com.mojang.blaze3d.platform.InputConstants.KEY_F6;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = IosMain.IOS_ID, value = Dist.CLIENT)
public class GetTheHandheldItemInformation {
	public static final Lazy<KeyMapping> GET_THE_HANDHELD_ITEM_INFORMATION = Lazy.of(() -> new KeyMapping(
			"key.itemoverlaystate.get_the_handheld_item_information",
			KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, KEY_F6,
			"key.categories.misc"));
	private static final String NBT_KEY = "itemoverlaystate.nbt";
	private static final String ITEM_COMPONENTS_KEY = "itemoverlaystate.item_components";

	@SubscribeEvent
	public static void onKeyInput(InputEvent.Key event) {
		var minecraft = Minecraft.getInstance();
		var player = minecraft.player;
		if (!GET_THE_HANDHELD_ITEM_INFORMATION.get().isDown() || minecraft.screen != null || minecraft.level == null || player == null) {
			return;
		}
		minecraft.setScreen(new ItemInformationScreen(player.getInventory(), player.getItemInHand(InteractionHand.MAIN_HAND)));
	}
}
