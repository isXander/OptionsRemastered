package dev.isxander.optionsremastered;

import dev.isxander.optionsremastered.api.PlaceholderCategorySupplier;
import dev.isxander.yacl.api.PlaceholderCategory;
import net.minecraft.client.gui.screen.option.TelemetryInfoScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;

public class TelemetryOptionsCategory extends PlaceholderCategorySupplier {
    @Override
    protected PlaceholderCategory generateBuilder(GameOptions options) {
        return PlaceholderCategory.createBuilder()
                .name(Text.translatable("options.telemetry"))
                .screen((client, screen) -> new TelemetryInfoScreen(screen, options))
                .build();
    }
}
