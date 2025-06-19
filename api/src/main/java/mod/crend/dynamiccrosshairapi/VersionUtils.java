package mod.crend.dynamiccrosshairapi;

import net.minecraft.util.Identifier;

//? if >1.21.5 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
*///?} else if >=1.21.2 {
/*import net.minecraft.client.render.RenderLayer;
import java.util.function.Function;
*///?}

public class VersionUtils {
	public static Identifier getIdentifier(String identifier) {
		//? if <1.21 {
		return new Identifier(identifier);
		//?} else {
		/*return Identifier.of(identifier);
		 *///?}
	}

	public static Identifier getIdentifier(String namespace, String path) {
		//? if <1.21 {
		return new Identifier(namespace, path);
		//?} else {
		/*return Identifier.of(namespace, path);
		 *///?}
	}

	public static Identifier getVanillaIdentifier(String path) {
		//? if <1.21 {
		return new Identifier(Identifier.DEFAULT_NAMESPACE, path);
		//?} else {
		/*return Identifier.ofVanilla(path);
		 *///?}
	}

	//? if >1.21.5 {
	/*public static RenderPipeline getGuiTextured() {
		return RenderPipelines.GUI_TEXTURED;
	}
	public static RenderPipeline getCrosshair() {
		return RenderPipelines.CROSSHAIR;
	}
	*///?} else if >=1.21.2 {
	/*public static Function<Identifier, RenderLayer> getGuiTextured() {
		return RenderLayer::getGuiTextured;
	}
	public static Function<Identifier, RenderLayer> getCrosshair() {
		return RenderLayer::getCrosshair;
	}
	*///?}
}
