package dev.isxander.optionsremastered.compat;

import dev.isxander.optionsremastered.api.PlaceholderCategorySupplier;
import dev.isxander.yacl.api.PlaceholderCategory;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;

public class LanguageReloadCompat {
    public static class LanguageReloadOptionsCategory extends PlaceholderCategorySupplier {
        @Override
        protected PlaceholderCategory generateBuilder(GameOptions options) {
            return PlaceholderCategory.createBuilder()
                    .name(Text.translatable("narrator.button.language"))
                    .screen((client, parent) -> new LanguageOptionsScreen(parent, options, client.getLanguageManager()))
                    .build();
        }
    }
}
