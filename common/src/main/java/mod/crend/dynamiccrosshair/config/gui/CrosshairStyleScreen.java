package mod.crend.dynamiccrosshair.config.gui;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

public class CrosshairStyleScreen extends YACLScreen {

	CrosshairStyleController control;
	SelectCrosshairController nestedControl;
	PreviewCrosshairWidget previewCrosshairWidget;

	public CrosshairStyleScreen(CrosshairStyleController control, YetAnotherConfigLib config, Screen parent) {
		super(config, parent);
		this.control = control;
		this.nestedControl = (SelectCrosshairController) control.styleOption.controller();
	}

	@Override
	protected void init() {
		super.init();
		int scaledSize = (int) (127 / MinecraftClient.getInstance().getWindow().getScaleFactor());
		previewCrosshairWidget = new PreviewCrosshairWidget(Dimension.ofInt(width - width / 6 - scaledSize / 2, (height - scaledSize) / 2, scaledSize, scaledSize), control);
		addDrawableChild(previewCrosshairWidget);
	}

	@Override
	public void close() {
		GLFW.glfwSetInputMode(client.getWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		super.close();
	}

	@Override
	public void finishOrSave() {
		nestedControl.save();
		super.finishOrSave();
	}

	@Override
	public void cancelOrReset() {
		nestedControl.cancel();
		super.cancelOrReset();
	}

	@Override
	public void undo() {
		if (!nestedControl.isInEditMode()) {
			super.undo();
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (tabManager.getCurrentTab() instanceof CategoryTab categoryTab) {
			categoryTab.saveFinishedButton.active = !nestedControl.isInEditMode();
			categoryTab.cancelResetButton.active = !nestedControl.isInEditMode();
			categoryTab.undoButton.active = !nestedControl.isInEditMode() && pendingChanges();
		}
	}
}
