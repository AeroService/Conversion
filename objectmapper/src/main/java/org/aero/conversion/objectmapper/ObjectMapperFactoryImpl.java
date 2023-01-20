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
import org.aero.common.core.validate.Check;
import org.aero.conversion.core.ConversionBus;
import org.aero.conversion.core.exception.ConversionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class ObjectMapperFactoryImpl implements ObjectMapper.Factory {

    static final ObjectMapper.Factory INSTANCE = ObjectMapper.factoryBuilder().addDiscoverer(FieldDiscoverer.create()).build();
    private static final int MAXIMUM_MAPPERS_SIZE = 64;

    private final Map<Type, ObjectMapper<?>> mappers = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<Type, ObjectMapper<?>> eldest) {
            return this.size() > MAXIMUM_MAPPERS_SIZE;
        }
    };
    private final List<FieldDiscoverer<?>> fieldDiscoverers;
    private final ConversionBus conversionBus;

    ObjectMapperFactoryImpl(final BuilderImpl builder) {
        this.fieldDiscoverers = new ArrayList<>(builder.discoverer);
        this.conversionBus = builder.conversionBus;
    }

    @Override
    public @NotNull ObjectMapper<?> get(@NotNull final Type type) throws ConversionException {
        Check.notNull(type, "type");
        if (GenericTypeReflector.isMissingTypeParameters(type)) {
            throw new ConversionException(type, "Raw types are not supported!");
        }

        synchronized (this.mappers) {
            ObjectMapper<?> objectMapper = this.mappers.get(type);

            if (objectMapper != null) {
                return objectMapper;
            }

            for (final FieldDiscoverer<?> discoverer : this.fieldDiscoverers) {
                objectMapper = this.createMapper(type, discoverer);
                if (objectMapper != null) {
                    this.mappers.put(type, objectMapper);
                    return objectMapper;
                }
            }

            throw new ConversionException(type, "Could not find factory for type " + type);
        }
    }

    private <T, U> ObjectMapper<T> createMapper(final Type type, final FieldDiscoverer<U> discoverer) throws ConversionException {
        final FieldDiscoverer.Result<T, U> result = discoverer.discover(type);

        if (result == null) {
            return null;
        }

        return new ObjectMapperImpl<>(type, result.mappingFields(), result.instanceFactory(), this.conversionBus);
    }

    static final class BuilderImpl implements ObjectMapper.Factory.Builder {

        private final List<FieldDiscoverer<?>> discoverer = new ArrayList<>();
        private ConversionBus conversionBus = ConversionBus.createDefault();

        @Override
        public @NotNull Builder addDiscoverer(@NotNull final FieldDiscoverer<?> discoverer) {
            this.discoverer.add(discoverer);
            return this;
        }

        @Override public @NotNull Builder conversionBus(@NotNull final ConversionBus conversionBus) {
            this.conversionBus = conversionBus;
            return this;
        }

        @Override
        public ObjectMapper.@UnknownNullability Factory build() {
            return new ObjectMapperFactoryImpl(this);
        }
    }
}
