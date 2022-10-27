package ro.cofi.respawnablecrystals.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ro.cofi.respawnablecrystals.RespawnableCrystals;

import java.util.*;
import java.util.function.BiFunction;

public class RSCrystalsCommand implements CommandExecutor {

    private static final String RELOAD_RESPONSE = "The config files have been reloaded.";
    private static final String CLEAR_RESPONSE =
        "The portal locations have been cleared. Start a new dragon fight to reset them";

    private static final Map<String, BiFunction<RSCrystalsCommand, CommandData, String>> options = new HashMap<>();
    private static final Set<BiFunction<RSCrystalsCommand, CommandData, String>> unloggableOptions = new HashSet<>();

    static {
        options.put("reload", RSCrystalsCommand::reload);
        options.put("clear", RSCrystalsCommand::clear);
        options.put("list", RSCrystalsCommand::list);
        options.put("help", RSCrystalsCommand::help);
        options.put("set", RSCrystalsCommand::set);
        options.put("check", RSCrystalsCommand::check);

        unloggableOptions.add(RSCrystalsCommand::list);
        unloggableOptions.add(RSCrystalsCommand::help);
        unloggableOptions.add(RSCrystalsCommand::check);
    }

    private final RespawnableCrystals plugin;

    public RSCrystalsCommand(RespawnableCrystals plugin) {
        this.plugin = plugin;
    }

    public static Set<String> getOptions() {
        return options.keySet();
    }

    @Override
    public boolean onCommand(
        @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args
    ) {
        BiFunction<RSCrystalsCommand, CommandData, String> function;
        CommandData data = new CommandData(sender, command, label, args);

        if (args.length < 1) {
            function = RSCrystalsCommand::help; // needs at least one parameter, show usage
        } else {
            function = options.get(args[0]);
            if (function == null)
                function = RSCrystalsCommand::help; // by default, show usage
        }

        String finalMessage = plugin.prefixMessage(function.apply(this, data));

        // send the message to the issuer
        sender.sendMessage(finalMessage);

        if (!unloggableOptions.contains(function))
            plugin.getLogger().info(finalMessage);

        return true;
    }

    private String reload(CommandData data) {
        plugin.reload();

        return RELOAD_RESPONSE;
    }

    private String clear(CommandData data) {
        plugin.getConfigManager().resetCrystalLocations();
        plugin.getCrystalManager().reloadLocations();

        return CLEAR_RESPONSE;
    }

    private String list(CommandData data) {
        List<String> lines = new ArrayList<>();

        lines.add("Crystal Locations:");

        for (Vector crystal : plugin.getConfigManager().getCrystalLocations())
            lines.add(crystal.toString());

        if (lines.size() == 1)
            lines.add("None");

        return String.join("\n> ", lines);
    }

    private String help(CommandData data) {
        List<String> sortedKeys = new ArrayList<>(options.keySet());
        Collections.sort(sortedKeys);

        return "%sUsage: /%s <%s>".formatted(
            ChatColor.RED.toString(), data.command.getName(), String.join("|", sortedKeys)
        );
    }

    private String set(CommandData data) {
        if (data.args.length < 3)
            return "%sUsage: /%s %s <key> <value>".formatted(
                ChatColor.RED.toString(), data.command.getName(), data.args[0]
            );

        String key = data.args[1];
        String value = data.args[2];

        if (!plugin.getConfig().contains(key))
            return "%sKey \"%s%s%s\" doesn't exist".formatted(
                ChatColor.RED.toString(),
                ChatColor.BOLD.toString(), key, ChatColor.RESET.toString() + ChatColor.RED
            );

        Object existingValue = plugin.getConfig().get(key);

        // set according to type
        if (existingValue instanceof Boolean) {
            boolean booleanValue = false;

            if (value.equalsIgnoreCase("true"))
                booleanValue = true;
            else if (!value.equalsIgnoreCase("false"))
                return "%sExpected a boolean value (true/false), received \"%s%s%s\"".formatted(
                    ChatColor.RED.toString(),
                    ChatColor.BOLD.toString(), value, ChatColor.RESET.toString() + ChatColor.RED
                );

            plugin.getConfig().set(key, booleanValue);
        } else if (existingValue instanceof Double) {
            double doubleValue;

            try {
                doubleValue = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return "%sExpected a double value (e.g.: 0.1), received \"%s%s%s\"".formatted(
                    ChatColor.RED.toString(),
                    ChatColor.BOLD.toString(), value, ChatColor.RESET.toString() + ChatColor.RED
                );
            }

            plugin.getConfig().set(key, doubleValue);
        } else if (existingValue instanceof Integer) {
            int intValue;

            try {
                intValue = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return "%sExpected an integer value (e.g.: 1), received \"%s%s%s\"".formatted(
                    ChatColor.RED.toString(),
                    ChatColor.BOLD.toString(), value, ChatColor.RESET.toString() + ChatColor.RED
                );
            }

            plugin.getConfig().set(key, intValue);
        } else if (existingValue instanceof String) {
            plugin.getConfig().set(key, value);
        } else {
            return "%sCannot set value for key \"%s%s%s\"".formatted(
                ChatColor.RED.toString(),
                ChatColor.BOLD.toString(), key, ChatColor.RESET.toString() + ChatColor.RED
            );
        }

        plugin.saveConfig();
        plugin.reload();

        return "Successfully changed value for \"%s%s%s\" from \"%s%s%s\" to \"%s%s%s\"".formatted(
            ChatColor.GOLD.toString(), key, ChatColor.RESET.toString(),
            ChatColor.GOLD.toString(), existingValue, ChatColor.RESET.toString(),
            ChatColor.GOLD.toString(), value, ChatColor.RESET.toString()
        );
    }

    private String check(CommandData data) {
        if (data.args.length < 2)
            return "%sUsage: /%s %s <key>".formatted(
                ChatColor.RED.toString(), data.command.getName(), data.args[0]
            );

        String key = data.args[1];

        if (!plugin.getConfig().contains(key))
            return "%sKey \"%s%s%s\" doesn't exist".formatted(
                ChatColor.RED.toString(),
                ChatColor.BOLD.toString(), key, ChatColor.RESET.toString() + ChatColor.RED
            );

        Object value = plugin.getConfig().get(key);

        if (value == null || value instanceof MemorySection)
            return "%sKey \"%s%s%s\" does not have an associated value".formatted(
                ChatColor.RED.toString(),
                ChatColor.BOLD.toString(), key, ChatColor.RESET.toString() + ChatColor.RED
            );

        return "The value for key \"%s%s%s\" is \"%s%s%s\"".formatted(
            ChatColor.GOLD.toString(), key, ChatColor.RESET.toString(),
            ChatColor.GOLD.toString(), value, ChatColor.RESET.toString()
        );
    }

    @SuppressWarnings("squid:S6218") // won't check for equality
    private record CommandData(CommandSender sender, Command command, String label, String[] args) { }

}
