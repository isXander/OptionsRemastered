package dev.isxander.optionsremastered;

import dev.isxander.optionsremastered.api.ConfigCategorySupplier;
import dev.isxander.optionsremastered.utils.ValueFormatters;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.gui.controllers.EnumController;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.*;
import net.minecraft.client.render.ChunkBuilderMode;
import net.minecraft.text.Text;

public class VideoOptionsCategory extends ConfigCategorySupplier {
    @Override
    public ConfigCategory.Builder generateBuilder(GameOptions options) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("options.video"))
                .option(OptionsRemastered.minecraftOption(options.getGraphicsMode(), GraphicsMode.class)
                        .controller(EnumController::new)
                        .build())
                .option(OptionsRemastered.minecraftSliderOption(options.getViewDistance(), int.class, ValueFormatters.chunks)
                        .build())
                .option(OptionsRemastered.minecraftSliderOption(options.getSimulationDistance(), int.class, ValueFormatters.chunks)
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getChunkBuilderMode(), ChunkBuilderMode.class)
                        .controller(EnumController::new)
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getAo(), AoMode.class)
                        .controller(EnumController::new)
                        .build())
                .option(OptionsRemastered.minecraftSliderOption(options.getMaxFps(), int.class, ValueFormatters.fps)
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getEnableVsync(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getBobView(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(OptionsRemastered.minecraftSliderOption(options.getGuiScale(), int.class, ValueFormatters.guiScale)
                        .flag(MinecraftClient::onResolutionChanged)
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getAttackIndicator(), AttackIndicator.class)
                        .controller(EnumController::new)
                        .build())
                .option(OptionsRemastered.minecraftSliderOption(options.getGamma(), double.class, ValueFormatters.gamma)
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getCloudRenderMode(), CloudRenderMode.class)
                        .controller(EnumController::new)
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getFullscreen(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getParticles(), ParticlesMode.class)
                        .controller(EnumController::new)
                        .build())
                .option(OptionsRemastered.minecraftSliderOption(options.getMipmapLevels(), int.class, ValueFormatters.mipmaps)
                        .flag(client -> {
                            client.setMipmapLevels(options.getMipmapLevels().getValue());
                            client.reloadResourcesConcurrently();
                        })
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getEntityShadows(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(OptionsRemastered.minecraftSliderOption(options.getEntityDistanceScaling(), double.class, ValueFormatters.percent)
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getShowAutosaveIndicator(), boolean.class)
                        .controller(TickBoxController::new)
                        .build());
    }
}
