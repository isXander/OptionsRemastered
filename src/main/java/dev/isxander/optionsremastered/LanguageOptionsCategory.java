package dev.isxander.optionsremastered;

import dev.isxander.optionsremastered.api.ConfigCategorySupplier;
import dev.isxander.yacl.api.Binding;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.gui.controllers.LabelController;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class LanguageOptionsCategory extends ConfigCategorySupplier {
    @Override
    protected ConfigCategory.Builder generateBuilder(GameOptions options) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("narrator.button.language"));

        builder.option(OptionsRemastered.minecraftOption(options.getForceUnicodeFont(), boolean.class)
                .controller(TickBoxController::new)
                .build());
        builder.option(Option.createBuilder(Text.class)
                .binding(Binding.immutable(Text.translatable("options.languageWarning")))
                .controller(LabelController::new)
                .build());

        MinecraftClient client = MinecraftClient.getInstance();
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

        return builder;
    }
}
