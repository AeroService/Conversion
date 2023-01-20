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

package org.aero.conversion.objectmapper.exception;

import org.aero.conversion.core.exception.ConversionException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@SuppressWarnings("MissingJavaDocType")
public class ObjectMapperException extends ConversionException {

    @SuppressWarnings("MissingJavaDocMethod")
    public ObjectMapperException() {

    }

    @SuppressWarnings("MissingJavaDocMethod")
    public ObjectMapperException(final String message) {
        super(message);
    }

    @SuppressWarnings("MissingJavaDocMethod")
    public ObjectMapperException(final Throwable cause) {
        super(cause);
    }

    @SuppressWarnings("MissingJavaDocMethod")
    public ObjectMapperException(@Nullable final Type expectedType, final String message) {
        super(expectedType, message);
    }

    @SuppressWarnings("MissingJavaDocMethod")
    public ObjectMapperException(@Nullable final Type expectedType, final Throwable cause) {
        super(expectedType, cause);
    }

    @SuppressWarnings("MissingJavaDocMethod")
    public ObjectMapperException(@Nullable final Type expectedType, final String message, final Throwable cause) {
        super(expectedType, message, cause);
    }
}
