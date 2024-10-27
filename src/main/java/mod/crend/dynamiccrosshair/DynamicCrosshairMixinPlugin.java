package mod.crend.dynamiccrosshair;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import mod.crend.libbamboo.PlatformUtils;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class DynamicCrosshairMixinPlugin implements IMixinConfigPlugin {

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String[] split = mixinClassName.split("\\.compat\\.mixin\\.");
        if (split.length == 2) {
            String modid = split[1].split("\\.")[0];
            return PlatformUtils.isModPresent(modid);
        }
        return true;
    }

    @Override
    public void onLoad(String mixinPackage) {
        // Forge doesn't init MixinExtras properly, manually force it if it hasn't been initialised yet.
        MixinExtrasBootstrap.init();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}