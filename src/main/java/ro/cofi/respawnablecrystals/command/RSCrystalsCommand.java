package ro.cofi.respawnablecrystals.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ro.cofi.respawnablecrystals.RespawnableCrystals;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;

public class RSCrystalsCommand implements CommandExecutor {

    private static final String RELOAD_RESPONSE = "The config files have been reloaded.";
    private static final String CLEAR_RESPONSE = "The portal locations have been cleared. Start a new dragon fight to reset them";

    private static final Map<String, BiPredicate<RSCrystalsCommand, CommandData>> options = new HashMap<>();

    static {
        options.put("reload", RSCrystalsCommand::reload);
        options.put("clear", RSCrystalsCommand::clear);
        options.put("list", RSCrystalsCommand::list);
        options.put("help", RSCrystalsCommand::help);
        options.put("set", RSCrystalsCommand::set);
        options.put("check", RSCrystalsCommand::check);
    }

    private final RespawnableCrystals plugin;

    public RSCrystalsCommand(RespawnableCrystals plugin) {
        this.plugin = plugin;
    }

    public static Set<String> getOptions() {
        return options.keySet();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        BiPredicate<RSCrystalsCommand, CommandData> function;
        AtomicReference<String> message = new AtomicReference<>();

        CommandData data = new CommandData(sender, command, label, args, message);

        if (args.length < 1) {
            function = RSCrystalsCommand::help; // needs at least one parameter, show usage
        } else {
            function = options.get(args[0]);
            if (function == null)
                function = RSCrystalsCommand::help; // by default, show usage
        }

        boolean log = function.test(this, data);
        String finalMessage = plugin.prefixMessage(message.get());

        // send the message to the issuer
        sender.sendMessage(finalMessage);

        if (log)
            plugin.getLogger().info(finalMessage);

        return true;
    }

    private boolean reload(CommandData data) {
        plugin.reload();

        data.message.set(RELOAD_RESPONSE);

        return true;
    }

    private boolean clear(CommandData data) {
        plugin.getConfigManager().resetCrystalLocations();
        plugin.getCrystalManager().reloadLocations();

        data.message.set(CLEAR_RESPONSE);

        return true;
    }

    private boolean list(CommandData data) {
        List<String> lines = new ArrayList<>();

        lines.add("Crystal Locations:");

        for (Vector crystal : plugin.getConfigManager().getCrystalLocations())
            lines.add(crystal.toString());

        if (lines.size() == 1)
            lines.add("None");

        data.message.set(StringUtils.join(lines, System.lineSeparator() + "> "));

        return false;
    }

    private boolean help(CommandData data) {
        List<String> sortedKeys = new ArrayList<>(options.keySet());
        Collections.sort(sortedKeys);
        data.message.set(ChatColor.RED + "Usage: /" + data.command.getName() +
                " <" + StringUtils.join(sortedKeys, "|") + ">");

        return false;
    }

    private boolean set(CommandData data) {
        if (data.args.length < 3) {
            data.message.set(ChatColor.RED + "Usage: /" + data.command.getName() + " " + data.args[0] + " <key> <value>");
            return true;
        }

        String key = data.args[1];
        String value = data.args[2];

        if (!plugin.getConfig().contains(key)) {
            data.message.set(ChatColor.RED + "Key \"" +
                    ChatColor.BOLD + key + ChatColor.RESET + ChatColor.RED + "\" doesn't exist");
            return true;
        }

        Object existingValue = plugin.getConfig().get(key);

        // set according to type
        if (existingValue instanceof Boolean) {
            boolean booleanValue = false;

            if (value.equalsIgnoreCase("true")) {
                booleanValue = true;
            } else if (!value.equalsIgnoreCase("false")) {
                data.message.set(ChatColor.RED + "Expected a boolean value (true/false), received \"" +
                        ChatColor.BOLD + value + ChatColor.RESET + ChatColor.RED + "\"");
                return true;
            }

            plugin.getConfig().set(key, booleanValue);
        } else if (existingValue instanceof Double) {
            double doubleValue;

            try {
                doubleValue = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                data.message.set(ChatColor.RED + "Expected a double value (e.g.: 0.1), received \"" +
                        ChatColor.BOLD + value + ChatColor.RESET + ChatColor.RED + "\"");
                return true;
            }

            plugin.getConfig().set(key, doubleValue);
        } else if (existingValue instanceof Integer) {
            int intValue;

            try {
                intValue = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                data.message.set(ChatColor.RED + "Expected an integer value (e.g.: 1), received \"" +
                        ChatColor.BOLD + value + ChatColor.RESET + ChatColor.RED + "\"");
                return true;
            }

            plugin.getConfig().set(key, intValue);
        } else if (existingValue instanceof String) {
            plugin.getConfig().set(key, value);
        } else {
            data.message.set(ChatColor.RED + "Cannot set value for key \"" +
                    ChatColor.BOLD + key + ChatColor.RESET + ChatColor.RED + "\"");
            return true;
        }

        data.message.set("Successfully changed value for \"" + ChatColor.GOLD + key + ChatColor.RESET + "\" from \"" +
                ChatColor.GOLD + existingValue + ChatColor.RESET + "\" to \"" +
                ChatColor.GOLD + value + ChatColor.RESET + "\"");

        plugin.saveConfig();
        plugin.reload();

        return true;
    }

    private boolean check(CommandData data) {
        if (data.args.length < 2) {
            data.message.set(ChatColor.RED + "Usage: /" + data.command.getName() + " " + data.args[0] + " <key>");
            return false;
        }

        String key = data.args[1];

        if (!plugin.getConfig().contains(key)) {
            data.message.set(ChatColor.RED + "Key \"" +
                    ChatColor.BOLD + key + ChatColor.RESET + ChatColor.RED + "\" doesn't exist");
            return false;
        }

        Object value = plugin.getConfig().get(key);

        if (value == null || value instanceof MemorySection) {
            data.message.set(ChatColor.RED + "Key \"" +
                    ChatColor.BOLD + key + ChatColor.RESET + ChatColor.RED + "\" does not have an associated value");
            return false;
        }

        data.message.set("The value for key \"" + ChatColor.GOLD + key + ChatColor.RESET + "\" is \"" +
                ChatColor.GOLD + value + ChatColor.RESET + "\"");

        return false;
    }

    private record CommandData(CommandSender sender,
                              Command command,
                              String label,
                              String[] args,
                              AtomicReference<String> message) {

    }

}
