package dev.isxander.optionsremastered;

import dev.isxander.optionsremastered.api.ConfigCategorySupplier;
import dev.isxander.optionsremastered.utils.ValueFormatters;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.gui.controllers.ActionController;
import dev.isxander.yacl.gui.controllers.EnumController;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;

public class GeneralOptionsCategory extends ConfigCategorySupplier {
    @Override
    protected ConfigCategory.Builder generateBuilder(GameOptions options) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Text.translatable("options.title"));

        builder.option(OptionsRemastered.minecraftSliderOption(options.getFov(), int.class, ValueFormatters.fov)
                .instant(true)
                .listener((opt, pending) -> options.write())
                .build());

        MinecraftClient client = MinecraftClient.getInstance();
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
                .option(OptionsRemastered.minecraftOption(options.getRealmsNotifications(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .option(OptionsRemastered.minecraftOption(options.getAllowServerListing(), boolean.class)
                        .controller(TickBoxController::new)
                        .build())
                .build());
        
        return builder;
    }
}
