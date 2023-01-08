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

import dev.dejvokep.boostedyaml.serialization.YamlSerializer;
import dev.dejvokep.boostedyaml.utils.supplier.MapSupplier;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Custom serializer singleton which supports {@link ConfigurationSerialization}.
 */
public class SpigotSerializer implements YamlSerializer {

    /**
     * Serializer instance.
     */
    private static final SpigotSerializer INSTANCE = new SpigotSerializer();

    /**
     * All supported abstract classes.
     */
    private static final Set<Class<?>> SUPPORTED_ABSTRACT_CLASSES = new HashSet<Class<?>>() {{
        add(ConfigurationSerializable.class);
    }};

    @Override
    @Nullable
    public Object deserialize(@NotNull Map<Object, Object> map) {
        //If does not contain the key
        if (!map.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY))
            return null;

        //If is not a valid class
        if (ConfigurationSerialization.getClassByAlias(map.get(ConfigurationSerialization.SERIALIZED_TYPE_KEY).toString()) == null)
            return null;

        //Create a map
        Map<String, Object> converted = new HashMap<>();
        //Iterate through all entries
        for (Map.Entry<Object, Object> entry : map.entrySet())
            //Add
            converted.put(entry.getKey().toString(), entry.getValue());

        try {
            //Deserialize
            return ConfigurationSerialization.deserializeObject(converted);
        } catch (Exception ex) {
            return null;
        }
    }

    @Nullable
    @Override
    public <T> Map<Object, Object> serialize(@NotNull T object, @NotNull MapSupplier supplier) {
        //Create a map
        Map<Object, Object> serialized = supplier.supply(1);
        //Cast
        ConfigurationSerializable cast = (ConfigurationSerializable) object;
        //Add
        serialized.putAll((cast).serialize());
        serialized.computeIfAbsent(ConfigurationSerialization.SERIALIZED_TYPE_KEY, k -> ConfigurationSerialization.getAlias(cast.getClass()));
        //Return
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
