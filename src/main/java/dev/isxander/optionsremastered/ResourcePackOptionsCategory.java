package dev.isxander.optionsremastered;

import com.google.common.collect.ImmutableList;
import dev.isxander.optionsremastered.api.PlaceholderCategorySupplier;
import dev.isxander.yacl.api.PlaceholderCategory;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.Text;

import java.util.List;

public class ResourcePackOptionsCategory extends PlaceholderCategorySupplier {
    @Override
    protected PlaceholderCategory generateBuilder(GameOptions options) {
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
}
