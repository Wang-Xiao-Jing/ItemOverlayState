package xiaojin.itemoverlaystate.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;
import xiaojin.itemoverlaystate.client.gui.screen.ItemInformationScreen;

import static com.mojang.blaze3d.platform.InputConstants.KEY_F6;

public class KeyBindings {
	public static final KeyMapping GET_THE_HANDHELD_ITEM_INFORMATION = new KeyMapping(
			"key.itemoverlaystate.get_the_handheld_item_information",
			InputConstants.Type.KEYSYM, KEY_F6,
			"key.categories.misc");

	public static void init(){
		KeyBindingHelper.registerKeyBinding(KeyBindings.GET_THE_HANDHELD_ITEM_INFORMATION);
		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
			while (GET_THE_HANDHELD_ITEM_INFORMATION.isDown()) {
				var player = minecraft.player;
				if (minecraft.screen != null || minecraft.level == null || player == null) {
					minecraft.setScreen(new ItemInformationScreen(player.getInventory(), player.getItemInHand(InteractionHand.MAIN_HAND)));
				}
			}
		});
	}
}
