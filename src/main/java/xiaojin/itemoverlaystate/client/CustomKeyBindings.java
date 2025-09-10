package xiaojin.itemoverlaystate.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import static net.minecraft.util.EnumHand.MAIN_HAND;
import static xiaojin.itemoverlaystate.client.AdvancedClipboardUtil.copyToClipboard;

public class CustomKeyBindings {
	
	// 定义自定义按键绑定
	public static KeyBinding getTheHandheldItemNbt;
	
	public static void init() {
		// 创建按键绑定
		getTheHandheldItemNbt = new KeyBinding(
				"获取手持物品NBT",
				Keyboard.KEY_F6,
				"key.categories.misc"
		);
		
		// 注册按键绑定
		ClientRegistry.registerKeyBinding(getTheHandheldItemNbt);
	}
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (getTheHandheldItemNbt.isPressed()) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			ItemStack heldItem = player.getHeldItem(MAIN_HAND);
			copyToClipboard(heldItem.serializeNBT().toString());
		}
	}
}
