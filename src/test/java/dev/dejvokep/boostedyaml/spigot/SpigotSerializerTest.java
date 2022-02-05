/*
 * Copyright 2022 https://dejvokep.dev/
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
package dev.dejvokep.boostedyaml.spigot;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SpigotSerializerTest {

    @Test
    void deserialize() {
        // Register
        ConfigurationSerialization.registerClass(CustomType.class);
        ConfigurationSerialization.registerClass(CustomType.class, "custom");
        // Deserialize
        CustomType deserialized = (CustomType) ConfigurationSerialization.deserializeObject(new HashMap<String, Object>(){{
            put("==", CustomType.class.getName());
            put("value", 5);
        }});
        // Assert
        assertNotNull(deserialized);
        assertEquals(5, deserialized.getValue());
        // Deserialize
        deserialized = (CustomType) ConfigurationSerialization.deserializeObject(new HashMap<String, Object>(){{
            put("==", "custom");
            put("value", 7);
        }});
        // Assert
        assertNotNull(deserialized);
        assertEquals(7, deserialized.getValue());
    }

    @Test
    void serialize() {
        // Register
        ConfigurationSerialization.registerClass(CustomType.class);
        // Try to serialize
        assertEquals(new HashMap<Object, Object>(){{
            put("value", 20);
        }}, new CustomType(20).serialize());
        assertEquals(new HashMap<Object, Object>(){{
            put("value", 50);
        }}, new CustomType(50).serialize());
    }

    @Test
    void getSupportedClasses() {
        assertEquals(Collections.emptySet(), SpigotSerializer.getInstance().getSupportedClasses());
    }

    @Test
    void getSupportedParentClasses() {
        assertEquals(1, SpigotSerializer.getInstance().getSupportedParentClasses().size());
        assertEquals(ConfigurationSerializable.class, SpigotSerializer.getInstance().getSupportedParentClasses().iterator().next());
    }

    public static class CustomType implements ConfigurationSerializable {

        private final int value;

        private CustomType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @NotNull
        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("value", value);
            return map;
        }

        public static CustomType deserialize(Map<String, Object> map) {
            return new CustomType((int) map.get("value"));
        }
    }

}