package mod.crend.dynamiccrosshair.style;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

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
			//? if >=1.21.2
			/*Function<Identifier, RenderLayer> renderLayer,*/
			int x, int y
	) {
		context.drawTexture(
				//? if >=1.21.2
				/*renderLayer,*/
				identifier,
				x, y,
				0, 0,
				15, 15,
				15, 15
		);
	}

	public String getName() {
		return name;
	}
}
