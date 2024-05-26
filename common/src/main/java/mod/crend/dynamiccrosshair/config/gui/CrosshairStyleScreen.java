package mod.crend.dynamiccrosshair.config.gui;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

public class CrosshairStyleScreen extends YACLScreen {

	CrosshairStyleController control;
	PreviewCrosshairWidget previewCrosshairWidget;

	public CrosshairStyleScreen(CrosshairStyleController control, YetAnotherConfigLib config, Screen parent) {
		super(config, parent);
		this.control = control;
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
}
