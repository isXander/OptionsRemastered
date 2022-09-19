package dev.isxander.optionsremastered.compat;

import net.fabricmc.loader.api.FabricLoader;

public class Compat {
    public static final boolean SODIUM = FabricLoader.getInstance().isModLoaded("sodium");
}
