package org.clockworx.battlearena.config;

import org.clockworx.battlearena.Arena;
import org.bukkit.block.data.BlockData;

final class DefaultSerializers {

    static void register() {
        ArenaConfigSerializer.registerSerializer(Arena.class, (node, section, type) -> {
            section.set(node, type.getName());
        });

        ArenaConfigSerializer.registerSerializer(BlockData.class, (node, section, type) -> {
            section.set(node, type.getAsString());
        });
    }
}
