package dev.isxander.optionsremastered;

import dev.isxander.optionsremastered.api.ConfigCategorySupplier;
import dev.isxander.optionsremastered.utils.ValueFormatters;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.gui.controllers.slider.FloatSliderController;
import net.minecraft.client.option.GameOptions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

public class SoundOptionsCategory extends ConfigCategorySupplier {
    @Override
    protected ConfigCategory.Builder generateBuilder(GameOptions options) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("options.sounds"));

        OptionGroup.Builder groupBuilder = OptionGroup.createBuilder();

        for (SoundCategory soundCategory : SoundCategory.values()) {
            Option<Float> soundOption = Option.createBuilder(float.class)
                    .name(Text.translatable("soundCategory." + soundCategory.getName()))
                    .binding(
                            1f,
                            () -> options.getSoundVolume(soundCategory),
                            value -> options.setSoundVolume(soundCategory, value)
                    )
                    .controller(opt -> new FloatSliderController(opt, 0f, 1f, 0.01f, ValueFormatters.percentWithOffF))
                    .build();

            if (soundCategory == SoundCategory.MASTER)
                builder.option(soundOption);
            else
                groupBuilder.option(soundOption);
        }

        builder.group(groupBuilder.build());

        return builder;
    }
}
