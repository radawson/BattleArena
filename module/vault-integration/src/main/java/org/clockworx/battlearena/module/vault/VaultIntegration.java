package org.clockworx.battlearena.module.vault;

import org.clockworx.battlearena.event.BattleArenaPostInitializeEvent;
import org.clockworx.battlearena.event.action.EventActionType;
import org.clockworx.battlearena.module.ArenaModule;
import org.clockworx.battlearena.module.ArenaModuleInitializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;

/**
 * A module that allows for hooking into ServiceIO or Vault plugin.
 * ServiceIO is a modern drop-in replacement for Vault that implements Vault interfaces.
 */
@ArenaModule(id = VaultIntegration.ID, name = "Vault", description = "Adds support for hooking into ServiceIO or Vault plugin.", authors = "BattlePlugins")
public class VaultIntegration implements ArenaModuleInitializer {
    public static final String ID = "vault";

    public static final EventActionType<AddPermissionAction> ADD_PERMISSION_ACTION = EventActionType.create("add-permission", AddPermissionAction.class, AddPermissionAction::new);
    public static final EventActionType<EditCurrencyAction> EDIT_CURRENCY_ACTION = EventActionType.create("edit-currency", EditCurrencyAction.class, EditCurrencyAction::new);
    public static final EventActionType<RemovePermissionAction> REMOVE_PERMISSION_ACTION = EventActionType.create("remove-permission", RemovePermissionAction.class, RemovePermissionAction::new);

    private VaultContainer vaultContainer;

    @EventHandler
    public void onPostInitialize(BattleArenaPostInitializeEvent event) {
        // Check that we have ServiceIO or Vault installed.
        // ServiceIO is preferred when both are present, since it's a modern drop-in replacement.
        Plugin providerPlugin = resolveVaultProviderPlugin(Bukkit.getServer().getPluginManager());
        if (providerPlugin == null) {
            event.getBattleArena().module(VaultIntegration.ID).ifPresent(container ->
                    container.disable("ServiceIO or Vault (with Vault API) is required for the Vault integration module to work!")
            );

            return;
        }

        this.vaultContainer = new VaultContainer(providerPlugin);
        if (!this.vaultContainer.hasEconomySupport()) {
            event.getBattleArena().warn("ServiceIO/Vault detected, but no Economy provider is registered. Currency actions will be ignored until an economy plugin is installed.");
        }
    }

    public VaultContainer getVaultContainer() {
        return this.vaultContainer;
    }

    @Nullable
    private static Plugin resolveVaultProviderPlugin(PluginManager pluginManager) {
        Plugin serviceIO = getEnabledPlugin(pluginManager, "ServiceIO");
        if (serviceIO != null && VaultContainer.isVaultApiAvailable(serviceIO)) {
            return serviceIO;
        }

        Plugin vault = getEnabledPlugin(pluginManager, "Vault");
        if (vault != null && VaultContainer.isVaultApiAvailable(vault)) {
            return vault;
        }

        return null;
    }

    @Nullable
    private static Plugin getEnabledPlugin(PluginManager pluginManager, String name) {
        Plugin plugin = pluginManager.getPlugin(name);
        if (plugin == null || !plugin.isEnabled()) {
            return null;
        }

        return plugin;
    }
}
