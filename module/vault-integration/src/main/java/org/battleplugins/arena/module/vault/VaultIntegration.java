package org.battleplugins.arena.module.vault;

import org.battleplugins.arena.event.BattleArenaPostInitializeEvent;
import org.battleplugins.arena.event.action.EventActionType;
import org.battleplugins.arena.module.ArenaModule;
import org.battleplugins.arena.module.ArenaModuleInitializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

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
        // Check that we have ServiceIO or Vault installed
        // ServiceIO is a modern drop-in replacement for Vault that implements Vault interfaces
        boolean hasServiceIO = Bukkit.getServer().getPluginManager().isPluginEnabled("ServiceIO");
        boolean hasVault = Bukkit.getServer().getPluginManager().isPluginEnabled("Vault");
        
        if (!hasServiceIO && !hasVault) {
            event.getBattleArena().module(VaultIntegration.ID).ifPresent(container ->
                    container.disable("ServiceIO or Vault is required for the Vault integration module to work!")
            );

            return;
        }

        this.vaultContainer = new VaultContainer();
    }

    public VaultContainer getVaultContainer() {
        return this.vaultContainer;
    }
}
