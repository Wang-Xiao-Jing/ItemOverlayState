package xiaojin.itemoverlaystate.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xiaojin.itemoverlaystate.client.gui.screen.ItemInformationScreen;

@Environment(EnvType.CLIENT)
public class TextImageButton extends ImageButton {
	
	private final Font font;
	
	private final Component text;
	private final int color;
	private final int textMargin;
	public boolean press;

	public TextImageButton(int x, int y,
	                       int width, int height,
	                       WidgetSprites sprites, Component message, OnPress onPress,
	                       Builder builder) {
		super(x, y, width, height, sprites, onPress, message);
		this.font = builder.font;
		this.text = builder.text;
		this.color = builder.color;
		this.textMargin = builder.textMargin;
		setTooltip(builder.tooltip);
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		ResourceLocation resourcelocation = this.sprites.get(!this.isPress(), this.isHoveredOrFocused());
		guiGraphics.blitSprite(resourcelocation, this.getX(), this.getY(), this.width, this.height);
		renderText(guiGraphics);
	}

	public void renderText(GuiGraphics guiGraphics) {
		if (text == null || font == null) {
			return;
		}
		int x = getX() + textMargin;
		int y = getY() + (getHeight() - font.lineHeight) / 2 - (this.isPress() ? -2 : 0);
		guiGraphics.drawString(font, getMessage(),
				x,
				y,
				color);
	}

	public int getTextMargin() {
		return textMargin;
	}

	public Component getText() {
		return text;
	}

	public boolean isPress() {
		return press;
	}

	public static class Builder {
		
		private Component text;
		private Font font;
		private int textMargin = 0;
		
		private Tooltip tooltip;
		private int color;

		public Builder() {
			this.font = Minecraft.getInstance().font;
		}

		public Builder text(Font font, Component component, int textMargin) {
			this.font = font;
			this.text = component;
			this.textMargin = textMargin;
			return this;
		}

		public Builder text(Component text) {
			this.text = text;
			return this;
		}

		public Builder font(Font font) {
			this.font = font;
			return this;
		}

		public Builder textMargin(int textMargin) {
			this.textMargin = textMargin;
			return this;
		}

		public Builder color(int color) {
			this.color = color;
			return this;
		}

		public Builder color(String rgbString) {
			this.color = ItemInformationScreen.getColor(rgbString);
			return this;
		}

		public Builder tooltip( Tooltip tooltip) {
			this.tooltip = tooltip;
			return this;
		}

		public TextImageButton build(int x, int y,
		                             int width, int height,
		                             WidgetSprites sprites, Component message, OnPress onPress) {
			return new TextImageButton(x, y, width, height, sprites, message, onPress, this);
		}
	}
}