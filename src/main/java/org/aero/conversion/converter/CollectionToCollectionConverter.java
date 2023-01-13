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

package org.aero.conversion.converter;

import io.leangen.geantyref.GenericTypeReflector;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.aero.conversion.ConversionBus;
import org.aero.conversion.exception.ConversionException;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ClassCanBeRecord")
public class CollectionToCollectionConverter implements ConditionalConverter<Collection<Object>, Collection<Object>> {

    private final ConversionBus conversionBus;

    public CollectionToCollectionConverter(ConversionBus conversionBus) {
        this.conversionBus = conversionBus;
    }

    @Override
    public boolean matches(Type sourceType, Type targetType) {
        Type sourceParams = this.getElementType(sourceType);
        Type targetParams = this.getElementType(targetType);

        if (sourceParams == null || targetParams == null) {
            return false;
        }

        return this.conversionBus.canConvert(sourceParams, targetParams);
    }

    @Override
    public @NotNull Collection<Object> convert(@NotNull Collection<Object> source, @NotNull Type sourceType,
        @NotNull Type targetType) throws ConversionException
    {
        Class<?> erasedTargetType = GenericTypeReflector.erase(targetType);

        boolean copyRequired = !erasedTargetType.isInstance(source);
        if (!copyRequired && source.isEmpty()) {
            return source;
        }

        Type sourceElementType = this.getElementType(sourceType);
        Type targetElementType = this.getElementType(targetType);

        Collection<Object> target = this.createCollection(erasedTargetType,
            GenericTypeReflector.erase(targetElementType), source.size());

        for (Object sourceElement : source) {
            Object targetElement = this.conversionBus.convert(sourceElement, sourceElementType, targetElementType);
            target.add(targetElement);
            if (sourceElement != targetElement) {
                copyRequired = true;
            }
        }

        return (copyRequired ? target : source);
    }

    private Type getElementType(Type type) {
        if (type instanceof ParameterizedType parameterizedType) {
            Type[] typeArgs = parameterizedType.getActualTypeArguments();
            if (typeArgs.length != 1) {
                return null;
            }

            return typeArgs[0];
        }

        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private @NotNull Collection<Object> createCollection(Class<?> collectionType, Class<?> elementType, int capacity) {
        if (LinkedHashSet.class == collectionType || HashSet.class == collectionType ||
            Set.class == collectionType || Collection.class == collectionType
        ) {
            return new LinkedHashSet<>(capacity);
        } else if (LinkedList.class == collectionType) {
            return new LinkedList<>();
        } else if (TreeSet.class == collectionType || NavigableSet.class == collectionType
            || SortedSet.class == collectionType
        ) {
            return new TreeSet<>();
        } else if (EnumSet.class.isAssignableFrom(collectionType)) {
            if (!Enum.class.isAssignableFrom(elementType)) {
                throw new IllegalArgumentException("Supplied type is not an enum: " + elementType.getName());
            }

            return EnumSet.noneOf(elementType.<Enum>asSubclass(Enum.class));
        }

        return new ArrayList<>();
    }
}
