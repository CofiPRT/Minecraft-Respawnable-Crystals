# Respawnable Crystals

A PaperMC plugin to occasionally regenerate Ender Crystals during the Ender Dragon fight.

## How to set up

After placing the plugin in your **plugins** folder, a new Ender Dragon fight is needed in order to detect
the locations of the End Pillars. **If an Ender Dragon fight is already in progress, you need to finish it
and start a new one.**

## Behavior

During an Ender Dragon fight, End Crystals will periodically respawn, randomly.

The iron bar cage and the obsidian pillars will **NOT** regenerate when a respawn triggered by this plugin
takes place. They **will** regenerate when a new Ender Dragon fight is started, according to the vanilla
behavior.

End Crystals placed by players are **NOT** affected.

## Config

Several aspects of the plugin are configurable, either by modifying the **config.yml**
(located in the **/plugins/RespawnableCrystals** folder of your server), or through the use of
[commands](#Commands).

| Option               | Valid values        | Description                                                                                                                                                                                                                                                                                                                                                                                                                                               |
|----------------------|---------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `enabled`            | `true`/`false`      | An ON/OFF switch for this plugin. <br/><br/> _Defaults to `true`._                                                                                                                                                                                                                                                                                                                                                                                        |
| `timer.min`          | any positive number | The **minimum** amount of time between respawn attempts, in seconds. <br/><br/> _Defaults to `30`._                                                                                                                                                                                                                                                                                                                                                       |
| `timer.max`          | any positive number | The **maximum** amount of time between respawn attempts, in seconds. <br/><br/> _Defaults to `60`._                                                                                                                                                                                                                                                                                                                                                       |
| `respawn-effects.*`  | `true`/`false`      | When a crystal respawns, several effects are applied in order to attract the player's attention. <br/><br/> _All effects default to `true`._                                                                                                                                                                                                                                                                                                              |
| `force-respawn`      | `true`/`false`      | When `false`, a respawn attempt may happen to target an already existing crystal, thus not respawning any new crystal. <br/><br/> When 9 crystals are alive, it may take a reasonable amount of time for the 10th one to respawn (on average, up to **10** times the value of `timer.max`). Good for variety. <br/><br/> When `true`, a crystal is guaranteed to respawn when an attempt is made. Good for consistency. <br/><br/> _Defaults to `false`._ |
| `end-dimension-only` | `true`/`false`      | Sometimes, some custom End worlds (e.g.: from **datapacks**) fail to correctly describe that they are, in fact, an End world. By setting this value to `false`, you allow the plugin to work in any world where an Ender Dragon fight can take place. <br/><br/> _Defaults to `true`._                                                                                                                                                                    |

## Commands

The plugin adds the custom command `/rscrystals`, used for managing the plugin along with editing the
configuration file in-game, instead of having to manually modify it.
**In order to use this command, a player needs the `rscrystals.command` permission (or `rscrystals.*`).**

When a **configuration key** is needed, you can press `TAB` while writing the command in order to browse 
the available keys.

- `/rscrystals help` - displays the available parameters for the `rscrystals` command.
- `/rscrystals list` - lists the locations of the saved End Crystals, atop the End Pillars. If the list displays `None`,
a new Ender Dragon fight is needed in order to detect the End Pillars.
- `/rscrystals clear` - clears the locations of the End Crystals, requiring a new Ender Dragon fight to be initiated.
- `/rscrystals reload` - reloads the configuration file in case it was edited manually.
- `/rscrystals check <key>` - checks the value of a configuration setting; e.g.: the "key" of the `min` setting under
the `timer` group is `timer.min`, thus the command will be `/rscrystals check timer.min` (by default, this should show `30`).
- `/rscrystals set <key> <value>` - sets the value of a configuration setting. The configuration file is automatically reloaded.

