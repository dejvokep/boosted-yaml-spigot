/*
 * Copyright 2024 https://dejvokep.dev/
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

import dev.dejvokep.boostedyaml.serialization.YamlSerializer;
import dev.dejvokep.boostedyaml.utils.supplier.MapSupplier;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A custom implementation for BoostedYAML's {@link YamlSerializer} designed to handle serialization of types defined
 * within the Spigot (Bukkit) APIs.
 * <p>
 * <b>If using this serializer,</b> register all types you need to {@link ConfigurationSerialization}. This serializer
 * internally uses and delegates the serialization logic to that class. Please refer to implementation details and
 * limitations imposed by that underlying class for further information on how to correctly register and design a custom
 * type serializer.
 * <p>
 * This class is designed around the singleton pattern. Use {@link #getInstance()} to obtain an instance of this
 * serializer.
 */
public class SpigotSerializer implements YamlSerializer {

    /**
     * The serializer instance.
     */
    private static final SpigotSerializer INSTANCE = new SpigotSerializer();

    /**
     * All supported abstract classes.
     */
    private static final Set<Class<?>> SUPPORTED_ABSTRACT_CLASSES = new HashSet<Class<?>>() {{
        add(ConfigurationSerializable.class);
    }};

    /**
     * Constructor of this serializer implementation.
     * <p>
     * This class is designed around the singleton pattern. Use {@link #getInstance()} to obtain an instance of this
     * serializer.
     */
    private SpigotSerializer() {
    }

    @Override
    @Nullable
    public Object deserialize(@NotNull Map<Object, Object> map) {
        // Does not contain the type key
        if (!map.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY))
            return null;

        // Not a valid class
        if (ConfigurationSerialization.getClassByAlias(map.get(ConfigurationSerialization.SERIALIZED_TYPE_KEY).toString()) == null)
            return null;

        // Convert to string keys
        Map<String, Object> converted = new HashMap<>();
        for (Map.Entry<Object, Object> entry : map.entrySet())
            converted.put(entry.getKey().toString(), entry.getValue());

        try {
            // Deserialize
            return ConfigurationSerialization.deserializeObject(converted);
        } catch (Exception ex) {
            // Edge case, handled by the underlying API
            return null;
        }
    }

    @Nullable
    @Override
    public <T> Map<Object, Object> serialize(@NotNull T object, @NotNull MapSupplier supplier) {
        // Output map
        Map<Object, Object> serialized = supplier.supply(1);
        ConfigurationSerializable cast = (ConfigurationSerializable) object;
        // Serialize
        serialized.putAll((cast).serialize());
        serialized.computeIfAbsent(ConfigurationSerialization.SERIALIZED_TYPE_KEY, k -> ConfigurationSerialization.getAlias(cast.getClass()));
        return serialized;
    }

    @NotNull
    @Override
    public Set<Class<?>> getSupportedClasses() {
        return Collections.emptySet();
    }

    @NotNull
    @Override
    public Set<Class<?>> getSupportedParentClasses() {
        return SUPPORTED_ABSTRACT_CLASSES;
    }

    /**
     * Returns the instance.
     *
     * @return the instance
     */
    public static SpigotSerializer getInstance() {
        return INSTANCE;
    }
}
