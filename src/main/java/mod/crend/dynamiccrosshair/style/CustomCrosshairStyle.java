package mod.crend.dynamiccrosshair.style;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

//? if <=1.21.5 {
import net.minecraft.client.render.RenderLayer;
//?} else
/*import com.mojang.blaze3d.pipeline.RenderPipeline;*/

import java.util.function.Function;

public class CustomCrosshairStyle extends AbstractCrosshairStyle {
	protected String name;

	public CustomCrosshairStyle(Identifier identifier, String name) {
		super(identifier);
		this.name = name;
	}

	@Override
	public boolean isCustom() {
		return true;
	}

	@Override
	public void draw(
			DrawContext context,
			//? if >1.21.5 {
			/*RenderPipeline renderLayer,
			*///?} else if >=1.21.2
			/*Function<Identifier, RenderLayer> renderLayer,*/
			int x, int y
			//? if >1.21.5
			/*, int color*/
	) {
		context.drawTexture(
				//? if >=1.21.2
				/*renderLayer,*/
				identifier,
				x, y,
				0, 0,
				15, 15,
				15, 15
				//? if >1.21.5
				/*, color*/
		);
	}

	public String getName() {
		return name;
	}
}
