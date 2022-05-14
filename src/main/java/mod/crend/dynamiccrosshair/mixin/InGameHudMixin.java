package mod.crend.dynamiccrosshair.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.component.CrosshairModifier;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderCrosshair", at = @At(value = "HEAD"), cancellable = true)
    private void preCrosshair(final MatrixStack matrixStack, final CallbackInfo ci) {
        if (!CrosshairHandler.shouldShowCrosshair()) ci.cancel();
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 0))
    private void drawCrosshair(InGameHud instance, MatrixStack matrixStack, int x, int y, int u, int v, int width, int height) {
        if (DynamicCrosshair.config.isDynamicCrosshairStyle()) {
            Crosshair crosshair = CrosshairHandler.getActiveCrosshair();
            RenderSystem.setShaderTexture(0, CrosshairHandler.crosshairTexture);
            instance.drawTexture(matrixStack, x, y, crosshair.getX(), crosshair.getY(), 15, 15);
            for (CrosshairModifier modifier : CrosshairHandler.getActiveCrosshairModifiers()) {
                instance.drawTexture(matrixStack, x, y, modifier.getX(), modifier.getY(), 15, 15);
            }
            RenderSystem.setShaderTexture(0, InGameHud.GUI_ICONS_TEXTURE);
        } else {
            instance.drawTexture(matrixStack, x, y, u, v, width, height);
        }
    }

    @Inject(method = "tick()V", at = @At(value = "TAIL"))
    private void tickDynamicCrosshair(CallbackInfo ci) {
        CrosshairHandler.tick();
    }

}
