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

import io.leangen.geantyref.TypeToken;
import org.aero.conversion.core.exception.ConversionException;
import org.aero.conversion.core.exception.ConverterNotFoundException;
import org.aero.conversion.core.util.ConversionUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

@SuppressWarnings("MissingJavaDocType")
public sealed interface ConversionBus extends ConverterRegistry permits ConversionBusImpl {

    @SuppressWarnings("MissingJavaDocMethod")
    static @NotNull ConversionBus create() {
        return new ConversionBusImpl();
    }

    @SuppressWarnings("MissingJavaDocMethod")
    static @NotNull ConversionBus createDefault() {
        return new DefaultConversionBus();
    }

    @SuppressWarnings("MissingJavaDocMethod")
    boolean canConvert(@NotNull Type sourceType, @NotNull Type targetType);

    @SuppressWarnings("MissingJavaDocMethod")
    default boolean canConvert(@NotNull TypeToken<?> sourceTypeToken, @NotNull TypeToken<?> targetTypeToken) {
        return this.canConvert(sourceTypeToken.getType(), targetTypeToken.getType());
    }

    @SuppressWarnings("MissingJavaDocMethod")
    @NotNull Object convert(@NotNull Object source, @NotNull Type sourceType, @NotNull Type targetType) throws ConversionException;

    @SuppressWarnings({"unchecked", "MissingJavaDocMethod"})
    default <T> @NotNull T convert(@NotNull Object source, @NotNull Class<T> targetType) throws ConversionException {
        return (T) this.convert(source, source.getClass(), targetType);
    }

    @SuppressWarnings({"unchecked", "MissingJavaDocMethod"})
    default <T, U> @NotNull T convert(@NotNull U source, @NotNull TypeToken<U> sourceTypeToken, @NotNull TypeToken<T> targetTypeToken)
        throws ConversionException {
        return (T) this.convert(source, sourceTypeToken.getType(), targetTypeToken.getType());
    }

    @SuppressWarnings("MissingJavaDocMethod")
    default @NotNull Object convertToObject(@NotNull final Object source) throws ConversionException {
        final Class<?> sourceType = source.getClass();

        for (final Class<?> targetType : ConversionUtil.OBJECT_TYPES) {
            if (!this.canConvert(sourceType, targetType)) {
                continue;
            }

            try {
                return this.convert(source, targetType);
            } catch (ConversionException ignored) {
                System.out.println("failed");
            }
        }

        throw new ConverterNotFoundException(source.getClass());
    }
}
