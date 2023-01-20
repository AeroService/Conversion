/*
 * Copyright 2020-2022 NatroxMC
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

package org.aero.conversion.objectmapper;

import io.leangen.geantyref.TypeToken;
import org.aero.common.core.builder.IBuilder;
import org.aero.common.core.validate.Check;
import org.aero.conversion.core.ConversionBus;
import org.aero.conversion.core.exception.ConversionException;
import org.aero.conversion.objectmapper.discoverer.FieldDiscoverer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;

@SuppressWarnings("MissingJavaDocType")
public interface ObjectMapper<T> {

    @SuppressWarnings("MissingJavaDocMethod")
    static @NotNull Factory factory() {
        return ObjectMapperFactoryImpl.INSTANCE;
    }

    @SuppressWarnings("MissingJavaDocMethod")
    static @NotNull Factory.Builder factoryBuilder() {
        return new ObjectMapperFactoryImpl.BuilderImpl();
    }

    @SuppressWarnings("MissingJavaDocMethod")
    @NotNull T load(@NotNull Map<String, Object> source) throws ConversionException;

    @SuppressWarnings("MissingJavaDocMethod")
    void load(@NotNull T value, @NotNull Map<String, Object> source) throws ConversionException;

    @SuppressWarnings("MissingJavaDocMethod")
    @NotNull Map<String, Object> save(@NotNull T value) throws ConversionException;

    @SuppressWarnings("MissingJavaDocMethod")
    void save(@NotNull Map<String, Object> target, @NotNull T value) throws ConversionException;

    @SuppressWarnings("MissingJavaDocType")
    interface Factory {

        @SuppressWarnings({"unchecked", "MissingJavaDocMethod"})
        default <V> @NotNull ObjectMapper<V> get(@NotNull TypeToken<V> type) throws ConversionException {
            Check.notNull(type, "type");
            return (ObjectMapper<V>) get(type.getType());
        }

        @SuppressWarnings({"unchecked", "MissingJavaDocMethod"})
        default <V> @NotNull ObjectMapper<V> get(@NotNull Class<V> type) throws ConversionException {
            Check.notNull(type, "type");
            return (ObjectMapper<V>) get((Type) type);
        }

        @SuppressWarnings("MissingJavaDocMethod")
        @NotNull ObjectMapper<?> get(@NotNull Type type) throws ConversionException;

        @SuppressWarnings("MissingJavaDocType")
        interface Builder extends IBuilder<Factory> {

            @SuppressWarnings("MissingJavaDocMethod")
            @NotNull Builder addDiscoverer(@NotNull FieldDiscoverer<?> discoverer);

            @SuppressWarnings("MissingJavaDocMethod")
            @NotNull Builder conversionBus(@NotNull ConversionBus conversionBus);

        }
    }
}
