package mod.crend.dynamiccrosshair.config.gui;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import mod.crend.dynamiccrosshair.registry.DynamicCrosshairStyles;
import mod.crend.dynamiccrosshair.style.AbstractCrosshairStyle;
import mod.crend.dynamiccrosshair.style.CustomCrosshairStyle;
import mod.crend.dynamiccrosshair.style.CrosshairStyleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectCrosshairController implements Controller<Identifier> {

	public final TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
	Option<Identifier> option;
	CustomCrosshairStyle editStyle = null;
	BufferedImage editImage;

	public SelectCrosshairController(Option<Identifier> option) {
		this.option = option;
	}

	@Override
	public Option<Identifier> option() {
		return this.option;
	}

	@Override
	public Text formatValue() {
		return Text.empty();
	}

	public List<AbstractCrosshairStyle> getButtons() {
		List<AbstractCrosshairStyle> buttons = new ArrayList<>();
		buttons.addAll(CrosshairStyleManager.INSTANCE.getBuiltinStyles());
		buttons.addAll(CrosshairStyleManager.INSTANCE.getCustomStyles());
		return buttons;
	}

	public boolean isInEditMode() {
		return editStyle != null;
	}

	public boolean isCustomStyle() {
		return CrosshairStyleManager.INSTANCE.isCustomStyle(option.pendingValue());
	}

	public void add() {
		editStyle = CrosshairStyleManager.INSTANCE.create();
		init(true);
		option.requestSet(editStyle.identifier);
	}

	public void edit() {
		if (!isInEditMode() && isCustomStyle()) {
			editStyle = CrosshairStyleManager.INSTANCE.getCustomStyle(option.pendingValue());
			init(false);
		}
	}

	public void delete() {
		if (!isInEditMode() && isCustomStyle()) {
			CrosshairStyleManager.INSTANCE.delete(option.pendingValue());
			option.requestSet(DynamicCrosshairStyles.DEFAULT);
		}
	}

	public void save() {
		if (isInEditMode() && isCustomStyle()) {
			CrosshairStyleManager.INSTANCE.save(editStyle, editImage);
		}
		editStyle = null;
	}

	public void cancel() {
		if (isInEditMode()) {
			if (!CrosshairStyleManager.INSTANCE.reload(editStyle)) {
				CrosshairStyleManager.INSTANCE.remove(option.pendingValue());
				option.requestSet(DynamicCrosshairStyles.DEFAULT);
			}
		}
		editStyle = null;
	}


	public void registerTexture() {
		if (isInEditMode()) {
			try (FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream()) {
				ImageIO.write(editImage, "PNG", outputStream);
				NativeImage nativeImage = NativeImage.read(new FastByteArrayInputStream(outputStream.array));
				textureManager.registerTexture(editStyle.identifier, new NativeImageBackedTexture(nativeImage));
			} catch (IOException ignored) {
			}
		}
	}

	public void init(boolean newImage) {
		editImage = new BufferedImage(15, 15, BufferedImage.TYPE_INT_ARGB);
		if (newImage) registerTexture();
		else loadCurrentTexture();
	}

	private void loadCurrentTexture() {
		if (editStyle != null) {
			NativeImage nativeImage = ((NativeImageBackedTexture) textureManager.getTexture(editStyle.identifier)).getImage();
			if (nativeImage != null) {
				int[] rgba = nativeImage.copyPixelsRgba();
				int i = 0;
				int j = 0;
				for (int px : rgba) {
					editImage.setRGB(i, j, px);
					++i;
					if (i == 15) {
						i = 0;
						++j;
					}
				}
			}
		}
	}

	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new SelectCrosshairElement(this, widgetDimension);
	}
}
