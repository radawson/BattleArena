package org.clockworx.battlearena.module.vault;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Reflection-based adapter for Vault-compatible APIs (ServiceIO or Vault).
 * This avoids hard-linking to Vault classes in our own classloader, which
 * keeps the module safe to load even when Vault is not installed.
 */
public class VaultContainer {
    private static final String ECONOMY_CLASS = "net.milkbowl.vault.economy.Economy";
    private static final String PERMISSION_CLASS = "net.milkbowl.vault.permission.Permission";

    private final ClassLoader apiClassLoader;
    private final Object economyProvider;
    private final Object permissionProvider;

    private final Method depositPlayerMethod;
    private final Method depositPlayerBankMethod;
    private final Method addPermissionMethod;
    private final Method addTransientPermissionMethod;
    private final Method removePermissionMethod;
    private final Method removeTransientPermissionMethod;

    /**
     * Creates a Vault container bound to the provider plugin's classloader.
     * The provider plugin is either ServiceIO (preferred) or Vault.
     *
     * @param providerPlugin The plugin supplying the Vault API classes.
     */
    public VaultContainer(Plugin providerPlugin) {
        this.apiClassLoader = providerPlugin.getClass().getClassLoader();
        this.economyProvider = resolveProvider(ECONOMY_CLASS);
        this.permissionProvider = resolveProvider(PERMISSION_CLASS);

        this.depositPlayerMethod = findCompatibleMethod(this.economyProvider, "depositPlayer", OfflinePlayer.class, double.class);
        this.depositPlayerBankMethod = findCompatibleMethod(this.economyProvider, "depositPlayer", OfflinePlayer.class, String.class, double.class);
        this.addPermissionMethod = findCompatibleMethod(this.permissionProvider, "playerAdd", Player.class, String.class);
        this.addTransientPermissionMethod = findCompatibleMethod(this.permissionProvider, "playerAddTransient", Player.class, String.class);
        this.removePermissionMethod = findCompatibleMethod(this.permissionProvider, "playerRemove", Player.class, String.class);
        this.removeTransientPermissionMethod = findCompatibleMethod(this.permissionProvider, "playerRemoveTransient", Player.class, String.class);
    }

    /**
     * Checks whether the provider plugin exposes the Vault API classes.
     *
     * @param plugin The plugin to inspect.
     * @return True if the required Vault API classes are present.
     */
    public static boolean isVaultApiAvailable(Plugin plugin) {
        return isClassAvailable(plugin, ECONOMY_CLASS) && isClassAvailable(plugin, PERMISSION_CLASS);
    }

    public void editCurrency(Player player, double amount) {
        invokeIfPresent(this.depositPlayerMethod, this.economyProvider, player, amount);
    }

    public void editCurrency(Player player, String bank, double amount) {
        invokeIfPresent(this.depositPlayerBankMethod, this.economyProvider, player, bank, amount);
    }

    public void addPermission(Player player, String permission, boolean isTransient) {
        if (isTransient) {
            invokeIfPresent(this.addTransientPermissionMethod, this.permissionProvider, player, permission);
        } else {
            invokeIfPresent(this.addPermissionMethod, this.permissionProvider, player, permission);
        }
    }

    public void removePermission(Player player, String permission, boolean isTransient) {
        if (isTransient) {
            invokeIfPresent(this.removeTransientPermissionMethod, this.permissionProvider, player, permission);
        } else {
            invokeIfPresent(this.removePermissionMethod, this.permissionProvider, player, permission);
        }
    }

    /**
     * Returns true when an economy provider is present and supports deposits.
     */
    public boolean hasEconomySupport() {
        return this.economyProvider != null && this.depositPlayerMethod != null;
    }

    @Nullable
    private Object resolveProvider(String className) {
        Class<?> apiClass = loadApiClass(className);
        if (apiClass == null) {
            return null;
        }

        RegisteredServiceProvider<?> registration = Bukkit.getServer().getServicesManager().getRegistration(apiClass);
        if (registration == null) {
            return null;
        }

        return registration.getProvider();
    }

    @Nullable
    private Class<?> loadApiClass(String className) {
        try {
            return this.apiClassLoader.loadClass(className);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    private static boolean isClassAvailable(Plugin plugin, String className) {
        try {
            plugin.getClass().getClassLoader().loadClass(className);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    @Nullable
    private static Method findCompatibleMethod(@Nullable Object target, String name, Class<?>... argTypes) {
        if (target == null) {
            return null;
        }

        for (Method method : target.getClass().getMethods()) {
            if (!method.getName().equals(name) || method.getParameterCount() != argTypes.length) {
                continue;
            }

            Class<?>[] paramTypes = method.getParameterTypes();
            boolean compatible = true;
            for (int i = 0; i < paramTypes.length; i++) {
                if (!paramTypes[i].isAssignableFrom(argTypes[i])) {
                    compatible = false;
                    break;
                }
            }

            if (compatible) {
                return method;
            }
        }

        return null;
    }

    private static void invokeIfPresent(@Nullable Method method, @Nullable Object target, Object... args) {
        if (method == null || target == null) {
            return;
        }

        try {
            method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
            // Intentionally ignored to keep optional integrations non-fatal.
        }
    }
}
