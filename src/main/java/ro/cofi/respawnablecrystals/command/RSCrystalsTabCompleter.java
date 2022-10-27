package ro.cofi.respawnablecrystals.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.cofi.respawnablecrystals.RespawnableCrystals;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class RSCrystalsTabCompleter implements TabCompleter {

    private final RespawnableCrystals plugin;

    public RSCrystalsTabCompleter(RespawnableCrystals plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(
        @NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args
    ) {
        // first argument, the option
        if (args.length == 1)
            return RSCrystalsCommand.getOptions().stream()
                .filter(option -> option.startsWith(args[0].toLowerCase()))
                .filter(key -> !key.equals(args[0].toLowerCase()))
                .toList();

        FileConfiguration config = plugin.getConfig();

        if (args.length == 2 && (args[0].equals("set") || args[0].equals("check")))
            return config.getKeys(true).stream()
                .filter(key -> !(config.get(key) instanceof MemorySection))
                .filter(key -> key.toLowerCase().startsWith(args[1].toLowerCase()))
                .filter(key -> !key.equals(args[1].toLowerCase()))
                .flatMap(key -> {
                    int dotIndex = key.indexOf(".", args[1].length());
                    String newKey = dotIndex == -1 ? key : key.substring(0, dotIndex + 1);
                    return newKey.equals("") ? Stream.empty() : Stream.of(newKey);
                })
                .toList();

        // don't suggest anything else
        return Collections.emptyList();
    }
}
