package mod.crend.dynamiccrosshair.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.render.CrosshairRenderer;
import mod.crend.dynamiccrosshairapi.registry.DynamicCrosshairStyles;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

// Apply after AutoHud, so we wrap around its crosshair render
@Mixin(value=InGameHud.class, priority=900)
public class InGameHudMixin {

    //? if >=1.21.2 {
    /*@Inject(method = "render", at=@At("TAIL"))
    private void dynamiccrosshair$fixRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (CrosshairRenderer.autoHudCompat) {
            // Weird hack to ensure nothing bad happens if both DynamicCrosshair and AutoHud are active.
            // If nothing gets rendered except for the crosshair with half transparency, it breaks.
            // We have to draw something, so draw it off-screen.
            context.drawGuiTexture(RenderLayer::getGuiTextured, DynamicCrosshairStyles.DOT, -20, -20, 15, 15);
        }
    }*///?}

    @ModifyExpressionValue(method = "renderCrosshair",
            //? if <1.20.3 {
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
            //? if <1.20.3 {
            target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V",
            //?} else if <1.21.2 {
            /*target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V",
			*///?} else {
            /*target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V",
            *///?}
            ordinal = 0))
    private void dynamiccrosshair$drawCrosshair(
            DrawContext context,
            //? if >=1.21.2
            /*Function<Identifier, RenderLayer> renderLayers,*/
            Identifier texture,
            int x, int y,
            //? if <1.20.3
            int u, int v,
            int width, int height,
            Operation<Void> original
    ) {
        CrosshairRenderer.wrapRender(context, x, y,
                //? if <1.20.3 {
                () -> original.call(context, texture, x, y, u, v, width, height),
                () -> original.call(context, texture, x, y, u, v, width, height)
                //?} else if <1.21.2 {
                /*() -> original.call(context, texture, x, y, width, height),
                () -> original.call(context, texture, x, y, width, height)
                *///?} else {
                /*() -> original.call(context, renderLayers, texture, x, y, width, height),
                () -> context.drawGuiTexture(RenderLayer::getGuiTextured, texture, x, y, width, height)
                *///?}
        );
    }

    @ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getPerspective()Lnet/minecraft/client/option/Perspective;"), require = 0)
    private Perspective dynamiccrosshair$thirdPersonCrosshair(Perspective originalPerspective) {
        if (originalPerspective == Perspective.THIRD_PERSON_BACK && DynamicCrosshairMod.config.isThirdPersonCrosshair()) {
            return Perspective.FIRST_PERSON;
        }
        return originalPerspective;
    }
}
