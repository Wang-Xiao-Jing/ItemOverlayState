package xiaojin.stackingtextureitems.client;

import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xiaojin.stackingtextureitems.mixinimod.client.IModItemOverride;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 物品堆叠纹理覆盖处理类
 * 
 * <p>该类用于根据物品堆叠数量来决定使用哪种模型纹理，支持多种比较操作符</p>
 * 
 * <h2>支持的比较操作符</h2>
 * <ul>
 *   <li>{@code <}  - 小于</li>
 *   <li>{@code <=} - 小于等于</li>
 *   <li>{@code >}  - 大于</li>
 *   <li>{@code >=} - 大于等于</li>
 *   <li>{@code =}  - 等于</li>
 *   <li>{@code !=} - 不等于</li>
 * </ul>
 * 
 * <h2>支持的逻辑操作符</h2>
 * <ul>
 *   <li>{@code &} - 与（AND）操作符，所有条件都必须满足</li>
 *   <li>{@code |} - 或（OR）操作符，任一条件满足即可</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>
 * {@code
 * {
 *   "parent": "item/generated",
 *   "textures": {
 *     "layer0": "items/apple"
 *   },
 *   "overrides": [
 *     {
 *       "predicate": {
 *         "sti:stacking": "=2"
 *       },
 *       "model": "item/apple_1"
 *     },
 *     {
 *       "predicate": {
 *         "sti:stacking": ">3 & <10"
 *       },
 *       "model": "item/apple_2"
 *     },
 *     {
 *       "predicate": {
 *         "sti:stacking": "<3 | >10"
 *       },
 *       "model": "item/apple_3"
 *     }
 *   ]
 * }
 * }
 * </pre>
 */
@SideOnly(Side.CLIENT)
public class StiItemOverride {
	/**
	 * 匹配堆叠条件的正则表达式模式
	 * <p>支持格式如: "=5"、">3"、"<10 & >=5"、"<3 | >10" 等</p>
	 */
	public static final Pattern IS_STACKING    = Pattern.compile("^ *(?:[<>]=?|!=|=) *\\d+ *((?:[&|] *(?:[<>]=?|!=|=) *\\d+)* *)*$");
	
	/**
	 * 提取数字的正则表达式模式
	 */
	public static final Pattern EXTRACT_NUMBER = Pattern.compile(" *\\d+ *");
	
	/**
	 * 小于比较模式
	 */
	public static final Pattern LT_PATTERN     = Pattern.compile("^ *< *\\d+ *$");
	
	/**
	 * 大于比较模式
	 */
	public static final Pattern GT_PATTERN     = Pattern.compile("^ *> *\\d+ *$");
	
	/**
	 * 小于等于比较模式
	 */
	public static final Pattern LE_PATTERN     = Pattern.compile("^ *<= *\\d+ *$");
	
	/**
	 * 大于等于比较模式
	 */
	public static final Pattern GE_PATTERN     = Pattern.compile("^ *>= *\\d+ *$");
	
	/**
	 * 不等于比较模式
	 */
	public static final Pattern NE_PATTERN     = Pattern.compile("^ *!= *\\d+ *$");
	
	/**
	 * 等于比较模式
	 */
	public static final Pattern EQ_PATTERN     = Pattern.compile("^ *= *\\d+ *$");
	
	/**
	 * STI命名空间前缀匹配模式
	 */
	public static final Pattern STI            = Pattern.compile("^sti:");
	
	/**
	 * 根据物品堆叠数量判断是否满足条件
	 *
	 * <p>支持的条件格式:</p>
	 * <ul>
	 *   <li>单个条件: {@code =5}、{@code >3}、{@code <10} 等</li>
	 *   <li>与条件: {@code >3 & <10} (表示数量大于3且小于10)</li>
	 *   <li>或条件: {@code <3 | >10} (表示数量小于3或大于10)</li>
	 *   <li>混合条件: {@code >3 & <10 | =15} (表示数量大于3且小于10，或者等于15)</li>
	 * </ul>
	 *
	 * <p>注意：与操作符(&)的优先级高于或操作符(|)</p>
	 *
	 * <h3>JSON配置示例</h3>
	 * <pre>
	 * {@code
	 * {
	 *   "parent": "item/generated",
	 *   "textures": {
	 *     "layer0": "items/apple"
	 *   },
	 *   "overrides": [
	 *     {
	 *       "predicate": {
	 *         "sti:stacking": "=2"
	 *       },
	 *       "model": "item/apple_1"
	 *     },
	 *     {
	 *       "predicate": {
	 *         "sti:stacking": ">3 & <10"
	 *       },
	 *       "model": "item/apple_2"
	 *     },
	 *     {
	 *       "predicate": {
	 *         "sti:stacking": "<3 | >10"
	 *       },
	 *       "model": "item/apple_3"
	 *     }
	 *   ]
	 * }
	 * }
	 * </pre>
	 *
	 * @param stack 要判断的物品堆
	 * @param map 包含条件的键值对条目
	 * @return 如果条件不满足返回true，满足返回false
	 */
	public static boolean stacking(ItemStack stack, Map.Entry<ResourceLocation, String> map) {
		String value = map.getValue().toLowerCase().trim();
		if (!IS_STACKING.matcher(value).matches()) {
			return true;
		}
		
		int count = stack.getCount();
		
		// 先处理或条件(|)
		String[] orConditions = value.split("\\|");
		// 如果只有一个部分，说明没有或条件，直接处理
		if (orConditions.length == 1) {
			return checkAndConditions(count, orConditions[0]);
		}
		
		// 如果有或条件，任一满足即可
		for (String orCondition : orConditions) {
			if (!checkAndConditions(count, orCondition)) {
				return false; // 任一OR条件满足就返回false（表示条件匹配）
			}
		}
		return true; // 所有OR条件都不满足，返回true
	}
	
	/**
	 * 检查与条件(&)
	 *
	 * @param count 物品数量
	 * @param conditionStr 条件字符串
	 * @return 如果条件不满足返回true，满足返回false
	 */
	private static boolean checkAndConditions(int count, String conditionStr) {
		// 处理多个与条件（用&连接）
		String[] conditions = conditionStr.split("&");
		for (String condition : conditions) {
			if (condition == null || condition.isEmpty()) {
				return true;
			}
			
			try {
				Matcher numberMatcher = EXTRACT_NUMBER.matcher(condition);
				if (!numberMatcher.find()) {
					return true;
				}
				int target = Integer.parseInt(numberMatcher.group());
				
				if (LT_PATTERN.matcher(condition).matches()) {
					if (!(count < target)) {
						return true;
					}
				} else if (GT_PATTERN.matcher(condition).matches()) {
					if (!(count > target)) {
						return true;
					}
				} else if (LE_PATTERN.matcher(condition).matches()) {
					if (!(count <= target)) {
						return true;
					}
				} else if (GE_PATTERN.matcher(condition).matches()) {
					if (!(count >= target)) {
						return true;
					}
				} else if (NE_PATTERN.matcher(condition).matches()) {
					if (count == target) {
						return true;
					}
				} else if (EQ_PATTERN.matcher(condition).matches()) {
					if (!(count == target)) {
						return true;
					}
				}
				// 如果条件匹配，则继续检查下一个条件
			} catch (Exception e) {
				return false;
			}
		}
		return false; // 所有AND条件都满足
	}
	
	/**
	 * 判断给定的键是否为STI命名空间
	 * 
	 * @param key 要检查的键
	 * @return 如果是STI命名空间返回true，否则返回false
	 */
	public static boolean isSti(String key) {
		return STI.matcher(key).matches() || new ResourceLocation(key).getNamespace().equals("sti");
	}
	
	/**
	 * 从JSON对象中提取STI相关的资源值映射
	 * 
	 * @param jsonObject 包含predicate属性的JSON对象
	 * @return STI资源位置到字符串值的映射
	 */
	public static Map<ResourceLocation, String> makeMapResourceValues(JsonObject jsonObject) {
		Map<ResourceLocation, String> map = Maps.newLinkedHashMap();
		JsonObject jsonobject = JsonUtils.getJsonObject(jsonObject, "predicate");
		for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
			String key = entry.getKey();
			if (!isSti(key)) {
				continue;
			}
			map.put(new ResourceLocation(key), JsonUtils.getString(entry.getValue(), key));
		}
		
		return map;
	}
}
