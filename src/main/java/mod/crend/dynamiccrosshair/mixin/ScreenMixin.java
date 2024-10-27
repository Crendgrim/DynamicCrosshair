package mod.crend.dynamiccrosshair.mixin;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenMixin {
	@Accessor
	List<Element> getChildren();

	@Accessor
	List<Drawable> getDrawables();

	@Accessor
	List<Selectable> getSelectables();
}
