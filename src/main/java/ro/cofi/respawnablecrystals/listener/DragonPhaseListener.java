package ro.cofi.respawnablecrystals.listener;

import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import ro.cofi.respawnablecrystals.RespawnableCrystals;

public class DragonPhaseListener extends AbstractListener {

    public DragonPhaseListener(RespawnableCrystals plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDragonPhaseChange(EnderDragonChangePhaseEvent event) {
        EnderDragon dragon = event.getEntity();

        // only works in the end
        if (plugin.getConfigManager().isEndDimensionOnlyEnabled() && !World.Environment.THE_END.equals(dragon.getWorld().getEnvironment()))
            return;

        // a dragon fight is already in progress
        if (plugin.getCrystalManager().getDragon() != null)
            return;

        // start the new dragon fight
        plugin.getCrystalManager().setDragon(dragon);
    }

}
