package dev.isxander.optionsremastered.api;

import dev.isxander.optionsremastered.api.CategorySupplier;
import dev.isxander.yacl.api.ConfigCategory;
import net.minecraft.client.option.GameOptions;

public abstract class ConfigCategorySupplier extends CategorySupplier.Supplier<ConfigCategory.Builder> {
    @Override
    protected ConfigCategory extend(ConfigCategory.Builder builder, GameOptions options) {
        extenders.forEach(e -> e.consumer().accept(options, builder));
        return builder.build();
    }
}
