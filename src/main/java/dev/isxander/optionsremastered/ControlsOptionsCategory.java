package dev.isxander.optionsremastered;

import dev.isxander.optionsremastered.api.ConfigCategorySupplier;
import dev.isxander.optionsremastered.utils.ValueFormatters;
import dev.isxander.yacl.api.ButtonOption;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.gui.controllers.ActionController;
import dev.isxander.yacl.gui.controllers.BooleanController;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.slider.DoubleSliderController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;

public class ControlsOptionsCategory extends ConfigCategorySupplier {
    @Override
    protected ConfigCategory.Builder generateBuilder(GameOptions options) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("options.controls"))
                .option(OptionsRemastered.minecraftOption(options.getSneakToggled(), boolean.class)
                        .controller(opt -> new BooleanController(opt, ValueFormatters.HOLD_TOGGLE, false))
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getSprintToggled(), boolean.class)
                        .controller(opt -> new BooleanController(opt, ValueFormatters.HOLD_TOGGLE, false))
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getAutoJump(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("options.mouse_settings"))
                        .option(OptionsRemastered.minecraftSliderOption(options.getMouseSensitivity(), double.class, ValueFormatters.MOUSE_SENSITIVITY)
                                .build())
                        .option(OptionsRemastered.minecraftOption(options.getInvertYMouse(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .option(OptionsRemastered.minecraftSliderOption(options.getMouseWheelSensitivity(), double.class, DoubleSliderController.DEFAULT_FORMATTER)
                                .build())
                        .option(OptionsRemastered.minecraftOption(options.getDiscreteMouseScroll(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .option(OptionsRemastered.minecraftOption(options.getTouchscreen(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("controls.keybinds"))
                        .option(ButtonOption.createBuilder()
                                .name(Text.translatable("controls.keybinds"))
                                .action((yaclScreen, button) -> MinecraftClient.getInstance().setScreen(new KeybindsScreen(yaclScreen, options)))
                                .controller(opt -> new ActionController(opt, Text.translatable("options-remastered.keybind_screen.button")))
                                .build())
                        .build());
    }
}
