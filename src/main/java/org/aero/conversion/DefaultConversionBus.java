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

import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;
import org.aero.conversion.converter.CharacterToNumberFactory;
import org.aero.conversion.converter.CollectionToCollectionConverter;
import org.aero.conversion.converter.MapToMapConverter;
import org.aero.conversion.converter.NumberToCharacterConverter;
import org.aero.conversion.converter.NumberToNumberConverterFactory;
import org.aero.conversion.converter.StringToNumberConverterFactory;
import org.aero.conversion.converter.EnumToIntegerConverter;
import org.aero.conversion.converter.EnumToStringConverter;
import org.aero.conversion.converter.ObjectToStringConverter;
import org.aero.conversion.converter.StringToBooleanConverter;
import org.aero.conversion.converter.StringToCharacterConverter;
import org.aero.conversion.converter.StringToCharsetConverter;
import org.aero.conversion.converter.StringToCurrencyConverter;
import org.aero.conversion.converter.StringToEnumConverterFactory;
import org.aero.conversion.converter.StringToUUIDConverter;

final class DefaultConversionBus extends ConversionBusImpl {

    public DefaultConversionBus() {
        // -> Number
        this.register(String.class, Number.class, new StringToNumberConverterFactory());
        this.register(Character.class, Number.class, new CharacterToNumberFactory(this));
        this.register(Number.class, Number.class, new NumberToNumberConverterFactory());
        // -> Integer
        this.register(Enum.class, Integer.class, new EnumToIntegerConverter());
        // -> Boolean
        this.register(String.class, Boolean.class, new StringToBooleanConverter());
        // -> Character
        this.register(String.class, Character.class, new StringToCharacterConverter());
        this.register(Number.class, Character.class, new NumberToCharacterConverter());
        // -> Charset
        this.register(String.class, Charset.class, new StringToCharsetConverter());
        // -> Currency
        this.register(String.class, Currency.class, new StringToCurrencyConverter());
        // -> UUID
        this.register(String.class, UUID.class, new StringToUUIDConverter());
        // -> Enum
        this.register(String.class, Enum.class, new StringToEnumConverterFactory());
        // -> String
        this.register(Number.class, String.class, new ObjectToStringConverter());
        this.register(Character.class, String.class, new ObjectToStringConverter());
        this.register(Boolean.class, String.class, new ObjectToStringConverter());
        this.register(Enum.class, String.class, new EnumToStringConverter());
        this.register(Locale.class, String.class, new ObjectToStringConverter());
        this.register(Charset.class, String.class, new ObjectToStringConverter());
        this.register(Currency.class, String.class, new ObjectToStringConverter());
        this.register(UUID.class, String.class, new ObjectToStringConverter());
        // -> Collection
        this.register(new CollectionToCollectionConverter(this));
        // -> Map
        this.register(new MapToMapConverter(this));
    }
}