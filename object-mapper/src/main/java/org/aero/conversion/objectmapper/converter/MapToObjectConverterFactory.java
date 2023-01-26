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

package org.aero.conversion.objectmapper.converter;

import org.aero.conversion.core.converter.Converter;
import org.aero.conversion.core.converter.ConverterFactory;
import org.aero.conversion.core.exception.ConversionException;
import org.aero.conversion.objectmapper.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;

@SuppressWarnings({"rawtypes", "ClassCanBeRecord", "MissingJavaDocType"})
public class MapToObjectConverterFactory implements ConverterFactory<Map, Object> {

    private final ObjectMapper.Factory objectMapperFactory;

    @SuppressWarnings("MissingJavaDocMethod")
    public MapToObjectConverterFactory(@NotNull final ObjectMapper.Factory objectMapperFactory) {
        this.objectMapperFactory = objectMapperFactory;
    }

    @Override
    public @NotNull <V> Converter<Map, V> create(final Class<V> type) throws ConversionException {
        return new MapToObject<>(this.objectMapperFactory.get(type));
    }

    @SuppressWarnings({"ClassCanBeRecord", "unchecked"})
    private static final class MapToObject<T> implements Converter<Map, T> {

        private final ObjectMapper<T> objectMapper;

        private MapToObject(final ObjectMapper<T> objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public @NotNull T convert(@NotNull final Map source, @NotNull final Type sourceType, @NotNull final Type targetType)
            throws ConversionException {
            return (T) this.objectMapper.load(source);
        }
    }
}
