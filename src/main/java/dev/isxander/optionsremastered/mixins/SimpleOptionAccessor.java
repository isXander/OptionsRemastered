package dev.isxander.optionsremastered.mixins;

import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleOption.class)
public interface SimpleOptionAccessor<T> {
    @Accessor
    Text getText();

    @Accessor
    SimpleOption.TooltipFactory<T> getTooltipFactory();
}
