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

import io.leangen.geantyref.TypeToken;
import org.aero.conversion.core.exception.ConversionException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class ConversionBusTest {

    @Test
    void test() {
        final ConversionBus conversionBus = ConversionBus.createDefault();

        try {
            final Map<String, Integer> in = new HashMap<>();
            in.put("a", 10);

            final Map<String, String> res = conversionBus.convert(in, new TypeToken<Map<String, Integer>>() {},
                new TypeToken<Map<String, String>>() {});

            System.out.println(res);
        } catch (final ConversionException e) {
            throw new RuntimeException(e);
        }
    }

    enum Mood {

        HAPPY,
        SAD

    }

    private static final class Lol {

        private final String name;

        private Lol(final String name) {
            this.name = name;
        }
    }
}
