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
import org.aero.conversion.core.exception.ConversionException;
import org.aero.conversion.objectmapper.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes", "MissingJavaDocType"})
public class ObjectToMapConverter implements Converter<Object, Map> {

    private final ObjectMapper.Factory objectMapperFactory;

    @SuppressWarnings("MissingJavaDocMethod")
    public ObjectToMapConverter(@NotNull final ObjectMapper.Factory objectMapperFactory) {
        this.objectMapperFactory = objectMapperFactory;
    }

    @Override
    public @NotNull Map<String, Object> convert(@NotNull final Object source, @NotNull final Type sourceType, @NotNull final Type targetType)
        throws ConversionException {
        final ObjectMapper<Object> objectMapper = (ObjectMapper<Object>) this.objectMapperFactory.get(sourceType);

        return objectMapper.save(source);
    }
}
