package dev.isxander.optionsremastered;

import dev.isxander.optionsremastered.api.CategorySupplier;
import dev.isxander.optionsremastered.compat.Compat;
import dev.isxander.optionsremastered.compat.LanguageReloadCompat;
import dev.isxander.optionsremastered.compat.SodiumCompat;
import dev.isxander.optionsremastered.mixins.SimpleOptionAccessor;
import dev.isxander.optionsremastered.utils.CallbackSliderController;
import dev.isxander.yacl.api.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class OptionsRemastered {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final GameOptions options = client.options;

    public static final CategorySupplier GENERAL = new CategorySupplier(new GeneralOptionsCategory());
    public static final CategorySupplier VIDEO = new CategorySupplier(new VideoOptionsCategory());
    public static final CategorySupplier SOUND = new CategorySupplier(new SoundOptionsCategory());
    public static final CategorySupplier CONTROLS = new CategorySupplier(new ControlsOptionsCategory());
    public static final CategorySupplier RESOURCE_PACKS = new CategorySupplier(new ResourcePackOptionsCategory());
    public static final CategorySupplier CHAT = new CategorySupplier(new ChatOptionsCategory());
    public static final CategorySupplier SKIN = new CategorySupplier(new SkinOptionsCategory());
    public static final CategorySupplier LANGUAGE = new CategorySupplier(new LanguageOptionsCategory());
    public static final CategorySupplier ACCESSIBILITY = new CategorySupplier(new AccessibilityOptionsCategory());

    private static final List<CategorySupplier.Supplier<?>> customSuppliers = new ArrayList<>();

    static {
        if (Compat.SODIUM) VIDEO.override(new SodiumCompat.SodiumVideoOptionsCategory(), 500);
        if (Compat.LANGUAGE_RELOAD) LANGUAGE.override(new LanguageReloadCompat.LanguageReloadOptionsCategory(), 500);
    }

    public static Screen createScreen(Screen parent) {
        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("options.title"))
                .category(GENERAL.apply(options))
                .category(VIDEO.apply(options))
                .category(SOUND.apply(options))
                .category(CONTROLS.apply(options))
                .category(RESOURCE_PACKS.apply(options))
                .category(CHAT.apply(options))
                .category(SKIN.apply(options))
                .category(LANGUAGE.apply(options))
                .category(ACCESSIBILITY.apply(options))
                .save(OptionsRemastered::save);

        Collection<ConfigCategory> customCategories = getCustomCategories();
        if (!customCategories.isEmpty()) builder.categories(customCategories);

        return builder.build().generateScreen(parent);
    }

    private static Collection<ConfigCategory> getCustomCategories() {
        return customSuppliers.stream().map(supplier -> supplier.supply(options)).toList();
    }

    public static void addCustomCategory(CategorySupplier.Supplier<?> supplier) {
        customSuppliers.add(supplier);
    }

    private static void save() {
        options.write();
    }

    public static <T> Option.Builder<T> minecraftOption(SimpleOption<T> minecraftOption, Class<T> typeClass) {
        SimpleOptionAccessor<T> accessor = (SimpleOptionAccessor<T>) (Object) minecraftOption;
        SimpleOption.TooltipFactory<T> tooltipFactory = accessor.getTooltipFactoryGetter().apply(client);

        return Option.createBuilder(typeClass)
                .name(accessor.getText())
                .tooltip(value -> convertOrderedTextList(tooltipFactory.apply(value)))
                .binding(Binding.minecraft(minecraftOption));
    }

    public static <T extends Number> Option.Builder<T> minecraftSliderOption(SimpleOption<T> minecraftOption, Class<T> typeClass, Function<T, Text> valueFormatter) {
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
