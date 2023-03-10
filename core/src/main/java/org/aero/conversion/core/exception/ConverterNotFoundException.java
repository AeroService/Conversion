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

package org.aero.conversion.core.exception;

import java.lang.reflect.Type;

@SuppressWarnings("MissingJavaDocType")
public class ConverterNotFoundException extends ConversionException {

    @SuppressWarnings("MissingJavaDocMethod")
    public ConverterNotFoundException(final Type sourceType, final Type targetType) {
        super(sourceType, "Failed to find converter which converts the source value of type " + sourceType.getTypeName()
            + " to target value of type " + targetType.getTypeName());
    }

    @SuppressWarnings("MissingJavaDocMethod")
    public ConverterNotFoundException(final Type sourceType) {
        super(sourceType, "Failed to find converter which converts the source value of type " + sourceType.getTypeName()
            + " to any correct target type");
    }
}
