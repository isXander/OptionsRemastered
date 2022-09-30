package dev.isxander.optionsremastered;

import dev.isxander.optionsremastered.api.ConfigCategorySupplier;
import dev.isxander.optionsremastered.utils.ValueFormatters;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.*;
import net.minecraft.client.render.ChunkBuilderMode;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

import java.util.Optional;

public class VideoOptionsCategory extends ConfigCategorySupplier {
    @Override
    public ConfigCategory.Builder generateBuilder(GameOptions options) {
        Window window = MinecraftClient.getInstance().getWindow();
        Option<Integer> fullscreenResolutionOption = Option.createBuilder(int.class)
                .name(Text.translatable("options.fullscreen.resolution"))
                .controller(opt -> new IntegerSliderController(opt, 0, window.getMonitor() != null ? window.getMonitor().getVideoModeCount() : 0, 1, ValueFormatters.RESOLUTION))
                .binding(
                        0,
                        () -> {
                            if (window.getMonitor() == null)
                                return 0;

                            Optional<VideoMode> optional = window.getVideoMode();
                            return optional.map((videoMode) -> window.getMonitor().findClosestVideoModeIndex(videoMode) + 1).orElse(0);
                        },
                        value -> {
                            if (window.getMonitor() != null) {
                                if (value == 0) {
                                    window.setVideoMode(Optional.empty());
                                } else {
                                    window.setVideoMode(Optional.of(window.getMonitor().getVideoMode(value - 1)));
                                }
                            }

                            window.applyVideoMode();
                        }
                )
                .build();

        return ConfigCategory.createBuilder()
                .name(Text.translatable("options.video"))
                .group(OptionGroup.createBuilder()
                        .option(OptionsRemastered.minecraftOption(options.getGraphicsMode(), GraphicsMode.class)
                                .controller(EnumController::new)
                                .build())
                        .option(OptionsRemastered.minecraftSliderOption(options.getMaxFps(), int.class, ValueFormatters.FPS)
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .option(OptionsRemastered.minecraftSliderOption(options.getViewDistance(), int.class, ValueFormatters.CHUNKS)
                                .build())
                        .option(OptionsRemastered.minecraftSliderOption(options.getSimulationDistance(), int.class, ValueFormatters.CHUNKS)
                                .build())
                        .option(OptionsRemastered.minecraftOption(options.getChunkBuilderMode(), ChunkBuilderMode.class)
                                .controller(EnumController::new)
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .option(OptionsRemastered.minecraftOption(options.getFullscreen(), boolean.class)
                                .controller(TickBoxController::new)
                                .listener((opt, pending) -> {
                                    fullscreenResolutionOption.setAvailable(pending);
                                    if (!pending)
                                        fullscreenResolutionOption.requestSet(0);
                                })
                                .build())
                        .option(fullscreenResolutionOption)
                        .option(OptionsRemastered.minecraftOption(options.getEnableVsync(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .option(OptionsRemastered.minecraftSliderOption(options.getGuiScale(), int.class, ValueFormatters.GUI_SCALE)
                                .flag(MinecraftClient::onResolutionChanged)
                                .build())
                        .option(OptionsRemastered.minecraftOption(options.getCloudRenderMode(), CloudRenderMode.class)
                                .controller(EnumController::new)
                                .build())
                        .option(OptionsRemastered.minecraftOption(options.getAo(), AoMode.class)
                                .controller(EnumController::new)
                                .build())
                        .option(OptionsRemastered.minecraftOption(options.getEntityShadows(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .option(OptionsRemastered.minecraftSliderOption(options.getEntityDistanceScaling(), double.class, ValueFormatters.PERCENT)
                                .build())
                        .option(OptionsRemastered.minecraftSliderOption(options.getGamma(), double.class, ValueFormatters.GAMMA)
                                .build())
                        .option(OptionsRemastered.minecraftOption(options.getParticles(), ParticlesMode.class)
                                .controller(EnumController::new)
                                .build())
                        .option(OptionsRemastered.minecraftSliderOption(options.getMipmapLevels(), int.class, ValueFormatters.MIPMAPS)
                                .flag(client -> {
                                    client.setMipmapLevels(options.getMipmapLevels().getValue());
                                    client.reloadResourcesConcurrently();
                                })
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .option(OptionsRemastered.minecraftOption(options.getBobView(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .option(OptionsRemastered.minecraftOption(options.getAttackIndicator(), AttackIndicator.class)
                                .controller(EnumController::new)
                                .build())
                        .option(OptionsRemastered.minecraftOption(options.getShowAutosaveIndicator(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .build());
    }
}
