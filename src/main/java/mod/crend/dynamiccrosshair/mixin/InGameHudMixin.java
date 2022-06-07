package mod.crend.dynamiccrosshair.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.config.CrosshairModifier;
import mod.crend.dynamiccrosshair.config.CrosshairStyle;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=InGameHud.class, priority=900)
public class InGameHudMixin {
    @Inject(method = "renderCrosshair", at = @At(value = "HEAD"), cancellable = true)
    private void dynamiccrosshair$preCrosshair(final MatrixStack matrixStack, final CallbackInfo ci) {
        if (!CrosshairHandler.shouldShowCrosshair()) ci.cancel();
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 0), require = 0)
    private void dynamiccrosshair$drawCrosshair(InGameHud instance, MatrixStack matrixStack, int x, int y, int u, int v, int width, int height) {
        if (DynamicCrosshair.config.isDynamicCrosshairStyle()) {
            Crosshair crosshair = CrosshairHandler.getActiveCrosshair();
            RenderSystem.setShaderTexture(0, CrosshairHandler.crosshairTexture);
            if (crosshair.hasStyle()) {
                CrosshairStyle crosshairStyle = crosshair.getCrosshairStyle();
                instance.drawTexture(matrixStack, x, y, crosshairStyle.getX(), crosshairStyle.getY(), 15, 15);
            }
            for (CrosshairModifier modifier : crosshair.getModifiers()) {
                instance.drawTexture(matrixStack, x, y, modifier.getX(), modifier.getY(), 15, 15);
            }
            RenderSystem.setShaderTexture(0, InGameHud.GUI_ICONS_TEXTURE);
        } else {
            instance.drawTexture(matrixStack, x, y, u, v, width, height);
        }
    }

    @Inject(method = "tick()V", at = @At(value = "TAIL"))
    private void dynamiccrosshair$tickDynamicCrosshair(CallbackInfo ci) {
        CrosshairHandler.tick();
    }

}
