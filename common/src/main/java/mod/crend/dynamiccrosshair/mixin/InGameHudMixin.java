package mod.crend.dynamiccrosshair.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.config.CrosshairColor;
import mod.crend.dynamiccrosshair.config.CrosshairModifier;
import mod.crend.dynamiccrosshair.config.CrosshairStyle;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.math.MatrixStack;
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

    private void dynamiccrosshair$setColor(final CrosshairColor color) {
        int argb = color.getColor();
        // convert ARGB hex to r, g, b, a floats
        RenderSystem.setShaderColor(((argb >> 16) & 0xFF) / 255.0f, ((argb >> 8) & 0xFF) / 255.0f, (argb & 0xFF) / 255.0f, ((argb >> 24) & 0xFF) / 255.0f);
        if (color.forced()) {
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        } else {
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        }
    }

    @ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z"))
    private boolean dynamiccrosshair$debugCrosshair(boolean original) {
        if (DynamicCrosshair.config.isDisableDebugCrosshair()) return false;
        return original;
    }

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 0))
    private void dynamiccrosshair$drawCrosshair(DrawContext context, Identifier texture, int x, int y, int u, int v, int width, int height, Operation<Void> original) {
        dynamiccrosshair$setColor(DynamicCrosshair.config.getColor());
        if (DynamicCrosshair.config.isDynamicCrosshairStyle()) {
            Crosshair crosshair = CrosshairHandler.getActiveCrosshair();
            if (crosshair.hasStyle()) {
                CrosshairStyle crosshairStyle = crosshair.getCrosshairStyle();
                dynamiccrosshair$setColor(crosshairStyle.getColor());
                context.drawTexture(CrosshairHandler.crosshairTexture, x, y, crosshairStyle.getStyle().getX(), crosshairStyle.getStyle().getY(), 15, 15);
            }
            for (CrosshairModifier modifier : crosshair.getModifiers()) {
                dynamiccrosshair$setColor(modifier.getColor());
                context.drawTexture(CrosshairHandler.crosshairTexture, x, y, modifier.getStyle().getX(), modifier.getStyle().getY(), 15, 15);
            }
        } else {
            original.call(context, texture, x, y, u, v, width, height);
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
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
