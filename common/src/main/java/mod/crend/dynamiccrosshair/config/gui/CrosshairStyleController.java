package mod.crend.dynamiccrosshair.config.gui;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import mod.crend.dynamiccrosshair.config.Config;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import mod.crend.libbamboo.auto.annotation.CustomController;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.Color;

public class CrosshairStyleController implements Controller<Config.CrosshairStyleSettings> {

	private final Option<Config.CrosshairStyleSettings> option;
	YetAnotherConfigLib nestedYacl;

	Option<Identifier> styleOption;
	Option<Boolean> overrideColorOption;
	Option<Color> customColorOption;
	Option<Boolean> enableBlendOption;
	Option<Boolean> isModifierOption;

	public CrosshairStyleController(Option<Config.CrosshairStyleSettings> option) {
		this.option = option;
	}

	@Override
	public Option<Config.CrosshairStyleSettings> option() {
		return option;
	}

	@Override
	public Text formatValue() {
		return Text.empty();
	}

	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		styleOption = Option.<Identifier>createBuilder()
				.name(Text.translatable("dynamiccrosshair.option.crosshairStyle.style"))
				.binding(option.binding().defaultValue().style,
						() -> option.pendingValue().style,
						style -> option.pendingValue().style = style
				)
				.customController(SelectCrosshairController::new)
				.build();
		customColorOption = Option.<Color>createBuilder()
				.name(Text.translatable("dynamiccrosshair.option.crosshairStyle.color.customColor"))
				.binding(option.binding().defaultValue().customColor,
						() -> option.pendingValue().customColor,
						color -> option.pendingValue().customColor = color
				)
				.controller(ColorControllerBuilder::create)
				.build();
		overrideColorOption = Option.<Boolean>createBuilder()
				.name(Text.translatable("dynamiccrosshair.option.crosshairStyle.color.crosshairColor"))
				.binding(option.binding().defaultValue().overrideColor,
						() -> option.pendingValue().overrideColor,
						overrideColor -> option.pendingValue().overrideColor = overrideColor
				)
				.listener((crosshairConfigColorOption, overrideColor) -> customColorOption.setAvailable(overrideColor))
				.controller(TickBoxControllerBuilder::create)
				.build();
		enableBlendOption = Option.<Boolean>createBuilder()
				.name(Text.translatable("dynamiccrosshair.option.crosshairStyle.color.enableBlend"))
				.binding(option.binding().defaultValue().enableBlend,
						() -> option.pendingValue().enableBlend,
						enableBlend -> option.pendingValue().enableBlend = enableBlend
				)
				.controller(TickBoxControllerBuilder::create)
				.build();
		isModifierOption = Option.<Boolean>createBuilder()
				.name(Text.translatable("dynamiccrosshair.option.crosshairStyle.isModifier"))
				.binding(option.binding().defaultValue().isModifier,
						() -> option.pendingValue().isModifier,
						isModifier -> option.pendingValue().isModifier = isModifier
				)
				.controller(TickBoxControllerBuilder::create)
				.build();

		nestedYacl = YetAnotherConfigLib.createBuilder()
				.title(Text.translatable("dynamiccrosshair.category.style"))
				.category(ConfigCategory.createBuilder()
						.name(Text.translatable("dynamiccrosshair.group.crosshairStyle"))
						.option(styleOption)
						.option(overrideColorOption)
						.option(customColorOption)
						.option(enableBlendOption)
						.option(isModifierOption)
						.build())
				.save(() -> {
					Config.CrosshairStyleSettings newStyle = new Config.CrosshairStyleSettings();
					newStyle.style = styleOption.pendingValue();
					newStyle.customColor = customColorOption.pendingValue();
					newStyle.overrideColor = overrideColorOption.pendingValue();
					newStyle.enableBlend = enableBlendOption.pendingValue();
					newStyle.isModifier = isModifierOption.pendingValue();
					option.requestSet(newStyle);
					ConfigHandler.CONFIG_STORE.save();
				})
				.build();
		ConfigHandler.CONFIG_STORE.withYacl().configChangeEvent.register(() -> {
			styleOption.requestSet(option.pendingValue().style);
			overrideColorOption.requestSet(option.pendingValue().overrideColor);
			customColorOption.requestSet(option.pendingValue().customColor);
			enableBlendOption.requestSet(option.pendingValue().enableBlend);
			isModifierOption.requestSet(option.pendingValue().isModifier);
		});
		return new CrosshairStyleControllerElement(this, screen, widgetDimension);
	}

	public static class Factory implements CustomController.ControllerFactory<Config.CrosshairStyleSettings> {
		@Override
		public Controller<Config.CrosshairStyleSettings> create(Option<Config.CrosshairStyleSettings> option) {
			return new CrosshairStyleController(option);
		}
	}
}
