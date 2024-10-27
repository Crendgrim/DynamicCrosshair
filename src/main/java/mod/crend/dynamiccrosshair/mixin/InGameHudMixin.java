package mod.crend.dynamiccrosshair.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.render.CrosshairRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value=InGameHud.class, priority=700)
public class InGameHudMixin {

    @ModifyExpressionValue(method = "renderCrosshair",
            //? if <1.20.6 {
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z")
            //?} else {
            /*at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowDebugHud()Z")
            *///?}
    )
    private boolean dynamiccrosshair$debugCrosshair(boolean original) {
        if (DynamicCrosshairMod.config.isDisableDebugCrosshair()) return false;
        return original;
    }

    @WrapOperation(method = "renderCrosshair", at = @At(
            value = "INVOKE",
            //? if <1.20.6 {
            target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V",
            //?} else {
            /*target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V",
			*///?}
            ordinal = 0))
    private void dynamiccrosshair$drawCrosshair(DrawContext context, Identifier texture, int x, int y, /*? if <1.20.6 {*/ int u, int v,/*?}*/ int width, int height, Operation<Void> original) {
        if (!CrosshairHandler.forceShowCrosshair && !CrosshairHandler.shouldShowCrosshair()) return;

        // Set up color first (and clean it up after) so that we can tint the vanilla crosshair even when dynamic identifier is off
        CrosshairRenderer.preRender();

        if (DynamicCrosshairMod.config.isFixCenteredCrosshair()) {
            CrosshairRenderer.fixCenteredCrosshairPre(context, x, y);
        }
        if (DynamicCrosshairMod.config.isDynamicCrosshairStyle()) {
            CrosshairRenderer.render(context, x, y);
        } else {
            original.call(context, texture, x, y, /*? if <1.20.6 {*/u, v,/*?}*/ width, height);
        }
        if (DynamicCrosshairMod.config.isFixCenteredCrosshair()) {
            CrosshairRenderer.fixCenteredCrosshairPost(context);
        }
        CrosshairRenderer.postRender();
    }

    @ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getPerspective()Lnet/minecraft/client/option/Perspective;"), require = 0)
    private Perspective dynamiccrosshair$thirdPersonCrosshair(Perspective originalPerspective) {
        if (originalPerspective == Perspective.THIRD_PERSON_BACK && DynamicCrosshairMod.config.isThirdPersonCrosshair()) {
            return Perspective.FIRST_PERSON;
        }
        return originalPerspective;
    }
}
