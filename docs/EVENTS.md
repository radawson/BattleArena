# BattleArena Event System Reference

## Overview

BattleArena uses an event-driven architecture that allows you to configure actions that trigger when specific events occur during competitions. Events can be defined at both the **arena level** (applying to all competitions) and the **phase level** (applying only during specific phases).

## Event Configuration

Events are configured in arena YAML files using the `events` section. Actions are executed in order when an event is triggered.

### Configuration Syntax

```yaml
events:
  on-join:
    - action-name{param1=value1;param2=value2}
    - another-action{param=value}
  
phases:
  waiting:
    events:
      on-start:
        - action-name{param=value}
```

### Event Scope

- **Arena-level events**: Defined in the root `events:` section. Apply to all competitions for that arena.
- **Phase-level events**: Defined in `phases:<phase-name>:events:`. Only apply during that specific phase.

Phase-level events take precedence over arena-level events when both are defined for the same event type.

## Available Event Types

### Player Events

#### `on-join`
Triggered when a player joins a competition.

**Event Class**: `ArenaJoinEvent`  
**Available Resolvers**: `player`, `arena`, `competition`, `map`

**Example**:
```yaml
on-join:
  - store{types=all}
  - change-gamemode{gamemode=adventure}
  - teleport{location=waitroom}
```

#### `on-leave`
Triggered when a player leaves a competition.

**Event Class**: `ArenaLeaveEvent`  
**Available Resolvers**: `player`, `arena`, `competition`, `map`  
**Event Data**: `cause` (COMMAND, DISCONNECT, GAME, SHUTDOWN, PLUGIN, KICKED, REMOVED)

**Example**:
```yaml
on-leave:
  - clear-effects
  - restore{types=all}
```

#### `on-spectate`
Triggered when a player enters spectator mode.

**Event Class**: `ArenaSpectateEvent`  
**Available Resolvers**: `player`, `arena`, `competition`, `map`

**Example**:
```yaml
on-spectate:
  - store{types=all}
  - change-gamemode{gamemode=spectator}
  - flight{enabled=true}
  - teleport{location=spectator}
```

#### `on-death`
Triggered when a player dies in a competition.

**Event Class**: `ArenaDeathEvent`  
**Available Resolvers**: `player`, `arena`, `competition`, `map`

**Example**:
```yaml
on-death:
  - clear-inventory
  - respawn
  - delay{ticks=20}
  - teleport{location=waitroom}
```

#### `on-kill`
Triggered when a player kills another player.

**Event Class**: `ArenaKillEvent`  
**Available Resolvers**: `player` (killer), `killed`, `killer`, `arena`, `competition`, `map`

**Example**:
```yaml
on-kill:
  - send-message{message=<green>You killed {killed}!</green>}
  - play-sound{sound=entity.player.levelup;volume=1;pitch=1}
```

#### `on-respawn`
Triggered when a player respawns after death.

**Event Class**: `ArenaRespawnEvent`  
**Available Resolvers**: `player`, `arena`, `competition`, `map`

**Example**:
```yaml
on-respawn:
  - give-item{item=diamond_sword}
  - teleport{location=team_spawn}
```

#### `on-life-deplete`
Triggered when a player loses a life (but still has lives remaining).

**Event Class**: `ArenaLifeDepleteEvent`  
**Available Resolvers**: `player`, `lives-left`, `arena`, `competition`, `map`

**Example**:
```yaml
on-life-deplete:
  - send-message{message=<yellow>You have {lives-left} lives remaining!</yellow>}
```

#### `on-lives-exhaust`
Triggered when a player runs out of all lives.

**Event Class**: `ArenaLivesExhaustEvent`  
**Available Resolvers**: `player`, `arena`, `competition`, `map`

**Example**:
```yaml
on-lives-exhaust:
  - send-message{message=<red>You're out of lives!</red>}
  - change-role{role=spectating}
```

#### `on-stat-change`
Triggered when a player's or team's stat changes.

**Event Class**: `ArenaStatChangeEvent`  
**Available Resolvers**: `player`, `stat`, `stat-holder`, `old-stat-value`, `new-stat-value`, `arena`, `competition`, `map`

**Example**:
```yaml
on-stat-change:
  - send-message{message=<gray>Your {stat} changed from {old-stat-value} to {new-stat-value}</gray>}
```

### Arena/Competition Events

#### `on-start`
Triggered when a competition phase starts.

**Event Class**: `ArenaPhaseStartEvent`  
**Available Resolvers**: `arena`, `competition`, `map`, `phase`

**Example**:
```yaml
phases:
  ingame:
    events:
      on-start:
        - broadcast{message=<green>Game starting!</green>;audience=game;type=title}
        - teleport{location=team_spawn}
```

#### `on-complete`
Triggered when a competition phase completes.

**Event Class**: `ArenaPhaseCompleteEvent`  
**Available Resolvers**: `arena`, `competition`, `map`, `phase`

**Example**:
```yaml
phases:
  victory:
    events:
      on-complete:
        - leave
        - restore-arena
```

#### `on-victory`
Triggered when players win a competition.

**Event Class**: `ArenaVictoryEvent`  
**Available Resolvers**: `arena`, `competition`, `map`, `players` (victors)

**Example**:
```yaml
on-victory:
  - send-message{message=<green>Congratulations! You won!</green>}
  - play-sound{sound=entity.player.levelup;volume=1;pitch=1}
  - run-command{command=give {player} diamond 64;source=console}
```

#### `on-lose`
Triggered when players lose a competition.

**Event Class**: `ArenaLoseEvent`  
**Available Resolvers**: `arena`, `competition`, `map`, `players` (losers)

**Example**:
```yaml
on-lose:
  - send-message{message=<red>You lost! Better luck next time.</red>}
  - play-sound{sound=block.anvil.place;volume=1;pitch=0.5}
```

#### `on-draw`
Triggered when a competition ends in a draw.

**Event Class**: `ArenaDrawEvent`  
**Available Resolvers**: `arena`, `competition`, `map`

**Example**:
```yaml
on-draw:
  - broadcast{message=<yellow>It's a draw!</yellow>;audience=game}
  - play-sound{sound=block.beacon.deactivate;volume=1;pitch=1}
```

## Available Actions

### Player Actions

#### `store`
Stores player data (inventory, health, etc.) for later restoration.

**Parameters**:
- `types` (required): Comma-separated list of types to store. Options: `INVENTORY`, `HEALTH`, `EXPERIENCE`, `GAMEMODE`, `ATTRIBUTES`, `FLIGHT`, `EFFECTS`, `LOCATION`, or `all`
- `clear-state` (optional): Whether to clear the player's current state after storing. Default: `true`

**Example**:
```yaml
- store{types=all}
- store{types=INVENTORY,HEALTH,EXPERIENCE;clear-state=false}
```

#### `restore`
Restores previously stored player data.

**Parameters**:
- `types` (required): Comma-separated list of types to restore. Options: `INVENTORY`, `HEALTH`, `EXPERIENCE`, `GAMEMODE`, `ATTRIBUTES`, `FLIGHT`, `EFFECTS`, `LOCATION`, or `all`

**Example**:
```yaml
- restore{types=all}
- restore{types=INVENTORY,HEALTH}
```

#### `teleport`
Teleports a player to a specified location.

**Parameters**:
- `location` (required): Location to teleport to. Options: `waitroom`, `spectator`, `team_spawn`, `last_location`
- `random` (optional): Whether to randomly select from team spawns. Default: `false`

**Example**:
```yaml
- teleport{location=waitroom}
- teleport{location=team_spawn;random=true}
```

#### `change-gamemode`
Changes a player's game mode.

**Parameters**:
- `gamemode` (required): Game mode to set. Options: `survival`, `creative`, `adventure`, `spectator`

**Example**:
```yaml
- change-gamemode{gamemode=adventure}
```

#### `change-role`
Changes a player's role in the competition.

**Parameters**:
- `role` (required): Role to set. Options: `playing`, `spectating`

**Example**:
```yaml
- change-role{role=spectating}
```

#### `flight`
Enables or disables flight for a player.

**Parameters**:
- `enabled` (required): Whether to enable flight. Options: `true`, `false`

**Example**:
```yaml
- flight{enabled=true}
```

#### `clear-inventory`
Clears a player's inventory.

**Parameters**: None

**Example**:
```yaml
- clear-inventory
```

#### `clear-effects`
Removes all potion effects from a player.

**Parameters**: None

**Example**:
```yaml
- clear-effects
```

#### `give-item`
Gives an item to a player.

**Parameters**:
- `item` (required): Item specification (e.g., `diamond_sword`, `golden_apple{count=5}`, `iron_sword{enchantments=[sharpness{level=5}]}`)
- `slot` (optional): Inventory slot to place item in. Default: `-1` (first available slot)

**Example**:
```yaml
- give-item{item=diamond_sword}
- give-item{item=golden_apple{count=3};slot=0}
```

#### `give-effects`
Applies potion effects to a player.

**Parameters**:
- `effects` (required): List of effects in square brackets. Format: `[effect1{param=value};effect2{param=value}]`

**Example**:
```yaml
- give-effects{effects=[speed{duration=300;amplifier=1};strength{duration=200;amplifier=2}]}
```

#### `health`
Sets a player's health and hunger levels.

**Parameters**:
- `health` (required): Health value (0-20)
- `hunger` (required): Hunger value (0-20)

**Example**:
```yaml
- health{health=20;hunger=20}
```

#### `respawn`
Respawns a player.

**Parameters**: None

**Example**:
```yaml
- respawn
```

#### `reset-state`
Resets a player's state (clears effects, resets health, etc.).

**Parameters**: None

**Example**:
```yaml
- reset-state
```

#### `join-random-team`
Automatically assigns a player to a random team.

**Parameters**: None

**Example**:
```yaml
- join-random-team
```

### Communication Actions

#### `send-message`
Sends a message to a player.

**Parameters**:
- `message` (required): Message text (supports MiniMessage format and resolver placeholders)
- `type` (optional): Message type. Options: `chat`, `action_bar`, `title`, `subtitle`. Default: `chat`

**Example**:
```yaml
- send-message{message=<green>Welcome to the arena!</green>}
- send-message{message=<red>You have {lives-left} lives left!</red>;type=action_bar}
```

#### `broadcast`
Broadcasts a message to multiple players.

**Parameters**:
- `message` (required): Message text (supports MiniMessage format and resolver placeholders)
- `audience` (optional): Who receives the message. Options: `game` (all players in competition), `server` (all online players). Default: `game`
- `type` (optional): Message type. Options: `chat`, `action_bar`, `title`, `subtitle`. Default: `chat`

**Example**:
```yaml
- broadcast{message=<green>Game starting!</green>;audience=game;type=title}
- broadcast{message=<yellow>Server-wide announcement!</yellow>;audience=server}
```

#### `play-sound`
Plays a sound to a player.

**Parameters**:
- `sound` (required): Sound name (e.g., `entity.player.levelup`, `block.note_block.pling`)
- `volume` (optional): Sound volume (0.0-1.0). Default: `1.0`
- `pitch` (optional): Sound pitch (0.0-2.0). Default: `1.0`

**Example**:
```yaml
- play-sound{sound=entity.player.levelup;volume=1;pitch=1}
- play-sound{sound=block.note_block.pling;pitch=2;volume=0.5}
```

### Competition Actions

#### `leave`
Removes a player from the competition.

**Parameters**: None

**Example**:
```yaml
- leave
```

#### `delay`
Adds a delay before executing the next action.

**Parameters**:
- `ticks` (required): Number of ticks to delay (20 ticks = 1 second)

**Example**:
```yaml
- delay{ticks=20}  # 1 second delay
- delay{ticks=100}  # 5 second delay
```

#### `run-command`
Executes a command.

**Parameters**:
- `command` (required): Command to execute (supports resolver placeholders)
- `source` (optional): Who executes the command. Options: `player`, `console`. Default: `player`

**Example**:
```yaml
- run-command{command=give {player} diamond 64;source=console}
- run-command{command=spawn}
```

#### `kill-entities`
Removes all entities within the competition bounds.

**Parameters**:
- `excluded-groups` (optional): Comma-separated list of entity spawn categories to exclude (e.g., `monster`, `creature`)

**Example**:
```yaml
- kill-entities
- kill-entities{excluded-groups=[monster,creature]}
```

#### `teardown`
Removes the competition from the arena.

**Parameters**: None

**Example**:
```yaml
- teardown
```

## Resolver Placeholders

Resolver placeholders allow you to insert dynamic values into messages and commands. Use `{placeholder-name}` syntax.

### Available Resolvers

- `{player}` - Player's name
- `{killer}` - Killer's name (in `on-kill` events)
- `{killed}` - Killed player's name (in `on-kill` events)
- `{arena}` - Arena name
- `{competition}` - Competition/map name
- `{map}` - Map name
- `{team}` - Team name
- `{lives-left}` - Remaining lives (in `on-life-deplete` events)
- `{stat}` - Stat name (in `on-stat-change` events)
- `{old-stat-value}` - Previous stat value (in `on-stat-change` events)
- `{new-stat-value}` - New stat value (in `on-stat-change` events)
- `{phase}` - Current phase name
- `{alive-players}` - Number of alive players
- `{online-players}` - Number of online players
- `{spectators}` - Number of spectators
- `{max-players}` - Maximum players
- `{time-remaining}` - Time remaining in phase
- `{time-remaining-short}` - Short format time remaining
- `{remaining-start-time}` - Time until phase starts

### Example Usage

```yaml
on-kill:
  - send-message{message=<green>You killed {killed}!</green>}
  - broadcast{message=<yellow>{killer} eliminated {killed}!</yellow>;audience=game}

on-life-deplete:
  - send-message{message=<yellow>You have {lives-left} lives remaining!</yellow>}

on-victory:
  - send-message{message=<green>Congratulations {player}! You won in {arena}!</green>}
```

## Action Execution Order

Actions are executed sequentially in the order they appear in the configuration. The `delay` action pauses execution for the specified number of ticks before continuing with the next action.

**Example**:
```yaml
on-death:
  - clear-inventory      # Executes immediately
  - respawn              # Executes immediately after
  - delay{ticks=20}      # Waits 1 second
  - teleport{location=waitroom}  # Executes after delay
```

## Best Practices

1. **Always store player data on join**: Use `store{types=all}` in `on-join` to save player state
2. **Restore on leave**: Use `restore{types=all}` in `on-leave` to restore player state
3. **Use delays for respawns**: Add a small delay before teleporting after respawn to avoid issues
4. **Test phase-specific events**: Phase events override arena events, so test carefully
5. **Use resolver placeholders**: Make messages dynamic and informative
6. **Order matters**: Place critical actions (like `store`) before other actions that might modify player state

## Troubleshooting

### Actions not executing
- Check that the event name is correct (case-sensitive)
- Verify the action syntax is correct (no typos in parameter names)
- Check server logs for parsing errors

### Resolver placeholders not working
- Ensure the resolver is available for that event type
- Check that placeholder names match exactly (case-sensitive)
- Some resolvers are only available in specific events (e.g., `{killer}` only in `on-kill`)

### Phase events not working
- Verify the phase name matches exactly
- Check that the competition is actually in that phase
- Remember phase events override arena events

### Teleport issues
- Ensure team spawns are defined if using `team_spawn` location
- Check that waitroom/spectator spawns are set in map configuration
- Use `join-random-team` before teleporting to team spawns
