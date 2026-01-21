PokeLike - A Pokémon-Inspired Roguelike

A procedurally generated dungeon crawler built from scratch in Java using the [LibGDX](https://libgdx.com/) framework. This project combines classic monster-catching mechanics with infinite roguelike exploration.
🚀 Project Overview

This game was developed as a technical exploration into game architecture, specifically focusing on Model-View-Controller (MVC) patterns and procedural generation. It features an infinite overworld, turn-based combat, and persistent party management.

Current State: Playable Prototype (v0.5)
🛠 Tech Stack

- Language: Java 17+
- Framework: LibGDX (LWJGL3 backend)
- Build Tool: Gradle
- Template: Built upon the standard gdx-liftoff / LibGDX Gradle template.

✨ Features Implemented
🌍 Infinite Exploration

- Procedural Generation: The world is generated in "chunks" (screens) as you move.
- Persistence: The game "remembers" every screen you visit. Dropped items stay dropped; defeated enemies stay defeated.
- Seamless Travel: Walk off the edge of the screen to transition to the next area (Zelda/Celeste style).

⚔️ Turn-Based Combat

- Classic Battle System: Encounter wild monsters, attack, or attempt to catch them.
- Party System: Build a team of up to 10 monsters.
- Permadeath (Sort of): If your active monster faints, the next available team member is automatically sent out. If the whole team wipes, it's Game Over.

🎒 Inventory & Menus

- Item System: Pick up Potions (Heal) and Pokéballs (Catch) scattered in the world.
- Interactive Inventory: A grid-based menu to view your squad, check HP, and use items.
- Game State Management: Main Menu, Pause Menu, and seamless state switching.

🏗 Architecture

The project adheres to SOLID principles and the MVC (Model-View-Controller) pattern to ensure scalability.

- Models: WorldManager, BattleManager (Pure data and state).
- Controllers: PlayerController, BattleController, InventoryController (Business logic, input handling, rules).
- Views: DungeonScreen, BattleScreen, InventoryScreen (Rendering logic only).

🎮 Controls
| Context | Key | Action |
| :--- | :--- | :--- |
| **General** | `Arrows` / `WASD` | Move / Navigate Menus |
| | `ESC` | Pause Game / Return |
| **Exploration** | `I` | Open Inventory |
| | `Walk Over` | Pick up Items / Encounter Enemies |
| **Battle** | `SPACE` | Attack |
| | `C` | Catch Enemy (needs Pokéball) |
| | `H` | Heal Active Monster (needs Potion) |
| | `S` | Switch Active Monster |
| **Inventory** | `Arrows` | Move Selection Cursor |
| | `H` | Use Potion on Selected Monster |

📦 How to Run

This project uses the Gradle wrapper, so no manual Gradle installation is required.

Clone the repository:
```Bash

git clone https://github.com/robin-demesmaeker06/pokelike.git
cd pokelike
```
Run the game (Linux/Mac):
```Bash

./gradlew lwjgl3:run
```
Run the game (Windows):
```Bash

gradlew.bat lwjgl3:run
```
📝 Credits

Code & Logic: Developed by Robin Demesmaeker.

Framework: Powered by LibGDX.

Assets: Placeholder assets used for prototyping.

Created January 2026