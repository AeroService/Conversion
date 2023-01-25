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

import org.aero.conversion.objectmapper.ObjectMapper;
import org.aero.conversion.objectmapper.converter.MapToObjectConverterFactory;
import org.aero.conversion.objectmapper.converter.ObjectToMapConverter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings("MissingJavaDocType")
public final class ObjectMappingConversionBus extends DefaultConversionBus {

    private ObjectMappingConversionBus() {
        this.register();
    }

    @SuppressWarnings("MissingJavaDocMethod")
    public static @NotNull ConversionBus createDefault() {
        return new ObjectMappingConversionBus();
    }

    protected void register() {
        super.register();

        final ObjectMapper.Factory factory = ObjectMapper.factory();
        this.register(Object.class, Map.class, new ObjectToMapConverter(factory));
        this.register(Map.class, Object.class, new MapToObjectConverterFactory(factory));
    }
}
