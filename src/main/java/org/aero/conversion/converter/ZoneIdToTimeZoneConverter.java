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

import java.lang.reflect.Type;
import java.time.ZoneId;
import java.util.TimeZone;
import org.jetbrains.annotations.NotNull;

public class ZoneIdToTimeZoneConverter implements Converter<ZoneId, TimeZone> {

    @Override
    public @NotNull TimeZone convert(@NotNull ZoneId source, @NotNull Type sourceType,
        @NotNull Type targetType) {
        return TimeZone.getTimeZone(source);
    }
}