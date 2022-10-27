package ro.cofi.respawnablecrystals.logic;

import org.bukkit.*;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Firework;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import ro.cofi.respawnablecrystals.RespawnableCrystals;

import java.util.*;

public class CrystalManager {

    private static final Random RAND = new Random();
    private static final int PILLAR_COUNT = 10;

    private final RespawnableCrystals plugin;
    private final Set<Vector> locations = new HashSet<>();

    private EnderDragon dragon;
    private int timer;

    public CrystalManager(RespawnableCrystals plugin) {
        this.plugin = plugin;

        timer = getRandomTimer();
        reloadLocations();
    }

    public boolean hasStoredLocations() {
        return locations.size() == PILLAR_COUNT;
    }

    public void reloadLocations() {
        locations.clear();
        locations.addAll(plugin.getConfigManager().getCrystalLocations());
    }

    public void saveCrystal(Vector location) {
        locations.add(location);
        plugin.getConfigManager().saveCrystalLocation(location);
    }

    public void setDragon(EnderDragon dragon) {
        this.dragon = dragon;
    }

    public EnderDragon getDragon() {
        return dragon;
    }

    public void offerTick() {
        if (dragon == null)
            return; // no dragon fight, no point in ticking

        if (dragon.isDead()) {
            setDragon(null);
            return; // safety check
        }

        if (timer-- > 0)
            return; // continue ticking

        // reset logic
        timer = getRandomTimer();

        attemptRespawn();
    }

    private void attemptRespawn() {
        // shuffle crystals in a list
        List<Vector> shuffledCrystals = new ArrayList<>(locations);
        Collections.shuffle(shuffledCrystals);

        // if the respawn is forced, always respawn a destroyed crystal
        int attempts = plugin.getConfigManager().isForceRespawnEnabled() ? shuffledCrystals.size() : 1;
        World endWorld = dragon.getWorld();

        while (attempts-- != 0) {
            Vector crystalLocation = shuffledCrystals.get(attempts);

            // if a crystal already exists at this location, don't spawn another one
            Location loc =
                new Location(endWorld, crystalLocation.getX(), crystalLocation.getY(), crystalLocation.getZ());
            if (endWorld.getNearbyEntities(loc, 1, 1, 1, EnderCrystal.class::isInstance).isEmpty()) {
                // spawn a crystal
                spawnCrystal(loc);
                return;
            }
        }

    }

    private int getRandomTimer() {
        int min = plugin.getConfigManager().getTimerMin();
        int max = plugin.getConfigManager().getTimerMax();

        // sanity check
        if (min >= max)
            return min * 20;

        return (RAND.nextInt(max - min + 1) + min) * 20; // in ticks
    }

    private void spawnCrystal(Location location) {
        World world = location.getWorld();

        // spawn the crystal
        world.spawn(
            location,
            EnderCrystal.class,
            CreatureSpawnEvent.SpawnReason.CUSTOM,
            crystal -> crystal.setShowingBottom(true)
        );

        // special FX
        if (plugin.getConfigManager().isEffectExplosionParticlesEnabled())
            world.spawnParticle(
                Particle.EXPLOSION_HUGE,
                location,
                10,
                3, 3, 3,
                1,
                null,
                true
            );

        if (plugin.getConfigManager().isEffectExplosionSoundEnabled())
            world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 128.0f, 1.0f);

        if (plugin.getConfigManager().isEffectBeaconSoundEnabled())
            world.playSound(location, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 128.0f, 1.0f);

        if (plugin.getConfigManager().isEffectFireworkLaunchEnabled())
            world.spawn(
                location.add(0, 1, 0),
                Firework.class,
                CreatureSpawnEvent.SpawnReason.CUSTOM,
                firework -> {
                    FireworkMeta fireworkMeta = firework.getFireworkMeta();
                    fireworkMeta.setPower(1);
                    fireworkMeta.addEffects(
                        FireworkEffect.builder()
                            .with(FireworkEffect.Type.BALL_LARGE)
                            .withFlicker()
                            .withTrail()
                            .withColor(Color.SILVER, Color.FUCHSIA)
                            .withFade(Color.WHITE, Color.FUCHSIA)
                            .build()
                    );
                    firework.setFireworkMeta(fireworkMeta);
                }
            );
    }

}
