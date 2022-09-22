package dev.isxander.optionsremastered;

import dev.isxander.optionsremastered.api.ConfigCategorySupplier;
import dev.isxander.optionsremastered.utils.ValueFormatters;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.gui.controllers.EnumController;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.text.Text;

public class AccessibilityOptionsCategory extends ConfigCategorySupplier {
    @Override
    protected ConfigCategory.Builder generateBuilder(GameOptions options) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("options.accessibility.title"))
                .option(OptionsRemastered.minecraftOption(options.getNarrator(), NarratorMode.class)
                        .controller(opt -> new EnumController<>(opt, ValueFormatters.narratorMode))
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getShowSubtitles(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getBackgroundForChatOnly(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(OptionsRemastered.minecraftSliderOption(options.getDistortionEffectScale(), double.class, ValueFormatters.percentWithOff)
                        .build())
                .option(OptionsRemastered.minecraftSliderOption(options.getFovEffectScale(), double.class, ValueFormatters.percentWithOff)
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getMonochromeLogo(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getHideLightningFlashes(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(OptionsRemastered.minecraftSliderOption(options.getDarknessEffectScale(), double.class, ValueFormatters.percentWithOff)
                        .build());
    }
}
