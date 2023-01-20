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

package org.aero.conversion.core.converter;

import io.leangen.geantyref.GenericTypeReflector;
import org.aero.conversion.core.ConversionBus;
import org.aero.conversion.core.exception.ConversionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

@SuppressWarnings({"ClassCanBeRecord", "MissingJavaDocType"})
public class MapToMapConverter implements ConditionalConverter<Map<Object, Object>, Map<Object, Object>> {

    private final ConversionBus conversionBus;

    @SuppressWarnings("MissingJavaDocMethod")
    public MapToMapConverter(final ConversionBus conversionBus) {
        this.conversionBus = conversionBus;
    }

    @Override
    public boolean matches(@NotNull final Type sourceType, @NotNull final Type targetType) {
        final Type[] sourceElementTypes = this.elementTypes(sourceType);
        final Type[] targetElementTypes = this.elementTypes(targetType);

        if (sourceElementTypes == null || targetElementTypes == null) {
            return false;
        }

        return this.conversionBus.canConvert(sourceElementTypes[0], targetElementTypes[0])
            && this.conversionBus.canConvert(sourceElementTypes[1], targetElementTypes[1]);
    }

    @Override
    public @NotNull Map<Object, Object> convert(@NotNull final Map<Object, Object> source, @NotNull final Type sourceType,
        @NotNull final Type targetType
    ) throws ConversionException {
        final Class<?> erasedTargetType = GenericTypeReflector.erase(targetType);
        boolean copyRequired = !erasedTargetType.isInstance(source);

        if (!copyRequired && source.isEmpty()) {
            return source;
        }

        final Type[] sourceParams = this.elementTypes(sourceType);
        final Type[] targetParams = this.elementTypes(targetType);
        final List<Map.Entry<Object, Object>> targetEntries = new ArrayList<>(source.size());

        for (final Map.Entry<Object, Object> entry : source.entrySet()) {
            final Object sourceKey = entry.getKey();
            final Object sourceValue = entry.getValue();

            final Object targetKey = this.conversionBus.convert(sourceKey, sourceParams[0], targetParams[0]);
            final Object targetValue = this.conversionBus.convert(targetKey, sourceParams[0], targetParams[1]);
            targetEntries.add(new AbstractMap.SimpleEntry<>(targetKey, targetValue));

            if (sourceKey != targetKey || sourceValue != targetValue) {
                copyRequired = true;
            }
        }
        if (!copyRequired) {
            return source;
        }

        final Map<Object, Object> targetMap = this.createMap(erasedTargetType,
            GenericTypeReflector.erase(targetParams[0]), source.size());

        for (final Map.Entry<Object, Object> entry : targetEntries) {
            targetMap.put(entry.getKey(), entry.getValue());
        }

        return targetMap;

    }

    private @Nullable Type[] elementTypes(final Type type) {
        if (type instanceof ParameterizedType parameterizedType) {
            final Type[] typeArgs = parameterizedType.getActualTypeArguments();
            if (typeArgs.length != 2) {
                return null;
            }

            return typeArgs;
        }

        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Map<Object, Object> createMap(final Class<?> mapType, final Class<?> keyType, final int capacity) {
        if (EnumMap.class == mapType) {
            if (!Enum.class.isAssignableFrom(keyType)) {
                throw new IllegalArgumentException("Supplied type is not an enum: " + keyType.getName());
            }

            return new EnumMap(keyType.asSubclass(Enum.class));
        }

        if (SortedMap.class == mapType || NavigableMap.class == mapType) {
            return new TreeMap<>();
        }

        return new LinkedHashMap<>(capacity);
    }
}
