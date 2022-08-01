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

package de.natrox.conversionbus.convert;

import de.natrox.common.validate.Check;
import de.natrox.conversionbus.ConversionBus;
import io.leangen.geantyref.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;

public interface MapConverter<T, U> extends Converter<Object, Map<T, U>> {

    static <T, U> @NotNull MapConverter<T, U> create(@NotNull Type type, @NotNull ConversionBus collection) {
        Check.notNull(type, "type");
        Check.notNull(collection, "collection");
        return MapConverterImpl.create(type, collection);
    }

    static <T, U> @NotNull MapConverter<T, U> create(@NotNull TypeToken<Map<T, U>> typeToken, @NotNull ConversionBus collection) {
        Check.notNull(typeToken, "typeToken");
        Check.notNull(collection, "collection");
        return create(typeToken.getType(), collection);
    }

    static <T, U> @NotNull MapConverter<T, U> create(@NotNull TypeToken<Map<T, U>> typeToken) {
        Check.notNull(typeToken, "typeToken");
        return create(typeToken.getType(), ConversionBus.defaults());
    }

    static <T, U> @NotNull MapConverter<T, U> create(@NotNull Type type) {
        Check.notNull(type, "type");
        return create(type, ConversionBus.defaults());
    }
}