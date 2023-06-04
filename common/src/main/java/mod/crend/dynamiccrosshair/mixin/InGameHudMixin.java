package mod.crend.dynamiccrosshair.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.render.CrosshairRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=InGameHud.class, priority=1010)
public class InGameHudMixin {
    @Inject(method = "renderCrosshair", at = @At(value = "HEAD"), cancellable = true)
    private void dynamiccrosshair$preCrosshair(DrawContext context, final CallbackInfo ci) {
        if (!CrosshairHandler.shouldShowCrosshair()) ci.cancel();
    }

    @ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z"))
    private boolean dynamiccrosshair$debugCrosshair(boolean original) {
        if (DynamicCrosshair.config.isDisableDebugCrosshair()) return false;
        return original;
    }

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 0))
    private void dynamiccrosshair$drawCrosshair(DrawContext context, Identifier texture, int x, int y, int u, int v, int width, int height, Operation<Void> original) {
        // Set up color first (and clean it up after) so that we can tint the vanilla crosshair even when dynamic style is off
        CrosshairRenderer.preRender();
        if (DynamicCrosshair.config.isDynamicCrosshairStyle()) {
            CrosshairRenderer.render(context, x, y);
        } else {
            original.call(context, texture, x, y, u, v, width, height);
        }
        CrosshairRenderer.postRender();
    }

    @ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getPerspective()Lnet/minecraft/client/option/Perspective;"), require = 0)
    private Perspective dynamiccrosshair$thirdPersonCrosshair(Perspective originalPerspective) {
        if (originalPerspective == Perspective.THIRD_PERSON_BACK && DynamicCrosshair.config.isThirdPersonCrosshair()) {
            return Perspective.FIRST_PERSON;
        }
        return originalPerspective;
    }

    @Inject(method = "tick()V", at = @At(value = "TAIL"))
    private void dynamiccrosshair$tickDynamicCrosshair(CallbackInfo ci) {
        CrosshairHandler.tick();
    }

}
