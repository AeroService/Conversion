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

import io.leangen.geantyref.GenericTypeReflector;
import org.aero.common.core.function.ThrowableFunction;
import org.aero.common.core.validate.Check;
import org.aero.conversion.core.ConversionBus;
import org.aero.conversion.core.exception.ConversionException;
import org.aero.conversion.objectmapper.discoverer.FieldDiscoverer;
import org.aero.conversion.objectmapper.exception.ObjectMapperException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "ClassCanBeRecord"})
final class ObjectMapperImpl<T, U> implements ObjectMapper<T> {

    private final Type type;
    private final List<FieldDiscoverer.FieldData<T, U>> fieldData;
    private final FieldDiscoverer.InstanceFactory<U> instanceFactory;
    private final ConversionBus conversionBus;

    ObjectMapperImpl(final Type type, final List<FieldDiscoverer.FieldData<T, U>> fieldData, final FieldDiscoverer.InstanceFactory<U> instanceFactory,
        final ConversionBus conversionBus
    ) {
        this.type = type;
        this.fieldData = Collections.unmodifiableList(fieldData);
        this.instanceFactory = instanceFactory;
        this.conversionBus = conversionBus;
    }

    @Override
    public @NotNull T load(@NotNull final Map<String, Object> source) throws ConversionException {
        Check.notNull(source, "source");
        return this.load(source, intermediate -> (T) this.instanceFactory.complete(intermediate));
    }

    @Override
    public void load(@NotNull final T value, @NotNull final Map<String, Object> source) throws ConversionException {
        Check.notNull(value, "value");
        Check.notNull(source, "source");
        this.load(source, intermediate -> {
            this.instanceFactory.complete(value, intermediate);
            return value;
        });
    }

    private T load(final Map<String, Object> source, final ThrowableFunction<U, T, ConversionException> completer) throws ConversionException {
        final U fieldData = this.instanceFactory.begin();

        for (final FieldDiscoverer.FieldData<T, U> field : this.fieldData) {
            final Object mapValue = source.get(field.name());

            if (mapValue == null) {
                continue;
            }

            final Type fieldType = field.type();
            final Class<?> erasedBoxedType = GenericTypeReflector.erase(GenericTypeReflector.box(fieldType));
            final Object fieldValue = this.conversionBus.convert(mapValue, mapValue.getClass(), fieldType);

            if (!erasedBoxedType.isInstance(fieldValue)) {
                throw new ObjectMapperException("Object " + fieldValue + " is not of expected type " + fieldType);
            }

            field.deserializer().accept(fieldData, fieldValue);
        }

        return completer.apply(fieldData);
    }

    @Override
    public @NotNull Map<String, Object> save(@NotNull final T value) throws ConversionException {
        Check.notNull(value, "value");
        final Map<String, Object> target = new HashMap<>();

        this.save(target, value);
        return target;
    }

    @Override
    public void save(@NotNull final Map<String, Object> target, @NotNull final T value) throws ConversionException {
        Check.notNull(target, "target");
        Check.notNull(value, "value");
        for (final FieldDiscoverer.FieldData<T, U> fieldData : this.fieldData) {
            try {
                final Object fieldValue = fieldData.serializer().apply(value);

                if (fieldValue == null) {
                    target.put(fieldData.name(), null);
                    continue;
                }

                final Object mapValue = this.conversionBus.convertToObject(fieldValue);

                target.put(fieldData.name(), mapValue);
            } catch (final IllegalAccessException e) {
                throw new ObjectMapperException(fieldData.type(), e);
            }
        }
    }
}
