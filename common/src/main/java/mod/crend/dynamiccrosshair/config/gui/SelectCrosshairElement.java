package mod.crend.dynamiccrosshair.config.gui;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import mod.crend.dynamiccrosshair.style.AbstractCrosshairStyle;
import mod.crend.dynamiccrosshair.style.CrosshairStyleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class SelectCrosshairElement extends AbstractWidget implements ParentElement {
	final static int BUTTON_SIZE = 23;
	final static int BUTTON_SPACING = 25;
	final static int BUTTONS_PER_ROW = 9;
	final static int BIG_BUTTON_WIDTH = 45;

	final SelectCrosshairController control;
	Element focusedElement;
	boolean dragging;
	int normalHeight;

	List<Element> childrenInSelectMode = new ArrayList<>();
	List<Element> childrenInEditMode = new ArrayList<>();
	List<Drawable> drawableInSelectMode = new ArrayList<>();
	List<Drawable> drawableInEditMode = new ArrayList<>();
	ButtonWidget addButton;
	ButtonWidget editButton;
	ButtonWidget deleteButton;
	ButtonWidget saveButton;
	ButtonWidget cancelButton;
	DrawCrosshairWidget drawCrosshairWidget;

	@Override
	public boolean canReset() {
		return true;
	}

	public SelectCrosshairElement(SelectCrosshairController selectCrosshairController, Dimension<Integer> widgetDimension) {
		super(widgetDimension);
		normalHeight = widgetDimension.height();
		this.control = selectCrosshairController;
		createButtons();
		toggleButtons();
		updateFromSelectedStyle(control.option().pendingValue());
		control.option().addListener((opt, style) -> updateFromSelectedStyle(style));
	}

	private <T extends Element & Drawable> void addSelectModeChild(T element) {
		childrenInSelectMode.add(element);
		drawableInSelectMode.add(element);
	}
	private <T extends Element & Drawable> void addEditModeChild(T element) {
		childrenInEditMode.add(element);
		drawableInEditMode.add(element);
	}
	private <T extends Element & Drawable> void addChild(T element) {
		addSelectModeChild(element);
		addEditModeChild(element);
	}

	private void createButtons() {
		// total width is 245px
		int x = getDimension().x();
		int y = getDimension().y() + BUTTON_SPACING + 2;

		addButton = ButtonWidget
				.builder(Text.translatable("dynamiccrosshair.add"), button -> {
					control.add();
					toggleButtons();
				})
				.dimensions(x, y, BIG_BUTTON_WIDTH - 2, 20)
				.build();
		editButton = ButtonWidget
				.builder(Text.translatable("dynamiccrosshair.edit"), button -> {
					if (control.isCustomStyle()) {
						control.edit();
						toggleButtons();
					}
				})
				.dimensions(x + BIG_BUTTON_WIDTH, y, BIG_BUTTON_WIDTH - 2, 20)
				.build();
		deleteButton = ButtonWidget
				.builder(Text.translatable("dynamiccrosshair.delete"), button -> {
					if (control.isCustomStyle()) {
						control.delete();
						toggleButtons();
					}
				})
				.dimensions(x + 2 * BIG_BUTTON_WIDTH, y, BIG_BUTTON_WIDTH - 2, 20)
				.build();

		saveButton = ButtonWidget
				.builder(Text.translatable("dynamiccrosshair.save"), button -> {
					control.save();
					toggleButtons();
				})
				.dimensions(x + 3 * BIG_BUTTON_WIDTH, y, BIG_BUTTON_WIDTH - 2, 20)
				.build();
		cancelButton = ButtonWidget
				.builder(Text.translatable("dynamiccrosshair.cancel"), button -> {
					control.cancel();
					toggleButtons();
				})
				.dimensions(x + 4 * BIG_BUTTON_WIDTH, y, BIG_BUTTON_WIDTH - 2, 20)
				.build();
	}
	private void resetButtons() {
		childrenInSelectMode.clear();
		childrenInEditMode.clear();
		drawableInSelectMode.clear();
		drawableInEditMode.clear();
		addChild(addButton);
		addChild(editButton);
		addChild(deleteButton);
		addChild(saveButton);
		addChild(cancelButton);

		List<AbstractCrosshairStyle> styles = control.getButtons();
		int n = 2 + Math.ceilDiv(styles.size(), BUTTONS_PER_ROW);

		setDimension(getDimension().withHeight(normalHeight * n));

		int x = getDimension().x();
		int y = 50;
		drawCrosshairWidget = new DrawCrosshairWidget(Dimension.ofInt(x + 90, y + 10, 45, 45), control);
		addEditModeChild(drawCrosshairWidget);

		int buttonI = 0;
		for (AbstractCrosshairStyle style : styles) {
			addSelectModeChild(new CrosshairButton(control, style, Dimension.ofInt(x, y, BUTTON_SIZE, BUTTON_SIZE)));
			++buttonI;
			x += BUTTON_SPACING;
			if (buttonI == 9) {
				buttonI = 0;
				x = getDimension().x();
				y += BUTTON_SPACING;
			}
		}
	}

	public void toggleButtons() {
		resetButtons();
		boolean editMode = control.editStyle != null;
		addButton.active = !editMode;
		boolean canEditSelectedStyle = !editMode && CrosshairStyleManager.INSTANCE.isCustomStyle(control.option.pendingValue());
		editButton.active = canEditSelectedStyle;
		deleteButton.active = canEditSelectedStyle;
		saveButton.active = editMode;
		cancelButton.active = editMode;
	}

	public void updateFromSelectedStyle(Identifier identifier) {
		boolean isCustomStyle = CrosshairStyleManager.INSTANCE.isCustomStyle(identifier);
		editButton.active = isCustomStyle;
		deleteButton.active = isCustomStyle;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (control.editStyle == null) {
			for (Drawable drawable : drawableInSelectMode) {
				drawable.render(context, mouseX, mouseY, delta);
			}
		} else {
			for (Drawable drawable : drawableInEditMode) {
				drawable.render(context, mouseX, mouseY, delta);
			}
		}
	}

	@Override
	public List<? extends Element> children() {
		return (control.editStyle == null ? childrenInSelectMode : childrenInEditMode);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (Element element : this.children()) {
			if (element.mouseClicked(mouseX, mouseY, button)) {
				this.setFocused(element);
				if (button == GLFW.GLFW_MOUSE_BUTTON_1 || button == GLFW.GLFW_MOUSE_BUTTON_2) {
					this.setDragging(true);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.setDragging(false);
		return this.hoveredElement(mouseX, mouseY).filter(element -> element.mouseReleased(mouseX, mouseY, button)).isPresent();
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return this.getFocused() != null && this.isDragging() && this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean isDragging() {
		return dragging;
	}

	@Override
	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

	@Nullable
	@Override
	public Element getFocused() {
		return focusedElement;
	}

	@Override
	public void setFocused(@Nullable Element focused) {
		if (this.focusedElement != null) {
			this.focusedElement.setFocused(false);
		}

		if (focused != null) {
			focused.setFocused(true);
		}

		this.focusedElement = focused;
	}

	@Nullable
	@Override
	public GuiNavigationPath getFocusedPath() {
		return ParentElement.super.getFocusedPath();
	}

	@Nullable
	@Override
	public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
		return ParentElement.super.getNavigationPath(navigation);
	}
}
