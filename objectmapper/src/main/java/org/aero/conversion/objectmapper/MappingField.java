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

import org.aero.common.core.function.ThrowableFunction;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;

record MappingField<T, U>(String name, Type type, Deserializer<U> deserializer, Serializer<T> serializer) {

    public interface Deserializer<T> extends BiConsumer<T, Object> {

    }

    interface Serializer<T> extends ThrowableFunction<T, Object, IllegalAccessException> {

    }
}
