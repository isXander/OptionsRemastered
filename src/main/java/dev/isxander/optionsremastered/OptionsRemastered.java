package dev.isxander.optionsremastered;

import com.google.common.collect.ImmutableList;
import dev.isxander.optionsremastered.compat.Compat;
import dev.isxander.optionsremastered.compat.SodiumCompat;
import dev.isxander.optionsremastered.mixins.SimpleOptionAccessor;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.gui.controllers.*;
import dev.isxander.yacl.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl.gui.controllers.slider.FloatSliderController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.option.*;
import net.minecraft.client.render.ChunkBuilderMode;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class OptionsRemastered {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final GameOptions options = client.options;

    public static Screen createScreen(Screen parent) {
        Option<NarratorMode> narratorOption = minecraftOption(options.getNarrator(), NarratorMode.class)
                .controller(opt -> new EnumController<>(opt, ValueFormatters.narratorMode()))
                .build();
        Option<Double> chatOpacityOption = minecraftSliderOption(options.getChatOpacity(), double.class, ValueFormatters.chatOpacity())
                .build();
        Option<Double> textBackgroundOpacityOption = minecraftSliderOption(options.getTextBackgroundOpacity(), double.class, ValueFormatters.percent())
                .build();
        Option<Double> chatDelayOption = minecraftOption(options.getChatDelay(), double.class)
                .name(Text.translatable("options-remastered.chat.delay"))
                .controller(opt -> new CallbackSliderController<>(opt, options.getChatDelay(), ValueFormatters.chatDelay()))
                .build();
        Option<Double> chatLineSpacingOption = minecraftSliderOption(options.getChatLineSpacing(), double.class, ValueFormatters.percent())
                .build();
        Option<Boolean> autoJumpOption = minecraftOption(options.getAutoJump(), boolean.class)
                .controller(TickBoxController::new)
                .build();
        Option<Boolean> sneakToggledOption = minecraftOption(options.getSneakToggled(), boolean.class)
                .controller(opt -> new BooleanController(opt, ValueFormatters.holdToggle(), false))
                .build();
        Option<Boolean> sprintToggledOption = minecraftOption(options.getSprintToggled(), boolean.class)
                .controller(opt -> new BooleanController(opt, ValueFormatters.holdToggle(), false))
                .build();
        Option<Double> distortionEffectScaleOption = minecraftSliderOption(options.getDistortionEffectScale(), double.class, ValueFormatters.percentWithOff())
                .build();
        Option<Double> fovEffectScaleOption = minecraftSliderOption(options.getFovEffectScale(), double.class, ValueFormatters.percentWithOff())
                .build();

        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("options.title"))
                .category(generalOptions())
                .category(videoOptions(distortionEffectScaleOption, fovEffectScaleOption))
                .category(soundOptions())
                .category(controlsOptions(autoJumpOption, sneakToggledOption, sprintToggledOption))
                .category(resourcePackOptions())
                .category(chatOptions(narratorOption, textBackgroundOpacityOption, chatOpacityOption, chatDelayOption, chatLineSpacingOption))
                .category(skinOptions())
                .category(languageOptions())
                .category(accessibilityOptions(narratorOption, textBackgroundOpacityOption, chatOpacityOption, chatLineSpacingOption, chatDelayOption, autoJumpOption, sneakToggledOption, sprintToggledOption, distortionEffectScaleOption, fovEffectScaleOption))
                .save(OptionsRemastered::save)
                .build().generateScreen(parent);
    }

    private static ConfigCategory generalOptions() {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("options.title"));

        builder.option(minecraftSliderOption(options.getFov(), int.class, ValueFormatters.fov())
                .instant(true)
                .listener((opt, pending) -> options.write())
                .build());

        if (client.world != null && client.isIntegratedServerRunning()) {
            OptionGroup.Builder groupBuilder = OptionGroup.createBuilder()
                    .name(Text.translatable("options.difficulty"));

            boolean locked = client.world.getLevelProperties().isDifficultyLocked();

            Option<Difficulty> difficultyOption = Option.createBuilder(Difficulty.class)
                    .name(Text.translatable("options.difficulty"))
                    .available(!locked)
                    .binding(new Binding<>() {
                        @Override
                        public void setValue(Difficulty value) {
                            client.getNetworkHandler().sendPacket(new UpdateDifficultyC2SPacket(value));
                            client.world.getLevelProperties().setDifficulty(value);
                        }

                        @Override
                        public Difficulty getValue() {
                            return client.world.getLevelProperties().getDifficulty();
                        }

                        @Override
                        public Difficulty defaultValue() {
                            return getValue();
                        }
                    })
                    .controller(opt -> new EnumController<>(opt, Difficulty::getTranslatableName))
                    .build();

            ButtonOption lockButton = ButtonOption.createBuilder()
                    .name(Text.translatable("difficulty.lock.title"))
                    .available(!locked)
                    .action((parent, btn) -> client
                            .setScreen(
                                    new ConfirmScreen(
                                            (difficultyLocked) -> {
                                                if (difficultyLocked && client.world != null) {
                                                    difficultyOption.applyValue();
                                                    client.getNetworkHandler().sendPacket(new UpdateDifficultyLockC2SPacket(true));
                                                    client.world.getLevelProperties().setDifficultyLocked(true);
                                                    difficultyOption.setAvailable(false);
                                                    btn.setAvailable(false);
                                                    client.setScreen(parent);
                                                }
                                            },
                                            Text.translatable("difficulty.lock.title"),
                                            Text.translatable("difficulty.lock.question", difficultyOption.pendingValue())
                                    )
                            ))
                    .controller(ActionController::new)
                    .build();

            groupBuilder.option(difficultyOption);
            groupBuilder.option(lockButton);

            builder.group(groupBuilder.build());
        }

        builder.group(OptionGroup.createBuilder()
                .name(Text.translatable("options.online.title"))
                .option(minecraftOption(options.getRealmsNotifications(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(minecraftOption(options.getAllowServerListing(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .build());

        return builder.build();
    }

    private static ConfigCategory skinOptions() {
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

        return builder.build();
    }

    private static ConfigCategory soundOptions() {
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
                    .controller(opt -> new FloatSliderController(opt, 0f, 1f, 0.01f, ValueFormatters.percentWithOffF()))
                    .build();

            if (soundCategory == SoundCategory.MASTER)
                builder.option(soundOption);
            else
                groupBuilder.option(soundOption);
        }

        builder.group(groupBuilder.build());

        return builder.build();
    }

    private static ConfigCategory videoOptions(Option<Double> distortionEffectScaleOption, Option<Double> fovEffectScaleOption) {
        if (Compat.SODIUM) return SodiumCompat.getSodiumVideoOptions();

        return ConfigCategory.createBuilder()
                .name(Text.translatable("options.video"))
                .option(minecraftOption(options.getGraphicsMode(), GraphicsMode.class)
                        .controller(EnumController::new)
                        .build())
                .option(minecraftSliderOption(options.getViewDistance(), int.class, ValueFormatters.chunks())
                        .build())
                .option(minecraftSliderOption(options.getSimulationDistance(), int.class, ValueFormatters.chunks())
                        .build())
                .option(minecraftOption(options.getChunkBuilderMode(), ChunkBuilderMode.class)
                        .controller(EnumController::new)
                        .build())
                .option(minecraftOption(options.getAo(), AoMode.class)
                        .controller(EnumController::new)
                        .build())
                .option(minecraftSliderOption(options.getMaxFps(), int.class, ValueFormatters.fps())
                        .build())
                .option(minecraftOption(options.getEnableVsync(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(minecraftOption(options.getBobView(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(minecraftSliderOption(options.getGuiScale(), int.class, ValueFormatters.guiScale())
                        .flag(MinecraftClient::onResolutionChanged)
                        .build())
                .option(minecraftOption(options.getAttackIndicator(), AttackIndicator.class)
                        .controller(EnumController::new)
                        .build())
                .option(minecraftSliderOption(options.getGamma(), double.class, ValueFormatters.gamma())
                        .build())
                .option(minecraftOption(options.getCloudRenderMode(), CloudRenderMode.class)
                        .controller(EnumController::new)
                        .build())
                .option(minecraftOption(options.getFullscreen(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(minecraftOption(options.getParticles(), ParticlesMode.class)
                        .controller(EnumController::new)
                        .build())
                .option(minecraftSliderOption(options.getMipmapLevels(), int.class, ValueFormatters.mipmaps())
                        .flag(client -> {
                            client.setMipmapLevels(options.getMipmapLevels().getValue());
                            client.reloadResourcesConcurrently();
                        })
                        .build())
                .option(minecraftOption(options.getEntityShadows(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(distortionEffectScaleOption)
                .option(minecraftSliderOption(options.getEntityDistanceScaling(), double.class, ValueFormatters.percent())
                        .build())
                .option(fovEffectScaleOption)
                .option(minecraftOption(options.getShowAutosaveIndicator(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .build();
    }

    private static ConfigCategory controlsOptions(Option<Boolean> autoJumpOption, Option<Boolean> sneakToggledOption, Option<Boolean> sprintToggledOption) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("options.controls"))
                .option(sneakToggledOption)
                .option(sprintToggledOption)
                .option(autoJumpOption)
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("options.mouse_settings"))
                        .option(minecraftSliderOption(options.getMouseSensitivity(), double.class, ValueFormatters.mouseSensitivity())
                                .build())
                        .option(minecraftOption(options.getInvertYMouse(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .option(minecraftSliderOption(options.getMouseWheelSensitivity(), double.class, DoubleSliderController.DEFAULT_FORMATTER)
                                .build())
                        .option(minecraftOption(options.getDiscreteMouseScroll(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .option(minecraftOption(options.getTouchscreen(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("controls.keybinds"))
                        .option(ButtonOption.createBuilder()
                                .name(Text.translatable("controls.keybinds"))
                                .action((yaclScreen, button) -> client.setScreen(new KeybindsScreen(yaclScreen, options)))
                                .controller(opt -> new ActionController(opt, Text.translatable("options-remastered.keybind_screen.button")))
                                .build())
                        .build())
                .build();
    }

    private static ConfigCategory languageOptions() {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("narrator.button.language"));

        builder.option(minecraftOption(options.getForceUnicodeFont(), boolean.class)
                .controller(TickBoxController::new)
                .build());
        builder.option(Option.createBuilder(Text.class)
                .binding(Binding.immutable(Text.translatable("options.languageWarning")))
                .controller(LabelController::new)
                .build());

        OptionGroup.Builder groupBuilder = OptionGroup.createBuilder();
        LanguageManager languageManager = client.getLanguageManager();
        List<Option<Boolean>> languageButtons = new ArrayList<>();
        for (LanguageDefinition language : languageManager.getAllLanguages()) {
            languageButtons.add(Option.createBuilder(boolean.class)
                    .name(Text.of(language.toString()))
                    .controller(TickBoxController::new)
                    .listener((option, pendingValue) -> {
                        if (pendingValue) {
                            languageButtons.forEach(btn -> {
                                if (btn != option)
                                    btn.requestSet(false);
                            });
                        }
                    })
                    .binding(
                            false,
                            () -> languageManager.getLanguage().getCode().equals(language.getCode()),
                            on -> {
                                if (on) {
                                    languageManager.setLanguage(language);
                                    options.language = language.getCode();
                                    client.reloadResources();
                                }
                            }
                    )
                    .build());
        }
        groupBuilder.options(languageButtons);
        builder.group(groupBuilder.build());
        return builder.build();
    }

    private static ConfigCategory chatOptions(Option<NarratorMode> narratorModeOption, Option<Double> textBackgroundOpacityOption, Option<Double> chatOpacityOption, Option<Double> chatDelayOption, Option<Double> chatLineSpacingOption) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("options.chat.title"))
                .option(minecraftOption(options.getChatVisibility(), ChatVisibility.class)
                        .controller(EnumController::new)
                        .build())
                .option(minecraftOption(options.getChatColors(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(minecraftOption(options.getChatLinks(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(minecraftOption(options.getChatLinksPrompt(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(chatOpacityOption)
                .option(textBackgroundOpacityOption)
                .option(minecraftSliderOption(options.getChatScale(), double.class, ValueFormatters.percent())
                        .build())
                .option(chatLineSpacingOption)
                .option(chatDelayOption)
                .option(minecraftSliderOption(options.getChatWidth(), double.class, ValueFormatters.chatWidth())
                        .build())
                .option(minecraftSliderOption(options.getChatHeightFocused(), double.class, ValueFormatters.chatHeight())
                        .build())
                .option(minecraftSliderOption(options.getChatHeightUnfocused(), double.class, ValueFormatters.chatHeight())
                        .build())
                .option(narratorModeOption)
                .option(minecraftOption(options.getAutoSuggestions(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(minecraftOption(options.getHideMatchedNames(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(minecraftOption(options.getReducedDebugInfo(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(minecraftOption(options.getChatPreview(), ChatPreviewMode.class)
                        .controller(EnumController::new)
                        .build())
                .option(minecraftOption(options.getOnlyShowSecureChat(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .build();
    }

    private static ConfigCategory resourcePackOptions() {
        return PlaceholderCategory.createBuilder()
                .name(Text.translatable("options.resourcepack"))
                .screen((client, yaclScreen) ->
                        new PackScreen(yaclScreen, client.getResourcePackManager(), resourcePackManager -> {
                            List<String> list = ImmutableList.copyOf(options.resourcePacks);
                            options.resourcePacks.clear();
                            options.incompatibleResourcePacks.clear();

                            for (ResourcePackProfile resourcePackProfile : resourcePackManager.getEnabledProfiles()) {
                                if (!resourcePackProfile.isPinned()) {
                                    options.resourcePacks.add(resourcePackProfile.getName());
                                    if (!resourcePackProfile.getCompatibility().isCompatible()) {
                                        options.incompatibleResourcePacks.add(resourcePackProfile.getName());
                                    }
                                }
                            }

                            options.write();
                            List<String> list2 = ImmutableList.copyOf(options.resourcePacks);
                            if (!list2.equals(list)) {
                                client.reloadResources();
                            }
                        }, client.getResourcePackDir(), Text.translatable("resourcePack.title")))
                .build();
    }

    private static ConfigCategory accessibilityOptions(Option<NarratorMode> narratorOption, Option<Double> textBackgroundOpacity, Option<Double> chatOpacityOption, Option<Double> chatLineSpacingOption, Option<Double> chatDelayOption, Option<Boolean> autoJumpOption, Option<Boolean> sneakToggledOption, Option<Boolean> sprintToggledOption, Option<Double> distortionEffectScaleOption, Option<Double> fovEffectScaleOption) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("options.accessibility.title"))
                .option(narratorOption)
                .option(minecraftOption(options.getShowSubtitles(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(textBackgroundOpacity)
                .option(minecraftOption(options.getBackgroundForChatOnly(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(chatOpacityOption)
                .option(chatLineSpacingOption)
                .option(chatDelayOption)
                .option(autoJumpOption)
                .option(sneakToggledOption)
                .option(sprintToggledOption)
                .option(distortionEffectScaleOption)
                .option(fovEffectScaleOption)
                .option(minecraftOption(options.getMonochromeLogo(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(minecraftOption(options.getHideLightningFlashes(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(minecraftSliderOption(options.getDarknessEffectScale(), double.class, ValueFormatters.percentWithOff())
                        .build())
                .build();
    }

    private static void save() {
        options.write();
    }

    private static <T> Option.Builder<T> minecraftOption(SimpleOption<T> minecraftOption, Class<T> typeClass) {
        SimpleOptionAccessor<T> accessor = (SimpleOptionAccessor<T>) (Object) minecraftOption;

        return Option.createBuilder(typeClass)
                .name(accessor.getText())
                .tooltip(value -> convertOrderedTextList(accessor.getTooltipFactoryGetter().apply(client).apply(value)))
                .binding(Binding.minecraft(minecraftOption));
    }

    private static <T extends Number> Option.Builder<T> minecraftSliderOption(SimpleOption<T> minecraftOption, Class<T> typeClass, Function<T, Text> valueFormatter) {
        return minecraftOption(minecraftOption, typeClass)
                .controller(opt -> new CallbackSliderController<>(opt, minecraftOption, valueFormatter));
    }

    private static Text convertOrderedTextList(List<OrderedText> list) {
        MutableText text = Text.empty();
        boolean first = true;
        for (OrderedText orderedText : list) {
            if (!first) text.append(" ");
            text.append(convertOrderedText(orderedText));
            first = false;
        }
        return text;
    }

    private static Text convertOrderedText(OrderedText orderedText) {
        MutableText text = Text.empty();
        orderedText.accept((index, style, codePoint) -> {
            text.append(Text.literal(Character.toString(codePoint)).setStyle(style));
            return true;
        });

        return text;
    }
}
