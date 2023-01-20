/*
 * Copyright 2020-2023 AeroService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aero.conversion.core;

import io.leangen.geantyref.GenericTypeReflector;
import org.aero.common.core.validate.Check;
import org.aero.conversion.core.converter.ConditionalConverter;
import org.aero.conversion.core.converter.Converter;
import org.aero.conversion.core.converter.ConverterCondition;
import org.aero.conversion.core.converter.ConverterFactory;
import org.aero.conversion.core.exception.ConversionException;
import org.aero.conversion.core.exception.ConverterNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unchecked"})
sealed class ConversionBusImpl implements ConversionBus permits DefaultConversionBus {

    private static final ConditionalConverter<Object, Object> NO_OP_CONVERTER = new NoOpConverter();

    private final Set<ConditionalConverter<Object, Object>> converters;
    private final Map<Key, ConditionalConverter<Object, Object>> cache = new ConcurrentHashMap<>(64);

    ConversionBusImpl() {
        this.converters = new HashSet<>();
    }

    @Override
    public <U, V> void register(@NotNull final Class<? extends U> source, @NotNull final Class<V> target, @NotNull final Converter<U, V> converter) {
        this.register(new ConverterAdapter(converter, source, target));
    }

    @Override
    public void register(@NotNull final ConditionalConverter<?, ?> converter) {
        this.converters.add((ConditionalConverter<Object, Object>) converter);
        this.invalidateCache();
    }

    @Override
    public <U, V> void register(@NotNull final Class<? extends U> source, @NotNull final Class<V> target,
        @NotNull final ConverterFactory<?, ?> factory
    ) {
        this.register(new ConverterFactoryAdapter(factory, source, target));
    }

    @Override
    public boolean canConvert(@NotNull final Type sourceType, @NotNull final Type targetType) {
        Check.notNull(sourceType, "sourceType");
        Check.notNull(targetType, "targetType");
        return this.converter(GenericTypeReflector.box(sourceType), GenericTypeReflector.box(targetType)) != null;
    }

    @Override
    public @NotNull Object convert(@NotNull final Object source, @NotNull final Type sourceType, @NotNull final Type targetType)
        throws ConversionException {
        Check.notNull(source, "source");
        Check.notNull(targetType, "targetType");

        final Type boxedTargetType = GenericTypeReflector.box(targetType);
        final Converter<Object, Object> converter = this.converter(sourceType, boxedTargetType);

        if (converter == null) {
            // No Converter found
            throw new ConverterNotFoundException(sourceType, boxedTargetType);
        }

        return converter.convert(source, sourceType, boxedTargetType);
    }

    private @Nullable Converter<Object, Object> converter(@NotNull final Type sourceType, @NotNull final Type targetType) {
        return this.cache.computeIfAbsent(new Key(sourceType, targetType), param -> {
            for (final ConditionalConverter<Object, Object> conv : this.converters) {
                if (conv.matches(param.sourceType(), param.targetType())) {
                    return conv;
                }
            }

            if (GenericTypeReflector.isSuperType(sourceType, targetType)) {
                return NO_OP_CONVERTER;
            }

            return null;
        });
    }

    private void invalidateCache() {
        this.cache.clear();
    }

    private static final class ConverterAdapter implements ConditionalConverter<Object, Object> {

        private final Converter<Object, Object> converter;
        private final Type sourceType;
        private final Type targetType;

        private ConverterAdapter(final Converter<?, ?> converter, final Type sourceType, final Type targetType) {
            this.converter = (Converter<Object, Object>) converter;
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        @Override
        public @NotNull Object convert(@NotNull final Object source, @NotNull final Type sourceType,
            @NotNull final Type targetType
        ) throws ConversionException {
            return this.converter.convert(source, sourceType, targetType);
        }

        @Override
        public boolean matches(final @NotNull Type sourceType, final @NotNull Type targetType) {
            if (this.targetType != targetType) {
                return false;
            }

            return GenericTypeReflector.isSuperType(this.sourceType, sourceType);
        }
    }

    private static final class ConverterFactoryAdapter implements ConditionalConverter<Object, Object> {

        private final ConverterFactory<Object, Object> converterFactory;
        private final Class<?> sourceType;
        private final Class<?> targetType;

        private ConverterFactoryAdapter(final ConverterFactory<?, ?> converterFactory, final Class<?> sourceType,
            final Class<?> targetType
        ) {
            this.converterFactory = (ConverterFactory<Object, Object>) converterFactory;
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        @Override
        public @NotNull Object convert(@NotNull final Object source, @NotNull final Type sourceType,
            @NotNull final Type targetType
        ) throws ConversionException {
            return this.converterFactory.create(GenericTypeReflector.erase(targetType))
                .convert(source, sourceType, targetType);
        }

        @Override
        public boolean matches(@NotNull final Type sourceType, @NotNull final Type targetType) {
            if (this.converterFactory instanceof ConverterCondition condition && condition.matches(sourceType, targetType)) {
                try {
                    final Converter<?, ?> converter = this.converterFactory.create(GenericTypeReflector.erase(targetType));

                    if (converter instanceof ConverterCondition converterCondition) {
                        return converterCondition.matches(sourceType, targetType);
                    }
                } catch (final ConversionException ignored) {
                    return false;
                }
            }

            if (!GenericTypeReflector.isSuperType(this.targetType, targetType)) {
                return false;
            }

            return GenericTypeReflector.isSuperType(this.sourceType, sourceType);
        }
    }

    record Key(Type sourceType, Type targetType) {

        @Override
        public boolean equals(@Nullable final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Key otherKey)) {
                return false;
            }
            return (this.sourceType.equals(otherKey.sourceType)) && this.targetType.equals(otherKey.targetType);
        }
    }

    private static final class NoOpConverter implements ConditionalConverter<Object, Object> {

        private NoOpConverter() {

        }

        @Override
        public @NotNull Object convert(@NotNull final Object source, @NotNull final Type sourceType, @NotNull final Type targetType) {
            return source;
        }

        @Override
        public boolean matches(@NotNull final Type sourceType, @NotNull final Type targetType) {
            return true;
        }
    }
}
