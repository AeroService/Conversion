package de.natrox.serialize;

import de.natrox.common.builder.IBuilder;
import de.natrox.common.validate.Check;
import de.natrox.serialize.exception.SerializeException;
import io.leangen.geantyref.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.function.Predicate;
import java.util.stream.Stream;

public sealed interface SerializerCollection extends Serializer<Object> permits SerializerCollectionImpl {

    static @NotNull SerializerCollection.Builder builder() {
        return new SerializerCollectionImpl.BuilderImpl(null);
    }

    static @NotNull SerializerCollection defaults() {
        return SerializerCollectionImpl.DEFAULT;
    }

    default SerializerCollection.Builder childBuilder() {
        return new SerializerCollectionImpl.BuilderImpl(this);
    }

    <T> @Nullable Serializer<T> get(@NotNull Type type);

    default <T> @Nullable Serializer<T> get(@NotNull Class<T> type) {
        Check.notNull(type, "type");
        return this.get((Type) type);
    }

    default <T> @Nullable Serializer<T> get(@NotNull TypeToken<T> typeToken) {
        Check.notNull(typeToken, "typeToken");
        return this.get(typeToken.getType());
    }

    @NotNull Object deserialize(@NotNull Object obj, Type @NotNull ... types) throws SerializeException;

    default Object deserialize(Object obj, Class<?>... types) throws SerializeException {
        Check.notNull(obj, "object");
        Check.notNull(types, "types");
        Check.argCondition(types.length <= 0, "types");
        return this.deserialize(obj, (Type[]) types);
    }

    default Object deserialize(Object obj, TypeToken<?>... typeTokens) throws SerializeException {
        Check.notNull(obj, "object");
        Check.notNull(typeTokens, "types");
        Check.argCondition(typeTokens.length <= 0, "types");
        return this.deserialize(obj, Stream.of(typeTokens).map(TypeToken::getType).toArray(Type[]::new));
    }

    interface Builder extends IBuilder<SerializerCollection> {

        <T> SerializerCollection.@NotNull Builder register(@NotNull Predicate<Type> test, final TypeSerializer<? super T> serializer);

        SerializerCollection.@NotNull Builder register(@NotNull Type type, @NotNull Serializer<?> serializer);

        default <T> SerializerCollection.@NotNull Builder register(@NotNull Class<T> type, @NotNull Serializer<? super T> serializer) {
            Check.notNull(type, "type");
            Check.notNull(serializer, "serializer");
            return this.register((Type) type, serializer);
        }

        default <T> SerializerCollection.@NotNull Builder register(@NotNull TypeToken<T> typeToken, final Serializer<? super T> serializer) {
            Check.notNull(typeToken, "typeToken");
            Check.notNull(serializer, "serializer");
            return this.register(typeToken.getType(), serializer);
        }

        default SerializerCollection.@NotNull Builder register(@NotNull TypeSerializer<?> serializer) {
            Check.notNull(serializer, "serializer");
            return this.register(serializer.type().getType(), serializer);
        }

        SerializerCollection.@NotNull Builder registerExact(@NotNull Type type, @NotNull Serializer<?> serializer);

        default <T> SerializerCollection.@NotNull Builder registerExact(@NotNull Class<T> type, @NotNull Serializer<? super T> serializer) {
            Check.notNull(type, "type");
            Check.notNull(serializer, "serializer");
            return this.registerExact((Type) type, serializer);
        }

        default <T> SerializerCollection.@NotNull Builder registerExact(@NotNull TypeToken<T> typeToken, final Serializer<? super T> serializer) {
            Check.notNull(typeToken, "typeToken");
            Check.notNull(serializer, "serializer");
            return this.registerExact(typeToken.getType(), serializer);
        }

        default SerializerCollection.@NotNull Builder registerExact(@NotNull TypeSerializer<?> serializer) {
            Check.notNull(serializer, "serializer");
            return this.registerExact(serializer.type().getType(), serializer);
        }
    }
}
