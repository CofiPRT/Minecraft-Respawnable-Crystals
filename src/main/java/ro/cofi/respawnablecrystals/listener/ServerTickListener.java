package ro.cofi.respawnablecrystals.listener;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.event.EventHandler;
import ro.cofi.respawnablecrystals.RespawnableCrystals;

public class ServerTickListener extends AbstractListener {

    public ServerTickListener(RespawnableCrystals plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onServerTick(ServerTickEndEvent event) {
        // only if the plugin is enabled, obviously
        if (!(plugin.getConfigManager().isEnabled()))
            return;

        plugin.getCrystalManager().offerTick();
    }

}
