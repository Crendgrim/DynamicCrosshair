package mod.crend.dynamiccrosshair.config.gui;

import mod.crend.dynamiccrosshair.compat.YaclIntegration;
import mod.crend.libbamboo.auto.annotation.CustomController;
import mod.crend.dynamiccrosshair.config.Config;

//? if yacl {
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ColorController;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import mod.crend.libbamboo.controller.NestedController;
import mod.crend.libbamboo.controller.NestingController;
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
	Option<Boolean> coalesceOption;

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
				.description(OptionDescription.of(Text.translatable("dynamiccrosshair.option.crosshairStyle.style.description")))
				.binding(option.binding().defaultValue().style,
						() -> option.pendingValue().style,
						style -> option.pendingValue().style = style
				)
				.customController(SelectCrosshairController::new)
				.build();
		customColorOption = Option.<Color>createBuilder()
				.name(Text.translatable("dynamiccrosshair.option.crosshairStyle.color.customColor"))
				.description(OptionDescription.of(Text.translatable("dynamiccrosshair.option.crosshairStyle.color.customColor.description")))
				.binding(option.binding().defaultValue().customColor,
						() -> option.pendingValue().customColor,
						color -> option.pendingValue().customColor = color
				)
				.customController(opt -> new NestedController<>(opt, new ColorController(opt, true)))
				.build();
		overrideColorOption = Option.<Boolean>createBuilder()
				.name(Text.translatable("dynamiccrosshair.option.crosshairStyle.color.crosshairColor"))
				.description(OptionDescription.of(Text.translatable("dynamiccrosshair.option.crosshairStyle.color.crosshairColor.description")))
				.binding(option.binding().defaultValue().overrideColor,
						() -> option.pendingValue().overrideColor,
						overrideColor -> option.pendingValue().overrideColor = overrideColor
				)
				.customController(opt -> new NestingController(opt, customColorOption))
				.build();
		enableBlendOption = Option.<Boolean>createBuilder()
				.name(Text.translatable("dynamiccrosshair.option.crosshairStyle.color.enableBlend"))
				.description(OptionDescription.of(Text.translatable("dynamiccrosshair.option.crosshairStyle.color.enableBlend.description")))
				.binding(option.binding().defaultValue().enableBlend,
						() -> option.pendingValue().enableBlend,
						enableBlend -> option.pendingValue().enableBlend = enableBlend
				)
				.controller(TickBoxControllerBuilder::create)
				.build();
		coalesceOption = Option.<Boolean>createBuilder()
				.name(Text.translatable("dynamiccrosshair.option.crosshairStyle.coalesce"))
				.description(OptionDescription.of(Text.translatable("dynamiccrosshair.option.crosshairStyle.coalesce.description")))
				.binding(option.binding().defaultValue().coalesce,
						() -> option.pendingValue().coalesce,
						coalesce -> option.pendingValue().coalesce = coalesce
				)
				.controller(TickBoxControllerBuilder::create)
				.build();

		nestedYacl = YetAnotherConfigLib.createBuilder()
				.title(Text.translatable("dynamiccrosshair.category.style"))
				.category(ConfigCategory.createBuilder()
						.name(Text.translatable("dynamiccrosshair.group.crosshairStyle"))
						.option(styleOption)
						.option(overrideColorOption)
						.option(enableBlendOption)
						.option(coalesceOption)
						.option(customColorOption)
						.build())
				.save(() -> {
					Config.CrosshairStyleSettings newStyle = new Config.CrosshairStyleSettings();
					newStyle.style = styleOption.pendingValue();
					newStyle.customColor = customColorOption.pendingValue();
					newStyle.overrideColor = overrideColorOption.pendingValue();
					newStyle.enableBlend = enableBlendOption.pendingValue();
					newStyle.coalesce = coalesceOption.pendingValue();
					option.requestSet(newStyle);
					ConfigHandler.CONFIG_STORE.save();
					YaclIntegration.onChange();
				})
				.build();
		ConfigHandler.CONFIG_STORE.withYacl().configChangeEvent.register(() -> {
			styleOption.requestSet(option.pendingValue().style);
			overrideColorOption.requestSet(option.pendingValue().overrideColor);
			customColorOption.requestSet(option.pendingValue().customColor);
			enableBlendOption.requestSet(option.pendingValue().enableBlend);
			coalesceOption.requestSet(option.pendingValue().coalesce);
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
//?} else {
/*public class CrosshairStyleController {
	public static abstract class Factory implements CustomController.ControllerFactory<Config.CrosshairStyleSettings> {
	}
}
*///?}
