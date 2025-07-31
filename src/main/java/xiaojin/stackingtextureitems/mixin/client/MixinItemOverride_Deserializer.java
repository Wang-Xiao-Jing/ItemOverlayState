package xiaojin.stackingtextureitems.mixin.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xiaojin.stackingtextureitems.client.StiItemOverride;
import xiaojin.stackingtextureitems.mixinimod.client.IModItemOverride;

import java.lang.reflect.Type;
import java.util.Map;

import static xiaojin.stackingtextureitems.client.StiItemOverride.isSti;

@Mixin(targets = "net.minecraft.client.renderer.block.model.ItemOverride$Deserializer")
public abstract class MixinItemOverride_Deserializer implements JsonDeserializer<ItemOverride> {
	@Shadow
	protected abstract Map<ResourceLocation, Float> makeMapResourceValues(JsonObject p_188025_1_);
	
	// 对不支持的的属性实行跳过
	@Redirect(method = "makeMapResourceValues", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/JsonUtils;getFloat(Lcom/google/gson/JsonElement;Ljava/lang/String;)F"))
	private float sti$makeMapResourceValues$redirectGetFloat(JsonElement jsonElement, String key) {
		if (isSti(key)) {
			return 0.0f;
		}
		return JsonUtils.getFloat(jsonElement, key);
	}
	
	@Inject(at = @At("HEAD"), method = "deserialize*", cancellable = true)
	private void sti$deserialize(JsonElement element, Type type, JsonDeserializationContext j, CallbackInfoReturnable<ItemOverride> cir) {
		JsonObject jsonobject = element.getAsJsonObject();
		Map<ResourceLocation, String> stringMap = StiItemOverride.makeMapResourceValues(jsonobject);
		Map<ResourceLocation, Float> floatMap = makeMapResourceValues(jsonobject);
		ResourceLocation locationIn = new ResourceLocation(JsonUtils.getString(jsonobject, "model"));
		IModItemOverride itemOverride = (IModItemOverride) new ItemOverride(locationIn, floatMap);
		itemOverride.setMapResourceValues(stringMap);
		cir.setReturnValue((ItemOverride) itemOverride);
//		StiItemOverride.deserialize(element, makeMapResourceValues(jsonobject), cir);
	}
}
