package xiaojin.itemoverlaystate.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TextComponentTagVisitor;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xiaojin.itemoverlaystate.IosMain;
import xiaojin.itemoverlaystate.client.gui.widget.TextImageButton;
import xiaojin.itemoverlaystate.util.ModNbtUtil;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ItemInformationScreen extends Screen {
	public static final Component TITLE = Component.translatable("itemoverlaystate.item_information_screen");

	private static final ResourceLocation BG_TEXTURE = IosMain.modRL("textures/gui/container/item_information_screen.png");
	private static final ResourceLocation PAGINATION_DEFAULT_TEXT = IosMain.modRL("item_information/pagination/default");
	private static final ResourceLocation PAGINATION_DEFAULT_PRESS_TEXT = IosMain.modRL("item_information/pagination/default_press");
	private static final ResourceLocation PAGINATION_FLOAT_TEXT = IosMain.modRL("item_information/pagination/float");
	private static final ResourceLocation PAGINATION_FLOAT_PRESS_TEXT = IosMain.modRL("item_information/pagination/float_press");
	private static final ResourceLocation PAGINATION_SELECTED_TEXT = IosMain.modRL("item_information/pagination/selected");
	private static final ResourceLocation PAGINATION_SELECTED_FLOAT_TEXT = IosMain.modRL("item_information/pagination/selected_float");
	private static final ResourceLocation PAGINATION_SELECTED_FLOAT_PRESS_TEXT = IosMain.modRL("item_information/pagination/selected_float_press");
	private static final ResourceLocation PAGINATION_SELECTED_PRESS_TEXT = IosMain.modRL("item_information/pagination/selected_press");
	private static final ResourceLocation PAGINATION_SWITCH_TEXT = IosMain.modRL("item_information/pagination/switch");
	private static final ResourceLocation COPY_TEXT = IosMain.modRL("item_information/copy");
	private static final ResourceLocation COPY_PRESS_TEXT = IosMain.modRL("item_information/copy_press");
	private static final WidgetSprites PAGINATION_SPRITES = new WidgetSprites(PAGINATION_DEFAULT_TEXT, PAGINATION_SELECTED_PRESS_TEXT, PAGINATION_SELECTED_TEXT, PAGINATION_SELECTED_PRESS_TEXT);
	private static final WidgetSprites PAGINATION_SWITCH_SPRITES = new WidgetSprites(PAGINATION_SWITCH_TEXT, PAGINATION_SWITCH_TEXT);
	private static final WidgetSprites COPY_SPRITES = new WidgetSprites(COPY_TEXT, COPY_PRESS_TEXT);

	private static final MutableComponent PROVIDER_TEXT_COMPONENT = Component.translatable("item_information_screen.provider.text.component");
	private static final MutableComponent PROVIDER_TEXT_DATA = Component.translatable("item_information_screen.provider.text.data");
	private static final MutableComponent PROVIDER_TEXT_DEFAULT = Component.translatable("item_information_screen.provider.text.default");
	private static final MutableComponent PROVIDER_TEXT_EXTRA = Component.translatable("item_information_screen.provider.text.extra");
	private static final MutableComponent PROVIDER_TEXT_SWITCH = Component.translatable("item_information_screen.provider.text.switch");
	private static final MutableComponent TEXT_COPY = Component.translatable("item_information_screen.text.copy");

	protected final int scissorLeft = 10;
	protected final int scissorTop = 45;
	protected final int scissorRight = 219;
	protected final int scissorBottom = 208;
	protected final int imageWidth = 230;
	protected final int imageHeight = 220;
	private final ItemStack item;
	private final List<Pagination> paginationList;
	protected int leftPos;
	protected int topPos;
	/**
	 * 当前页码
	 */
	private int page;

	public ItemInformationScreen(Container container, ItemStack item) {
		super(TITLE);
		this.item = item;

		this.paginationList = new ArrayList<>(4);
	}

	public static int getColor(String colorText) {
		return TextColor.parseColor(colorText).getOrThrow().getValue();
	}

	private static void displayClientMessage(LocalPlayer player, Component textComponent) {
		player.displayClientMessage(textComponent, false);
	}

	private static Component copyOnClickText(MutableComponent component, Component copyComponent) {
		return copyOnClickText(component, copyComponent, Component.translatable("chat.copy.click"));
	}

	private static Component copyOnClickText(MutableComponent component, Component copyComponent, Component clickComponent) {
		var copyText = copyComponent.toString();
		return ComponentUtils.wrapInSquareBrackets(component.withStyle(style -> style.withColor(ChatFormatting.GREEN)
						.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copyText))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, clickComponent))
						.withInsertion(copyText)))
				.append("\n")
				.append(copyComponent);
	}

	@Override
	protected void init() {
		this.leftPos = (this.width - this.imageWidth) / 2;
		this.topPos = (this.height - this.imageHeight) / 2;

		if (minecraft == null) {
			throw new NullPointerException("minecraft is null");
		}
		if (minecraft.level == null) {
			throw new NullPointerException("level is null");
		}

		paginationList.clear();
		var provider = minecraft.level.registryAccess();
		this.paginationList.add(new Pagination(PROVIDER_TEXT_COMPONENT, this,
				ModNbtUtil.getDataComponentsNbt(item, provider), this.font));
		this.paginationList.add(new Pagination(PROVIDER_TEXT_DATA, this,
				ModNbtUtil.getItemNbt(item, provider), this.font));
		this.paginationList.add(new Pagination(PROVIDER_TEXT_DEFAULT, this,
				ModNbtUtil.getDefaultDataComponentsNbt(item, provider), this.font));
		this.paginationList.add(new Pagination(PROVIDER_TEXT_EXTRA, this,
				ModNbtUtil.getExtraDataComponentsNbt(item, provider), this.font));

		int paginationIndex = 0;
		int paginationImageButtonPoX = leftPos + 11;
		int paginationImageButtonPoY = topPos + 29;
		for (Pagination pagination : paginationList) {
			pagination.init();
			int finalIndex = paginationIndex;
			final var component = pagination.getComponent();
			final int width = font.width(component) + 6;
			addRenderableWidget(TextImageButton.builder()
					.text(font, component, 3)
					.color("#ffffff")
					.build(
							paginationImageButtonPoX, paginationImageButtonPoY,
							width, 14,
							PAGINATION_SPRITES,
							component, button -> setPage(finalIndex)));
			paginationImageButtonPoX += width + 1;
			paginationIndex++;
		}
		renderables.stream()
				.filter(r -> r instanceof TextImageButton)
				.map(r -> (TextImageButton) r)
				.findFirst().get().press = true;

		final var switchWidget = new ImageButton(leftPos - 14, topPos + 29, 16, 17, PAGINATION_SWITCH_SPRITES, button -> {
			page++;
			if (page >= paginationList.size()) {
				page = 0;
			}
			setPage(page);
		});
		switchWidget.setTooltip(Tooltip.create(PROVIDER_TEXT_SWITCH));
		addRenderableWidget(switchWidget);

		final var copyWidget = new ImageButton(leftPos + 207, topPos + 46, 12, 12, COPY_SPRITES,
				button -> this.minecraft.keyboardHandler.setClipboard(getNbtText()));
		copyWidget.setTooltip(Tooltip.create(TEXT_COPY));
		addRenderableWidget(copyWidget);
	}

	public final int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
		var textImageButtonList = renderables.stream()
				.filter(r -> r instanceof TextImageButton)
				.map(r -> (TextImageButton) r).toList();
		textImageButtonList.forEach(button1 -> button1.press = false);
		textImageButtonList.get(page).press = true;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		PoseStack pose = guiGraphics.pose();
		guiGraphics.renderItem(item, leftPos + 8, topPos + 8);
		guiGraphics.drawString(font, item.getHoverName(), leftPos + 28, topPos + 12, getColor("#ffffff"));
		renderNbtText(guiGraphics, pose);
	}

	protected void renderNbtText(GuiGraphics guiGraphics, PoseStack pose) {
		guiGraphics.enableScissor(
				leftPos + scissorLeft,
				topPos + scissorTop,
				leftPos + scissorRight,
				topPos + scissorBottom);
		pose.pushPose();
		final var nbtText = getNbtTextList();
		for (int i = 0, nbtTextSize = nbtText.size(); i < nbtTextSize; i++) {
			int lineHeight = font.lineHeight;
			int y = topPos + getNbtTextPositionY() + i * (lineHeight + 1);
			int minY = topPos + scissorTop - lineHeight;
			int maxY = topPos + scissorBottom + lineHeight;
			if (y < minY || y > maxY) {
				continue;
			}
			guiGraphics.drawString(font, nbtText.get(i), leftPos + getNbtTextPositionX(), y, 0xFFFFFF);
		}
		pose.popPose();
		guiGraphics.disableScissor();
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		boolean is = super.mouseDragged(mouseX, mouseY, button, dragX, dragY);

		// 限制在特定区域拖动
		if (mouseX >= leftPos + scissorLeft && mouseX <= leftPos + scissorRight &&
				mouseY >= topPos + scissorTop && mouseY <= topPos + scissorBottom) {
			if (getNbtTextWidth() > scissorRight - scissorLeft) {
				setNbtTextPositionX(getNbtTextPositionX() + (int) dragX);
			}
			if (getNbtTextHeight() > scissorBottom - scissorTop) {
				setNbtTextPositionY(getNbtTextPositionY() + (int) dragY);
			}
		}
		return is;
	}

	@Override
	public void renderMenuBackground(GuiGraphics guiGraphics) {
		guiGraphics.blit(BG_TEXTURE,
				leftPos, topPos,
				0, 0, 0,
				imageWidth, imageHeight,
				256, 256);
	}

	public ItemStack getStack() {
		return item;
	}

	public List<FormattedCharSequence> getNbtTextList() {
		return getPagination().getNbtTextList();
	}

	public int getNbtTextWidth() {
		return getPagination().getNbtTextWidth();
	}

	public int getNbtTextHeight() {
		return getPagination().getNbtTextHeight();
	}

	public int getNbtTextPositionX() {
		return getPagination().getNbtTextPositionX();
	}

	public void setNbtTextPositionX(int x) {
		getPagination().setNbtTextPositionX(Math.clamp(x, -getNbtTextWidth() + scissorRight, 12));
	}

	public int getNbtTextPositionY() {
		return getPagination().getNbtTextPositionY();
	}

	public void setNbtTextPositionY(int y) {
		getPagination().setNbtTextPositionY(Math.clamp(y, -getNbtTextHeight() + scissorBottom, 47));
	}

	public String getNbtText() {
		return getPagination().getNbtText();
	}

	public Pagination getPagination() {
		return paginationList.get(page);
	}

	public static class Pagination {
		private final List<FormattedCharSequence> nbtTextList;
		private final Component component;
		private final ItemInformationScreen screen;
		private final CompoundTag nbt;
		private final  Font font;
		private final String nbtText;
		private int nbtTextPositionX = 12;
		private int nbtTextPositionY = 47;
		private int nbtTextWidth = 219;
		private int nbtTextHeight = 208;

		public Pagination(Component component, ItemInformationScreen screen, CompoundTag nbt,  Font font) {
			this.component = component;
			this.screen = screen;
			this.nbt = nbt;
			this.font = font;
			var mutableComponent = getMutableComponent(this.nbt);
			this.nbtTextList = toPrettyComponent(mutableComponent);
			nbtText = mutableComponent.getString();
		}

		public  List<FormattedCharSequence> toPrettyComponent(Component text) {
			return ComponentRenderUtils.wrapComponents(text, Integer.MAX_VALUE, font);
		}

		public  Component getMutableComponent(CompoundTag nbt) {
			return new TextComponentTagVisitor("  ").visit(nbt);
		}

		public void init() {
			setNbtTextWidth(nbtTextList.stream().map(font::width).max(Integer::compareTo).get());
			setNbtTextHeight(nbtTextList.size() * (font.lineHeight + 1));
		}

		public int getNbtTextPositionX() {
			return nbtTextPositionX;
		}

		public void setNbtTextPositionX(int x) {
			this.nbtTextPositionX = Math.clamp(x, -nbtTextWidth + screen.scissorRight, screen.scissorLeft + 2);
		}

		public int getNbtTextPositionY() {
			return nbtTextPositionY;
		}

		public void setNbtTextPositionY(int y) {
			this.nbtTextPositionY = Math.clamp(y, -nbtTextHeight + screen.scissorBottom, screen.scissorTop + 2);
		}

		public int getNbtTextWidth() {
			return nbtTextWidth;
		}

		public void setNbtTextWidth(int nbtTextWidth) {
			this.nbtTextWidth = nbtTextWidth;
		}

		public int getNbtTextHeight() {
			return nbtTextHeight;
		}

		public void setNbtTextHeight(int nbtTextHeight) {
			this.nbtTextHeight = nbtTextHeight;
		}

		public CompoundTag getNbt() {
			return nbt;
		}

		public List<FormattedCharSequence> getNbtTextList() {
			return nbtTextList;
		}

		public Component getComponent() {
			return component;
		}

		public String getNbtText() {
			return nbtText;
		}
	}
}
