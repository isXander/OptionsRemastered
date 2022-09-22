package dev.isxander.optionsremastered.utils;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.gui.controllers.slider.ISliderController;
import dev.isxander.yacl.gui.controllers.slider.SliderControllerElement;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Function;

public class CallbackSliderController<T extends Number> implements ISliderController<T> {
    private final Option<T> option;
    private final SimpleOption.SliderCallbacks<T> callbacks;
    private final Function<T, Text> valueFormatter;

    public CallbackSliderController(Option<T> option, SimpleOption<T> minecraftOption, Function<T, Text> valueFormatter) {
        this.option = option;
        this.callbacks = (SimpleOption.SliderCallbacks<T>) minecraftOption.getCallbacks();
        this.valueFormatter = valueFormatter;
    }

    @Override
    public Option<T> option() {
        return option;
    }

    @Override
    public Text formatValue() {
        return valueFormatter.apply(option.pendingValue());
    }

    @Override
    public double min() {
        return 0.0;
    }

    @Override
    public double max() {
        return 1.0;
    }

    @Override
    public double interval() {
        return 0;
    }

    @Override
    public void setPendingValue(double v) {
        option.requestSet(callbacks.toValue(v));
    }

    @Override
    public double pendingValue() {
        return callbacks.toSliderProgress(option.pendingValue());
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new CallbackSliderControllerElement(this, screen, widgetDimension, min(), max());
    }

    public static class CallbackSliderControllerElement extends SliderControllerElement {

        public CallbackSliderControllerElement(CallbackSliderController<?> controller, YACLScreen screen, Dimension<Integer> dim, double min, double max) {
            super(controller, screen, dim, min, max, 0);
        }

        @Override
        protected double roundToInterval(double value) {
            return MathHelper.clamp(value, 0.0, 1.0);
        }

        @Override
        public void incrementValue(double amount) {

        }
    }
}
