package dev.isxander.optionsremastered;

import dev.isxander.optionsremastered.api.ConfigCategorySupplier;
import dev.isxander.optionsremastered.utils.ValueFormatters;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
//import net.minecraft.client.option.ChatPreviewMode;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;

public class ChatOptionsCategory extends ConfigCategorySupplier {
    @Override
    protected ConfigCategory.Builder generateBuilder(GameOptions options) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("options.chat.title"))
                .group(OptionGroup.createBuilder()
                        .option(OptionsRemastered.minecraftOption(options.getChatVisibility(), ChatVisibility.class)
                                .controller(EnumController::new)
                                .build())
                        .option(OptionsRemastered.minecraftOption(options.getChatColors(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .option(OptionsRemastered.minecraftOption(options.getChatLinks(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .option(OptionsRemastered.minecraftOption(options.getChatLinksPrompt(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .option(OptionsRemastered.minecraftSliderOption(options.getChatOpacity(), double.class, ValueFormatters.CHAT_OPACITY)
                                .build())
                        .option(OptionsRemastered.minecraftSliderOption(options.getTextBackgroundOpacity(), double.class, ValueFormatters.PERCENT)
                                .build())
                        .option(OptionsRemastered.minecraftSliderOption(options.getChatDelay(), double.class, ValueFormatters.CHAT_DELAY)
                                .name(Text.translatable("options-remastered.chat.delay"))
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .option(OptionsRemastered.minecraftSliderOption(options.getChatScale(), double.class, ValueFormatters.PERCENT)
                                .build())
                        .option(OptionsRemastered.minecraftSliderOption(options.getChatWidth(), double.class, ValueFormatters.CHAT_WIDTH)
                                .build())
                        .option(OptionsRemastered.minecraftSliderOption(options.getChatHeightFocused(), double.class, ValueFormatters.CHAT_HEIGHT)
                                .build())
                        .option(OptionsRemastered.minecraftSliderOption(options.getChatHeightUnfocused(), double.class, ValueFormatters.CHAT_HEIGHT)
                                .build())
                        .option(OptionsRemastered.minecraftSliderOption(options.getChatLineSpacing(), double.class, ValueFormatters.PERCENT)
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .option(OptionsRemastered.minecraftOption(options.getAutoSuggestions(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .option(OptionsRemastered.minecraftOption(options.getHideMatchedNames(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .option(OptionsRemastered.minecraftOption(options.getReducedDebugInfo(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .option(OptionsRemastered.minecraftOption(options.getOnlyShowSecureChat(), boolean.class)
                                .controller(TickBoxController::new)
                                .build())
                        .build());
    }
}
