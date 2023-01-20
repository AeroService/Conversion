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
import org.aero.common.core.validate.Check;
import org.aero.conversion.core.exception.ConversionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("MissingJavaDocType")
public interface FieldDiscoverer<T> {

    @SuppressWarnings("MissingJavaDocMethod")
    static @NotNull FieldDiscoverer<?> create() {
        return ObjectFieldDiscoverer.EMPTY_CONSTRUCTOR_INSTANCE;
    }

    @SuppressWarnings("MissingJavaDocMethod")
    static @NotNull FieldDiscoverer<?> create(@NotNull ThrowableFunction<Type, Supplier<Object>, ConversionException> instanceFactory) {
        Check.notNull(instanceFactory, "instanceFactory");
        return new ObjectFieldDiscoverer(instanceFactory, true);
    }

    @SuppressWarnings("MissingJavaDocMethod")
    <U> @Nullable Result<U, T> discover(@NotNull Type target) throws ConversionException;

    @SuppressWarnings("MissingJavaDocType")
    interface Result<T, U> {

        @SuppressWarnings("MissingJavaDocMethod")
        @NotNull List<MappingField<T, U>> mappingFields();

        @SuppressWarnings("MissingJavaDocMethod")
        @NotNull InstanceFactory<U> instanceFactory();

    }

    @SuppressWarnings("MissingJavaDocType")
    interface InstanceFactory<T> {

        @SuppressWarnings("MissingJavaDocMethod")
        T begin();

        @SuppressWarnings("MissingJavaDocMethod")
        void complete(Object value, T intermediate) throws ConversionException;

        @SuppressWarnings("MissingJavaDocMethod")
        Object complete(T intermediate) throws ConversionException;

    }
}
