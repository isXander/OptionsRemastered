package dev.isxander.optionsremastered.api;

import dev.isxander.optionsremastered.api.CategorySupplier;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.PlaceholderCategory;
import net.minecraft.client.option.GameOptions;

public abstract class PlaceholderCategorySupplier extends CategorySupplier.Supplier<PlaceholderCategory> {
    @Override
    protected ConfigCategory extend(PlaceholderCategory builder, GameOptions options) {
        return builder;
    }
}
