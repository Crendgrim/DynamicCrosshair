package mod.crend.yaclx.auto;

import dev.isxander.yacl.gui.ImageRenderer;
import mod.crend.yaclx.ItemOrTag;
import mod.crend.yaclx.auto.annotation.DescriptionImage;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class ItemOrTagRenderer implements ImageRenderer {
	ItemOrTag itemOrTag;

	public ItemOrTagRenderer(ItemOrTag itemOrTag) {
		this.itemOrTag = itemOrTag;
	}

	@Override
	public int render(DrawContext graphics, int x, int y, int renderWidth) {
		int dx = 0, dy = 0;
		for (var entry : itemOrTag.getAllItems()) {
			graphics.drawItemWithoutEntity(new ItemStack(entry), x + dx, y + dy);
			dx += 20;
			if (dx + 20 > renderWidth) {
				dx = 0;
				dy += 20;
			}
		}

		return dy + 20;
	}

	@Override
	public void close() { }

	public static class Factory implements DescriptionImage.DescriptionImageRendererFactory<ItemOrTag> {
		@Override
		public ImageRenderer create(ItemOrTag value) {
			return new ItemOrTagRenderer(value);
		}
	}
}
