package mod.crend.dynamiccrosshair.style;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.function.Function;

//? if <=1.21.5 {
import net.minecraft.client.render.RenderLayer;
//?} else
/*import com.mojang.blaze3d.pipeline.RenderPipeline;*/

public abstract class AbstractCrosshairStyle {
	public final Identifier identifier;
	public AbstractCrosshairStyle(Identifier identifier) {
		this.identifier = identifier;
	}

	abstract public boolean isCustom();

	abstract public void draw(
			DrawContext context,
			//? if >1.21.5 {
			/*RenderPipeline renderPipeline,
			*///?} else if >=1.21.2
			/*Function<Identifier, RenderLayer> renderLayer,*/
			int x, int y
			//? if >1.21.5
			/*, int color*/
	);
	//? if >1.21.5 {
	/*public void draw(
			DrawContext context,
			RenderPipeline renderPipeline,
			int x, int y
	) {
		draw(context, renderPipeline, x, y, 0xFFFFFFFF);
	}
	*///?}
}
