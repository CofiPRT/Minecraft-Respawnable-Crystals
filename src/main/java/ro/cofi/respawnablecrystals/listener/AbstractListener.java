package ro.cofi.respawnablecrystals.listener;

import org.bukkit.event.Listener;
import ro.cofi.respawnablecrystals.RespawnableCrystals;

public abstract class AbstractListener implements Listener {

    protected final RespawnableCrystals plugin;

    protected AbstractListener(RespawnableCrystals plugin) {
        this.plugin = plugin;
    }

}
