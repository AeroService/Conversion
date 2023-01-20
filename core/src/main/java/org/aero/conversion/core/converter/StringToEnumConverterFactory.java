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

import org.aero.conversion.core.exception.ConversionException;
import org.aero.conversion.core.exception.ConversionFailedException;
import org.aero.conversion.core.util.ConversionUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

@SuppressWarnings({"rawtypes", "unchecked", "MissingJavaDocType"})
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    @Override
    public <T extends Enum> Converter<String, T> create(final Class<T> targetType) {
        return new StringToEnum(ConversionUtil.enumType(targetType));
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static final class StringToEnum<T extends Enum> implements Converter<String, T> {

        private final Class<T> enumType;

        private StringToEnum(final Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public @NotNull T convert(@NotNull final String source, @NotNull final Type sourceType, @NotNull final Type targetType)
            throws ConversionException {
            if (source.isEmpty()) {
                // It's an empty enum identifier: reset the enum value to null.
                throw new ConversionFailedException(sourceType, targetType);
            }
            return (T) Enum.valueOf(this.enumType, source.trim());
        }
    }

}
