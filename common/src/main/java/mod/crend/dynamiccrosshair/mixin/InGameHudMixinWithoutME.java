package mod.crend.dynamiccrosshair.mixin;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.render.CrosshairRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value=InGameHud.class, priority=700)
public class InGameHudMixinWithoutME {

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowDebugHud()Z"))
    private boolean dynamiccrosshair$debugCrosshair(DebugHud instance) {
        if (DynamicCrosshair.config.isDisableDebugCrosshair()) return false;
        return instance.shouldShowDebugHud();
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V", ordinal = 0))
    private void dynamiccrosshair$drawCrosshair(DrawContext context, Identifier texture, int x, int y, int width, int height) {
        if (!CrosshairHandler.forceShowCrosshair && !CrosshairHandler.shouldShowCrosshair()) return;

        // Set up color first (and clean it up after) so that we can tint the vanilla crosshair even when dynamic style is off
        CrosshairRenderer.preRender();

        if (DynamicCrosshair.config.isFixCenteredCrosshair()) {
            CrosshairRenderer.fixCenteredCrosshairPre(context, x, y);
        }
        if (DynamicCrosshair.config.isDynamicCrosshairStyle()) {
            CrosshairRenderer.render(context, x, y);
        } else {
            context.drawGuiTexture(texture, x, y, width, height);
        }
        if (DynamicCrosshair.config.isFixCenteredCrosshair()) {
            CrosshairRenderer.fixCenteredCrosshairPost(context);
        }
        CrosshairRenderer.postRender();
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getPerspective()Lnet/minecraft/client/option/Perspective;"), require = 0)
    private Perspective dynamiccrosshair$thirdPersonCrosshair(GameOptions instance) {
        Perspective originalPerspective = instance.getPerspective();
        if (originalPerspective == Perspective.THIRD_PERSON_BACK && DynamicCrosshair.config.isThirdPersonCrosshair()) {
            return Perspective.FIRST_PERSON;
        }
        return originalPerspective;
    }
}
