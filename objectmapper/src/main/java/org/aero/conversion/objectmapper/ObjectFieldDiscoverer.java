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
import org.aero.conversion.core.exception.ConversionException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
final class ObjectFieldDiscoverer implements FieldDiscoverer<Map<Field, Object>> {

    static final ObjectFieldDiscoverer EMPTY_CONSTRUCTOR_INSTANCE = new ObjectFieldDiscoverer(new EmptyConstructorFactory(), false);

    private final ThrowableFunction<Type, Supplier<Object>, ConversionException> instanceFactory;
    private final boolean requiresInstanceCreation;

    ObjectFieldDiscoverer(final ThrowableFunction<Type, Supplier<Object>, ConversionException> instanceFactory,
        final boolean requiresInstanceCreation
    ) {
        this.instanceFactory = instanceFactory;
        this.requiresInstanceCreation = requiresInstanceCreation;
    }

    @Override
    public <U> Result<U, Map<Field, Object>> discover(@NotNull final Type targetType) throws ConversionException {
        final Class<?> erasedTargetType = GenericTypeReflector.erase(targetType);
        if (erasedTargetType.isInterface()) {
            throw new ConversionException(targetType, "ObjectMapper can only work with concrete types");
        }

        final Supplier<Object> maker = this.instanceFactory.apply(targetType);
        if (maker == null && this.requiresInstanceCreation) {
            return null;
        }

        final List<MappingField<U, Map<Field, Object>>> mappingFields = new ArrayList<>();
        Type collectType = targetType;
        Class<?> collectClass = erasedTargetType;
        while (true) {
            for (final Field field : collectClass.getDeclaredFields()) {
                if ((field.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) != 0) {
                    continue;
                }

                field.setAccessible(true);
                final Type fieldType = GenericTypeReflector.getFieldType(field, collectType);
                mappingFields.add(new MappingField<>(
                    field.getName(),
                    fieldType,
                    (intermediate, value) -> intermediate.put(field, value),
                    field::get
                ));
            }

            collectClass = collectClass.getSuperclass();
            if (collectClass.equals(Object.class)) {
                break;
            }
            collectType = GenericTypeReflector.getExactSuperType(collectType, collectClass);
        }

        return new FieldDiscovererResultImpl<>(mappingFields, new InstanceFactory<>() {
            @Override
            public Map<Field, Object> begin() {
                return new HashMap<>();
            }

            @Override
            public void complete(final Object instance, final Map<Field, Object> fieldData) throws ConversionException {
                for (final Map.Entry<Field, Object> entry : fieldData.entrySet()) {
                    try {
                        entry.getKey().set(instance, entry.getValue());
                    } catch (final IllegalAccessException e) {
                        throw new ConversionException(targetType, e);
                    }
                }
            }

            @Override
            public Object complete(final Map<Field, Object> fieldData) throws ConversionException {
                final Object instance = maker == null ? null : maker.get();
                if (instance == null) {
                    throw new ConversionException(targetType, "Unable to create instances for this type");
                }
                this.complete(instance, fieldData);
                return instance;
            }
        });
    }

    private static final class EmptyConstructorFactory implements ThrowableFunction<Type, Supplier<Object>, ConversionException> {

        @Override
        public Supplier<Object> apply(final Type type) {
            try {
                final Constructor<?> constructor = GenericTypeReflector.erase(type).getDeclaredConstructor();
                constructor.setAccessible(true);
                return () -> {
                    try {
                        return constructor.newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                };
            } catch (final NoSuchMethodException ignored) {
                return null;
            }
        }
    }
}
