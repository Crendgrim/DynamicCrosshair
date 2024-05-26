package mod.crend.dynamiccrosshair.style;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

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
	public void draw(DrawContext context, int x, int y) {
		context.drawTexture(identifier, x, y, 0, 0, 15, 15, 15, 15);
	}

	public String getName() {
		return name;
	}
}
