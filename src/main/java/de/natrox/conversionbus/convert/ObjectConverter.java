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
import de.natrox.conversionbus.objectmapping.ObjectMapper;
import io.leangen.geantyref.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;

public interface ObjectConverter<T> extends Converter<Map<String, Object>, T> {

    static <T> @NotNull ObjectConverter<T> create(@NotNull Type type, @NotNull ObjectMapper.Factory factory) {
        Check.notNull(type, "type");
        Check.notNull(factory, "factory");
        return new ObjectConverterImpl<>(type, factory);
    }

    static <T> @NotNull ObjectConverter<T> create(@NotNull Type type) {
        Check.notNull(type, "type");
        return create(type, ObjectMapper.factory());
    }

    static <T> @NotNull ObjectConverter<T> create(@NotNull Class<T> type, @NotNull ObjectMapper.Factory factory) {
        Check.notNull(type, "type");
        Check.notNull(factory, "factory");
        return create((Type) type, factory);
    }

    static <T> @NotNull ObjectConverter<T> create(@NotNull Class<T> type) {
        Check.notNull(type, "type");
        return create((Type) type, ObjectMapper.factory());
    }

    static <T> @NotNull ObjectConverter<T> create(@NotNull TypeToken<T> typeToken, @NotNull ObjectMapper.Factory factory) {
        Check.notNull(typeToken, "typeToken");
        Check.notNull(factory, "factory");
        return create(typeToken.getType(), factory);
    }

    static <T> @NotNull ObjectConverter<T> create(@NotNull TypeToken<T> typeToken) {
        Check.notNull(typeToken, "typeToken");
        return create(typeToken.getType(), ObjectMapper.factory());
    }
}