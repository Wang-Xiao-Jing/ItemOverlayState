package xiaojin.itemoverlaystate.client.override;

import com.google.gson.JsonElement;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xiaojin.itemoverlaystate.client.event.ItemOverridePredicateEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * 物品堆叠纹理覆盖处理类
 *
 */
@SideOnly(Side.CLIENT)
public class IosItemOverride {
	public static final Map<ResourceLocation, Predicate> PREDICATES = new HashMap<>();

	public static void init() {
		MinecraftForge.EVENT_BUS.post(new ItemOverridePredicateEvent.NewOverridePredicate());
	}

	/**
	 * 判断自定义高级条件
	 *
	 * @return 如果满足自定义高级条件返回true，否则返回false
	 */
	public static boolean test(ResourceLocation modelRL, ResourceLocation key, ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase livingEntity, JsonElement json) {
		if (!isPredicate(key) || json == null || json.isJsonNull()) {
			return false;
		}

		return PREDICATES.get(key).test(modelRL, stack, worldIn, livingEntity, json);
	}

	/**
	 * 判断给定的键是否为已注册的谓词
	 *
	 * @param key 要检查的键
	 */
	public static boolean isPredicate(String key) {
		return isPredicate(new ResourceLocation(key));
	}

	public static boolean isPredicate(ResourceLocation key) {
		return PREDICATES.containsKey(key);
	}

	@FunctionalInterface
	public interface Predicate {
		boolean test(ResourceLocation modelRL, ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase livingEntity, JsonElement json);
	}
}
