package dev.isxander.optionsremastered;

import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.function.Function;

class ValueFormatters {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    static Function<NarratorMode, Text> narratorMode() {
        return value -> client.getNarratorManager().isActive()
                ? value.getName()
                : Text.translatable("options.narrator.notavailable");
    }

    static Function<Integer, Text> fov() {
        return value -> switch (value) {
            case 70 -> Text.translatable("options.fov.min");
            case 110 -> Text.translatable("options.fov.max");
            default -> IntegerSliderController.DEFAULT_FORMATTER.apply(value);
        };
    }

    static Function<Integer, Text> chunks() {
        return value -> Text.translatable("options.chunks", value);
    }

    static Function<Integer, Text> fps() {
        return value -> value == 260
                ? Text.translatable("options.framerateLimit.max")
                : Text.translatable("options.framerate", value);
    }

    static Function<Integer, Text> guiScale() {
        return value -> value == 0
                ? Text.translatable("options.guiScale.auto")
                : Text.of(Integer.toString(value));
    }

    static Function<Double, Text> gamma() {
        return value -> {
            int percent = (int)(value * 100.0);

            return switch (percent) {
                case 0 -> Text.translatable("options.gamma.min");
                case 50 -> Text.translatable("options.gamma.default");
                case 100 -> Text.translatable("options.gamma.max");
                default -> Text.of(Integer.toString(percent));
            };
        };
    }

    static Function<Integer, Text> mipmaps() {
        return value -> value == 0
                ? ScreenTexts.OFF
                : Text.of(Integer.toString(value));
    }

    static Function<Double, Text> percent() {
        return value -> Text.of(String.format("%.0f%%", value * 100));
    }

    static Function<Double, Text> percentWithOff() {
        return value -> ((int)(value * 100)) == 0
                ? ScreenTexts.OFF
                : Text.of(String.format("%.0f%%", value * 100));
    }

    static Function<Float, Text> percentF() {
        return value -> Text.of(String.format("%.0f%%", value * 100));
    }

    static Function<Float, Text> percentWithOffF() {
        return value -> ((int)(value * 100)) == 0
                ? ScreenTexts.OFF
                : Text.of(String.format("%.0f%%", value * 100));
    }

    static Function<Double, Text> chatOpacity() {
        return value -> Text.of(String.format("%.0f%%", (value * 0.9 + 0.1) * 100));
    }

    static Function<Double, Text> chatDelay() {
        return value -> value <= 0
                ? Text.translatable("gui.none")
                : Text.translatable("options-remastered.formatter.seconds", String.format("%.1f", value));
    }

    static Function<Double, Text> chatWidth() {
        return value -> Text.translatable("options-remastered.formatter.pixels", ChatHud.getWidth(value));
    }

    static Function<Double, Text> chatHeight() {
        return value -> Text.translatable("options-remastered.formatter.pixels", ChatHud.getHeight(value));
    }

    static Function<Double, Text> mouseSensitivity() {
        return value -> {
            if (value == 0.0)
                return Text.translatable("options.sensitivity.min");
            if (value == 1.0)
                return Text.translatable("options.sensitivity.max");

            return Text.of(String.format("%.0f%%", value * 100 * 2));
        };
    }

    static Function<Boolean, Text> holdToggle() {
        return value -> value
                ? Text.translatable("options-remastered.formatter.hold_toggle.on")
                : Text.translatable("options-remastered.formatter.hold_toggle.off");
    }

    static Function<Double, Text> mouseWheelSensitivity() {
        return value -> Text.of(String.format("%.2f", Math.pow(10.0, value / 100.0)));
    }
}
