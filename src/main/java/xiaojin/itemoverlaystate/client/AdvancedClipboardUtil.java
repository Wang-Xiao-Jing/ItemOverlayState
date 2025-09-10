package xiaojin.itemoverlaystate.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentString;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class AdvancedClipboardUtil {
	
	/**
	 * 将字符串复制到系统剪贴板（带回调）
	 *
	 * @param text     要复制的字符串
	 * @param callback 复制完成后的回调函数
	 */
	public static void copyToClipboard(String text, ClipboardCallback callback) {
		boolean success = copyToClipboard(text);
		if (callback != null) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			if (success) {
				callback.onSuccess(text);
				sendMessage(player, "复制物品NBT成功：" + text);
			} else {
				callback.onError(text);
				sendMessage(player, "复制物品NBT失败");
			}
		}
	}
	
	/**
	 * 将字符串复制到系统剪贴板（带错误处理）
	 *
	 * @param text 要复制的字符串
	 * @return 复制是否成功
	 */
	public static boolean copyToClipboard(String text) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		try {
			// 检查是否在无头环境中运行
			if (GraphicsEnvironment.isHeadless()) {
				sendMessage(player, "无法在无头环境中访问剪贴板");
				return false;
			}
			
			// 创建StringSelection对象
			StringSelection stringSelection = new StringSelection(text);
			
			// 获取系统剪贴板
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			
			// 将字符串设置到剪贴板
			clipboard.setContents(stringSelection, null);
			sendMessage(player, "复制物品NBT成功：" + text);
			return true;
		} catch (HeadlessException e) {
			sendMessage(player, "复制物品NBT失败：无法访问剪贴板: " + e.getMessage());
			return false;
		} catch (IllegalStateException e) {
			sendMessage(player, "复制物品NBT失败：无法设置剪贴板内容: " + e.getMessage());
			return false;
		} catch (Exception e) {
			sendMessage(player, "复制物品NBT失败：复制到剪贴板时发生未知错误: " + e.getMessage());
			return false;
		}
	}
	
	private static void sendMessage(EntityPlayerSP player, String text) {
		player.sendMessage(new TextComponentString(text));
	}
	
	// 回调接口
	public interface ClipboardCallback {
		void onSuccess(String text);
		
		void onError(String text);
	}
}
