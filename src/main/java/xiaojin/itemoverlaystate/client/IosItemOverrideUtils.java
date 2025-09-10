package xiaojin.itemoverlaystate.client;

import com.google.gson.JsonElement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 物品堆叠纹理覆盖工具类
 */
@SideOnly(Side.CLIENT)
public class IosItemOverrideUtils {
	/**
	 * 百分比判断
	 */
	public static final Pattern PERCENTAGE     = Pattern.compile("^ *-?\\d+(\\.\\d+)?% *$");
	/**
	 * 提取数字
	 */
	public static final Pattern EXTRACT_NUMBER = Pattern.compile(" *-?\\d+(\\.\\d+)? *");
	
	/**
	 * 判断给定的键是否为STI命名空间
	 *
	 * @param key 要检查的键
	 * @return 如果是STI命名空间返回true，否则返回false
	 */
	public static boolean isIos(String key) {
		return new ResourceLocation(key).getNamespace().equals("ios");
	}
	
	/**
	 * 根据数字判断是否满足条件
	 *
	 * @param element JSON元素
	 * @param count   数量
	 * @return 如果条件不满足返回false，满足返回true
	 */
	public static boolean numericalJudgment(JsonElement element, int count) {
		return element.getAsJsonPrimitive().isNumber() && count >= element.getAsJsonPrimitive().getAsInt();
	}
	
	/**
	 * 根据百分比判断是否满足条件
	 *
	 * @param element  JSON元素
	 * @param count    数量
	 * @param maxCount 最大数量
	 * @return 如果条件不满足返回false，满足返回true
	 */
	public static boolean percentageJudgment(JsonElement element, float count, int maxCount) {
		if (!element.getAsJsonPrimitive().isString()) {
			return false;
		}
		String value = element.getAsString().toLowerCase().trim();
		if (!PERCENTAGE.matcher(value).matches()) {
			return false;
		}
		return count / maxCount >= getAFloat(value) / 100;
	}
	
	/**
	 * 获取一个字符串中的数字
	 *
	 * @param value 字符串
	 * @return 数字
	 */
	public static float getAFloat(String value) {
		// 提取数字
		Matcher matcher = EXTRACT_NUMBER.matcher(value);
		if (matcher.find()) {
			return Float.parseFloat(matcher.group());
		} else {
			return 0;
		}
	}
}
