package dev.isxander.optionsremastered.compat;

import dev.isxander.optionsremastered.api.PlaceholderCategorySupplier;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.PlaceholderCategory;
import me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;

public class SodiumCompat {
    public static ConfigCategory getSodiumVideoOptions() {
        return PlaceholderCategory.createBuilder()
                .name(Text.translatable("options.video"))
                .screen((client, parent) -> new SodiumOptionsGUI(parent))
                .build();
    }

    public static class SodiumVideoOptionsCategory extends PlaceholderCategorySupplier {
        @Override
        public PlaceholderCategory generateBuilder(GameOptions options) {
            return PlaceholderCategory.createBuilder()
                    .name(Text.translatable("options.video"))
                    .screen((client, parent) -> new SodiumOptionsGUI(parent))
                    .build();
        }
    }
}
