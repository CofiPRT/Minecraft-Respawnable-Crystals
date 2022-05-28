package ro.cofi.respawnablecrystals.listener;

import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import ro.cofi.respawnablecrystals.RespawnableCrystals;

public class DragonDeathListener extends AbstractListener {

    public DragonDeathListener(RespawnableCrystals plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDragonDeath(EntityDeathEvent event) {
        // only care about dragons
        if (!(event.getEntity() instanceof EnderDragon dragon))
            return;

        // only works in the end
        if (plugin.getConfigManager().isEndDimensionOnlyEnabled() && !World.Environment.THE_END.equals(dragon.getWorld().getEnvironment()))
            return;

        EnderDragon existingDragon = plugin.getCrystalManager().getDragon();
        if (!dragon.equals(existingDragon))
            return;

        // stop the existing dragon fight
        plugin.getCrystalManager().setDragon(null);
    }

}
