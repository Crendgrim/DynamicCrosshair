package mod.crend.dynamiccrosshair.style;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public abstract class AbstractCrosshairStyle {
	public final Identifier identifier;
	public AbstractCrosshairStyle(Identifier identifier) {
		this.identifier = identifier;
	}

	abstract public boolean isCustom();

	abstract public void draw(DrawContext context, int x, int y);
}
