package mod.crend.dynamiccrosshair.style;

import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import mod.crend.dynamiccrosshairapi.registry.DynamicCrosshairStyles;
import mod.crend.libbamboo.PlatformUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CrosshairStyleManager {
	public static final CrosshairStyleManager INSTANCE = new CrosshairStyleManager();

	public static final TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();

	public static final Path crosshairDirectory = PlatformUtils.resolveConfigFile("crosshairs");

	Map<Identifier, CustomCrosshairStyle> styles = new LinkedHashMap<>();

	private CrosshairStyleManager() { }

	public void init() {
		File directory = crosshairDirectory.toFile();
		directory.mkdirs();
		for (File file : Objects.requireNonNull(directory.listFiles())) {
			registerCustomCrosshair(file);
		}
	}

	public boolean has(Identifier identifier) {
		return styles.containsKey(identifier);
	}
	public boolean isCustomStyle(Identifier identifier) {
		return has(identifier) && get(identifier).isCustom();
	}
	public AbstractCrosshairStyle get(Identifier identifier) {
		if (BuiltinCrosshairStyle.BUILTIN_STYLES.containsKey(identifier)) return BuiltinCrosshairStyle.BUILTIN_STYLES.get(identifier);
		if (has(identifier)) return styles.get(identifier);
		return new CustomCrosshairStyle(identifier, "unknown");
	}
	public CustomCrosshairStyle getCustomStyle(Identifier identifier) {
		return styles.get(identifier);
	}

	public Collection<BuiltinCrosshairStyle> getBuiltinStyles() {
		return BuiltinCrosshairStyle.BUILTIN_STYLES.values();
	}
	public Collection<CustomCrosshairStyle> getCustomStyles() {
		return styles.values();
	}

	public void delete(Identifier identifier) {
		if (has(identifier)) {
			File file = crosshairDirectory.resolve(styles.get(identifier).getName() + ".png").toFile();
			file.delete();
		}
		remove(identifier);
	}
	public void remove(Identifier identifier) {
		styles.remove(identifier);
	}

	public CustomCrosshairStyle create() {
		String name = getFirstUnusedName();

		Identifier identifier = DynamicCrosshairStyles.of("custom-" + name);
		CustomCrosshairStyle style = new CustomCrosshairStyle(identifier, name);
		styles.put(identifier, style);
		return style;
	}

	private String getFirstUnusedName() {
		int i = 0;
		do {
			i++;
		} while (crosshairDirectory.resolve(i + ".png").toFile().exists());
		return Integer.toString(i);
	}

	private void registerCustomCrosshair(File file) {
		String name = file.getName().split("\\.")[0];
		Identifier identifier = DynamicCrosshairStyles.of("custom-" + name);
		styles.put(identifier, new CustomCrosshairStyle(identifier, name));
		try {
			NativeImage nativeImage = NativeImage.read(new FileInputStream(file));
			textureManager.registerTexture(identifier, new NativeImageBackedTexture(nativeImage));
		} catch (IOException ignored) {
		}
	}

	public void save(CustomCrosshairStyle style, BufferedImage image) {
		styles.put(style.identifier, style);
		File file = crosshairDirectory.resolve(style.getName() + ".png").toFile();
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException ignored) {
		}
	}

	public boolean reload(CustomCrosshairStyle editStyle) {
		try {
			File file = crosshairDirectory.resolve(editStyle.name + ".png").toFile();
			if (file.exists()) {
				NativeImage nativeImage = NativeImage.read(new FileInputStream(file));
				textureManager.registerTexture(editStyle.identifier, new NativeImageBackedTexture(nativeImage));
				return true;
			} else {
				textureManager.destroyTexture(editStyle.identifier);
				return false;
			}
		} catch (IOException ignored) {
		}
		return false;
	}

	public void registerTexture(BufferedImage editImage, Identifier identifier) {
		try (FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream()) {
			ImageIO.write(editImage, "PNG", outputStream);
			NativeImage nativeImage = NativeImage.read(new FastByteArrayInputStream(outputStream.array));
			textureManager.registerTexture(identifier, new NativeImageBackedTexture(nativeImage));
		} catch (IOException ignored) {
		}
	}

	public NativeImage getTexture(Identifier identifier) {
		return ((NativeImageBackedTexture) textureManager.getTexture(identifier)).getImage();
	}
}
