package dev.isxander.optionsremastered.utils;

import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.util.Monitor;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.function.Function;

public class ValueFormatters {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static final Function<NarratorMode, Text> narratorMode =
        value -> client.getNarratorManager().isActive()
                ? value.getName()
                : Text.translatable("options.narrator.notavailable");


    public static final Function<Integer, Text> fov = value -> switch (value) {
            case 70 -> Text.translatable("options.fov.min");
            case 110 -> Text.translatable("options.fov.max");
            default -> IntegerSliderController.DEFAULT_FORMATTER.apply(value);
    };

    public static final Function<Integer, Text> chunks = value -> Text.translatable("options.chunks", value);

    public static final Function<Integer, Text> fps = value -> value == 260
            ? Text.translatable("options.framerateLimit.max")
            : Text.translatable("options.framerate", value);


    public static final Function<Integer, Text> guiScale = value -> value == 0
            ? Text.translatable("options.guiScale.auto")
            : Text.of(Integer.toString(value));


    public static final Function<Double, Text> gamma = value -> {
        int percent = (int)(value * 100.0);

        return switch (percent) {
            case 0 -> Text.translatable("options.gamma.min");
            case 50 -> Text.translatable("options.gamma.default");
            case 100 -> Text.translatable("options.gamma.max");
            default -> Text.of(Integer.toString(percent));
        };
    };


    public static final Function<Integer, Text> mipmaps = value -> value == 0
            ? ScreenTexts.OFF
            : Text.of(Integer.toString(value));


    public static final Function<Double, Text> percent = value -> Text.of(String.format("%.0f%%", value * 100));

    public static final Function<Double, Text> percentWithOff = value -> ((int)(value * 100)) == 0
            ? ScreenTexts.OFF
            : Text.of(String.format("%.0f%%", value * 100));


    public static final Function<Float, Text> percentF = value -> Text.of(String.format("%.0f%%", value * 100));

    public static final Function<Float, Text> percentWithOffF = value -> ((int)(value * 100)) == 0
            ? ScreenTexts.OFF
            : Text.of(String.format("%.0f%%", value * 100));


    public static final Function<Double, Text> chatOpacity = value -> Text.of(String.format("%.0f%%", (value * 0.9 + 0.1) * 100));

    public static final Function<Double, Text> chatDelay = value -> value <= 0
            ? Text.translatable("gui.none")
            : Text.translatable("options-remastered.formatter.seconds", String.format("%.1f", value));


    public static final Function<Double, Text> chatWidth = value -> Text.translatable("options-remastered.formatter.pixels", ChatHud.getWidth(value));

    public static final Function<Double, Text> chatHeight = value -> Text.translatable("options-remastered.formatter.pixels", ChatHud.getHeight(value));

    public static final Function<Double, Text> mouseSensitivity = value -> {
        if (value == 0.0)
            return Text.translatable("options.sensitivity.min");
        if (value == 1.0)
            return Text.translatable("options.sensitivity.max");

        return Text.of(String.format("%.0f%%", value * 100 * 2));
    };

    public static final Function<Boolean, Text> holdToggle = value -> value
            ? Text.translatable("options-remastered.formatter.hold_toggle.on")
            : Text.translatable("options-remastered.formatter.hold_toggle.off");

    private static final Monitor monitor = client.getWindow().getMonitor();
    public static final Function<Integer, Text> resolution = value -> {
        if (monitor == null)
            return Text.translatable("options.fullscreen.unavailable");
        return value == 0
                ? Text.translatable("options.fullscreen.current")
                : Text.of(monitor.getVideoMode(value - 1).toString());
    };
}
