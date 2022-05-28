package ro.cofi.respawnablecrystals.listener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.util.Vector;
import ro.cofi.respawnablecrystals.RespawnableCrystals;

public class CrystalSpawnListener extends AbstractListener {

    private static final Vector DRAGON_SPAWNPOINT = new Vector(0, 128, 0);

    public CrystalSpawnListener(RespawnableCrystals plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCrystalSpawn(EntitySpawnEvent event) {
        // only if the plugin is enabled, obviously
        if (!plugin.getConfigManager().isEnabled())
            return;

        // if the crystals have already been saved, there's no point in doing any more checks
        if (plugin.getCrystalManager().hasStoredLocations())
            return;

        Entity entity = event.getEntity();
        World world = entity.getWorld();

        // only care about crystals
        if (!(entity instanceof EnderCrystal crystal))
            return;

        // only works in the end
        if (plugin.getConfigManager().isEndDimensionOnlyEnabled() && !World.Environment.THE_END.equals(world.getEnvironment()))
            return;

        // if this crystal points towards the dragon spawnpoint at the very moment it is spawned, it's a pillar crystal
        Location beamTarget = crystal.getBeamTarget();
        if (beamTarget == null || !beamTarget.toVector().equals(DRAGON_SPAWNPOINT))
            return;

        // it is indeed a pillar crystal, save it
        plugin.getCrystalManager().saveCrystal(crystal.getLocation().toVector());
    }

}
