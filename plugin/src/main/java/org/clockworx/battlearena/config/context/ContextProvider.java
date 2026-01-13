package org.clockworx.battlearena.config.context;

import org.clockworx.battlearena.config.ArenaOption;
import org.clockworx.battlearena.config.ParseException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public interface ContextProvider<T> {

    T provideInstance(@Nullable Path sourceFile, ArenaOption option, Class<?> type, ConfigurationSection configuration, String name, @Nullable Object scope) throws ParseException;
}
