package dev.isxander.optionsremastered;

import dev.isxander.optionsremastered.api.ConfigCategorySupplier;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.text.Text;

public class SkinOptionsCategory extends ConfigCategorySupplier {
    @Override
    protected ConfigCategory.Builder generateBuilder(GameOptions options) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("options.skinCustomisation"));

        for (PlayerModelPart modelPart : PlayerModelPart.values()) {
            builder.option(Option.createBuilder(boolean.class)
                    .name(modelPart.getOptionName())
                    .binding(
                            true,
                            () -> options.isPlayerModelPartEnabled(modelPart),
                            value -> options.togglePlayerModelPart(modelPart, value)
                    )
                    .controller(TickBoxController::new)
                    .build());
        }

        return builder;
    }
}
