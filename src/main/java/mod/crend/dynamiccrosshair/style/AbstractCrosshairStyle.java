package mod.crend.dynamiccrosshair.style;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public abstract class AbstractCrosshairStyle {
	public final Identifier identifier;
	public AbstractCrosshairStyle(Identifier identifier) {
		this.identifier = identifier;
	}

	abstract public boolean isCustom();

	abstract public void draw(
			DrawContext context,
			//? if >=1.21.2
			/*Function<Identifier, RenderLayer> renderLayer,*/
			int x, int y
	);
}
