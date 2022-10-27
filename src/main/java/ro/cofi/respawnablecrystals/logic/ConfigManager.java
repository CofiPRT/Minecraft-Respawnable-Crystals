package ro.cofi.respawnablecrystals.logic;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import ro.cofi.respawnablecrystals.RespawnableCrystals;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class ConfigManager {

    private static final String EFFECTS_KEY = "respawn-effects";
    private static final String LOCATIONS_KEY = "crystal-locations";

    private final RespawnableCrystals plugin;

    public ConfigManager(RespawnableCrystals plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("enabled");
    }

    public int getTimerMin() {
        return plugin.getConfig().getInt("timer.min");
    }

    public int getTimerMax() {
        return plugin.getConfig().getInt("timer.max");
    }

    public boolean isEffectExplosionParticlesEnabled() {
        return plugin.getConfig().getBoolean(EFFECTS_KEY + "." + "explosion-particles");
    }

    public boolean isEffectExplosionSoundEnabled() {
        return plugin.getConfig().getBoolean(EFFECTS_KEY + "." + "explosion-sound");
    }

    public boolean isEffectBeaconSoundEnabled() {
        return plugin.getConfig().getBoolean(EFFECTS_KEY + "." + "beacon-sound");
    }

    public boolean isEffectFireworkLaunchEnabled() {
        return plugin.getConfig().getBoolean(EFFECTS_KEY + "." + "firework-launch");
    }

    public boolean isForceRespawnEnabled() {
        return plugin.getConfig().getBoolean("force-respawn");
    }

    public boolean isEndDimensionOnlyEnabled() {
        return plugin.getConfig().getBoolean("end-dimension-only");
    }

    public List<Vector> getCrystalLocations() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(LOCATIONS_KEY);
        if (section == null)
            return Collections.emptyList();

        return section.getKeys(false)
            .stream()
            .map(this::stringToVec)
            .flatMap(Stream::ofNullable)
            .toList();
    }

    public void saveCrystalLocation(Vector location) {
        plugin.getConfig().set(LOCATIONS_KEY + "." + vecToString(location), true);
        plugin.saveConfig();
    }

    public void resetCrystalLocations() {
        plugin.getConfig().set(LOCATIONS_KEY, null);
        plugin.saveConfig();
    }

    private String vecToString(Vector vec) {
        // decimal points are converted to underscores
        return "%.2f;%.2f;%.2f".formatted(vec.getX(), vec.getY(), vec.getZ()).replace('.', '_');
    }

    private Vector stringToVec(String vec) {
        double[] coords = Stream.of(vec.split(";"))
            .mapToDouble(s -> Double.parseDouble(s.replace('_', '.')))
            .toArray();

        if (coords.length < 3) {
            plugin.getLogger().severe(plugin.prefixMessage(
                "Invalid coordinates '" + vec + "' in config file."
            ));
            return null;
        }

        return new Vector(coords[0], coords[1], coords[2]);
    }

}
