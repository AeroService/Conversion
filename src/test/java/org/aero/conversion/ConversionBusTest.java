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

package org.aero.conversion;

import io.leangen.geantyref.TypeToken;
import java.util.HashMap;
import java.util.Map;
import org.aero.conversion.exception.ConversionException;
import org.junit.jupiter.api.Test;

class ConversionBusTest {

    @Test
    void test() {
        ConversionBus conversionBus = ConversionBus.createDefault();

        try {
            Map<String, Integer> in = new HashMap<>();
            in.put("a", 10);

            Map<String, String> res = conversionBus.convert(in, new TypeToken<Map<String, Integer>>() {}, new TypeToken<Map<String, String>>() {});

            System.out.println(res);
        } catch (ConversionException e) {
            throw new RuntimeException(e);
        }
    }

    enum Mood {

        HAPPY,
        SAD

    }

    static class Lol {

        private final String name;

        public Lol(String name) {
            this.name = name;
        }
    }
}
