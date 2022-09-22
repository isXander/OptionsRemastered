package dev.isxander.optionsremastered.api;

import com.google.common.collect.*;
import dev.isxander.yacl.api.ConfigCategory;
import net.minecraft.client.option.GameOptions;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class CategorySupplier implements Function<GameOptions, ConfigCategory> {
    private int priority = 1000;
    private Supplier<?> supplier;

    public CategorySupplier(Supplier<?> category) {
        this.supplier = category;
    }

    @Override
    public ConfigCategory apply(GameOptions gameOptions) {
        return supplier.supply(gameOptions);
    }

    public void override(Supplier<?> supplier, int priority) {
        if (priority < this.priority) {
            this.supplier = supplier;
            this.priority = priority;
        }
    }

    public void extend(BiConsumer<GameOptions, ConfigCategory.Builder> consumer, int priority) {
        supplier.extenders.add(new Extender(priority, consumer));
    }

    public void extend(BiConsumer<GameOptions, ConfigCategory.Builder> consumer) {
        extend(consumer, 1000);
    }

    public static abstract class Supplier<T> {
        protected final Multiset<Extender> extenders = HashMultiset.create();

        protected abstract T generateBuilder(GameOptions options);

        protected abstract ConfigCategory extend(T builder, GameOptions options);

        public final ConfigCategory supply(GameOptions options) {
            return extend(generateBuilder(options), options);
        }
    }

    public record Extender(int priority, BiConsumer<GameOptions, ConfigCategory.Builder> consumer) implements Comparable<Extender> {
        @Override
        public int compareTo(@NotNull CategorySupplier.Extender o) {
            return Integer.compare(priority(), o.priority());
        }
    }
}
