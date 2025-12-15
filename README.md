# Assessment Deliverables

## Coursework

* [High-level Class Diagram >>](diagram/)
* [Javadocs Documentation >>](javadocs/)
* [Demonstration Video >>](video/)

## Source Code

* [Source Code >>](tron-master/src/main/java)  
---
**This project uses OpenJDK 24 and JavaFX 24. To run the game locally, install JavaFX SDK 24 and set the JavaFX module path, or use the javafx-maven-plugin.**

We successfully compiled and ran this project in lab's computer by adding VM options to the Run Configuration in IntelliJ:
```
--module-path "C:\Users\[username]\.m2\repository\org\openjfx\javafx-controls\24;C:\Users\[username]\.m2\repository\org\openjfx\javafx-fxml\24;C:\Users\[username]\.m2\repository\org\openjfx\javafx-graphics\24;C:\Users\[username]\.m2\repository\org\openjfx\javafx-base\24" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base
```

---

# 1. Project Overview

Tron Master is a JavaFX light-cycle game with Story, Survival, and Two-Player modes. Players use boosts and jumps to outmaneuver walls and portals. Built with Java 24 and JavaFX 24 (Maven), the codebase uses FXML controllers with MVC pattern.

---

# 2. Development Summary

This sprint refactored the monolithic `tron-master` into a modular, testable codebase.

- UI: FXML controllers (`MainMenuController`, `PlayMenuController`, `SettingsController`) and `ViewUtils` for shared UI.
- Core: Centralised lifecycle and state using `AbstractGameController` and `GameData` (singleton).
- Logic: Mode rules moved to strategies (`StoryLogicStrategy`, `SurvLogicStrategy`, `TwoPlayerLogicStrategy`) and assembled via `GameObjectFactory`.
- Features: Added `SoundManager`, procedural wall gaps (`WallLayoutController`), portals, a 3-second countdown gate, and settings for background colours.
- Quality: Improved collision math, wall/portal rendering (`WallRenderer`), score persistence (`HighScoreManager`), and added JUnit tests.

---

# 3. Additional Game Features

## **Sound Effects**

**Description:**
Background music and short sound effects create an arcade atmosphere across menus and gameplay.

**Implementation Details:**

* **`SoundManager`** — Singleton loader/controller for music and SFX; provides play/stop/mute APIs (uses JavaFX Media API, refer to https://docs.oracle.com/javafx/2/api/javafx/scene/media/package-summary.html).
* **`SoundEffectListener`** — Listens for player events (death, boost, jump) and plays effects.
* UI controllers (`MainMenuController`, `PlayMenuController`, `SettingsController`) start/stop music and trigger click/quit sounds.
* Countdown and reset use simple tick/reset effects (`TwoPlayerGameController` & `AbstractGameController`).
* Portal and teleport events trigger a teleport SFX in relevant strategies.

---

## **Wall Obstacles / Random Walls**

**Description:**
Walls are static obstacles. Story and Two-Player modes use fixed FXML layouts; Survival uses the same layouts with random gaps.

**Implementation Details:**

* **`Wall`** — Simple object storing position, size and color; collisions cause player death.
* **`WallLayoutController`** — Loads FXML layouts and returns wall lists; in Survival mode it cuts random gaps (configurable MIN/MAX size) and provides `isPositionSafe()` for spawning.
* **`WallRenderer`** — Draws walls and optional glow effects on the canvas.
* **FXML files:** `two_player_walls.fxml`, `story_walls.fxml`, `survival_walls.fxml` for maintainable wall customization.

---

## **Portals**

**Description:**
Portals (entrance/exit pairs) provide teleportation in Story mode and Survival mode.

**Implementation Details:**

* **`Portal`** — Extends `Wall`, flagged as entrance or exit; `linkToExit()` connects an entrance to an exit. `teleport()` moves the player to the exit (clears trail, applies offset and cooldown), and `handleCollision()` returns true when a teleport occurs.
* **`WallLayoutController`** — Loads portals from FXML (naming conventions `portal_entrance_X` / `portal_exit_X` link automatically).
* **`StoryLogicStrategy`** — Checks portal collisions for all players and optionally grants a small reward (e.g., +1 boost for human players).

---

## **Countdown Start**

**Description:**
Two-Player mode starts with a 3-2-1 countdown that disables input and synchronizes the start.

**Implementation Details:**

* `TwoPlayerGameController.startCountdown()` uses a JavaFX Timeline to show 3→2→1, plays a tick SFX, disables player input while counting, then hides the display and starts the game (see JavaFX Timeline: https://docs.oracle.com/javase/8/javafx/api/javafx/animation/Timeline.html).
* `PlayerController` gates input via a boolean flag (`isRenderingEnabled`) so keys are ignored while the countdown is active.
* `GameArea` draws/removes the countdown numbers on the canvas.

---

## **Settings Option**

**Description:**
Settings allow picking one of several background colours and persist the choice across modes.

**Implementation Details:**

* `SettingsController` loads `settings.fxml`, displays a color preview grid, and saves the selected hex color to `GameData` when the user confirms.
* `GameArea.drawBackground()` reads `GameData.getBackgroundColor()` and paints the playfield accordingly.
* `MainMenuController` opens the settings scene; music may be paused while previewing instructions.

---

# 4. Applied Design Patterns

## **Singleton Pattern**

### **Justification**

The game requires a single shared source for mode-dependent data. Before applying the pattern, components duplicated or inconsistently managed game state. The Singleton pattern ensures:

* Centralised and consistent global game state
* Reduced coupling between Model and Controller
* Simplified debugging and state tracking
* Lazy and controlled instantiation

### **Implementation Details**

| Component | Details |
|---|---|
| Class | `GameData.java` |
| Methods | `getInstance()` (static), private constructor |
| Responsibilities | Stores game-wide state: survival score, story level/score, boosts, two-player scores, background colour |

---

## **Factory Pattern**

### **Justification**

Player and game object creation was scattered across logic classes, causing tight coupling and duplicated constructor logic. The Factory pattern provides:

* Centralised and consistent object creation
* Reduced dependency on concrete classes
* Simplified extensibility when adding new object types

### **Implementation Details**

| Component | Details |
|---|---|
| Class | `GameObjectFactory.java` |
| Methods | `createHumanPlayer()`, `createAIPlayer()`, `createWall()`, `createLinkedPortalPair()` |
| Responsibilities | Central source of object construction to reduce coupling and enable consistent configuration |

---

## **Strategy Pattern**

### **Justification**

Each game mode originally contained large amounts of duplicated logic, making it difficult to maintain and extend. The Strategy pattern enables:

* Independent implementation of each game mode's rules
* Shared base logic for movement/update cycles
* Open extension for new modes
* Cleaner separation of concerns

### **Implementation Details**

| Component | Details |
|---|---|
| Abstract | `GameLogic.java` |
| Abstract methods | `updateGame()`, `getVelocity()`, `getRandomStart()`, `tick()`, `reset()`, `addScore()` |
| Concrete strategies | `StoryLogicStrategy.java` (AI, portals, level), `SurvLogicStrategy.java` (survival scoring, teleport support), `TwoPlayerLogicStrategy.java` (competitive rules) |
| Purpose | Separate mode rules while sharing movement and update plumbing |

---

## **Observer Pattern**

### **Justification**

Player state changes required explicit calls from model to UI or sound logic, causing tight coupling. The Observer pattern provides:

* Loose coupling between Model and Controller/Sound subsystems
* Easy addition of new event-driven behaviors
* Clear event notification architecture

### **Implementation Details**

| Component | Details |
|---|---|
| Subject | `Player.java` |
| Subject methods | `addStateListener()`, `removeStateListener()`, event notifications (death/boost/jump) |
| Observer interface | `PlayerStateListener.java` — `onPlayerDied()`, `onPlayerBoosted()`, `onPlayerJumped()` |
| Concrete observers | `AbstractGameController` (UI updates/game-end logic), `SoundEffectListener` (plays SFX) |

---

## **Template Method Pattern**

### **Justification**

All three controllers shared the same lifecycle algorithm (game loop, listener registration, reset/exit behaviour). This led to major duplication and inconsistency. The Template Method pattern:

* Defines a single, reusable game lifecycle
* Ensures consistent behaviour across all controllers
* Extracts shared logic into one abstract class
* Allows subclasses to override only mode-specific steps

### **Implementation Details**

| Component | Details |
|---|---|
| Abstract class | `AbstractGameController.java` |
| Abstract methods | `getPlayers()`, `getGameArea()`, `isGameRunning()`, `doGameTick()`, `render()`, `updateUI()`, `handleGameEnd()`, `doReset()` |
| Hook methods | `beforeGameStart()`, `afterGameEnd()` |
| Template ops | `startGame()`, `stopGame()`, `gameTick()`, `registerPlayerListeners()`, `unregisterPlayerListeners()`, `onResetBtnClick()`, `onExitBtnClick()` |
| Inner class | `GameLoopTimer` — 20 ms tick loop |
| Concrete controllers | `StoryGameController`, `SurvivalGameController`, `TwoPlayerGameController` |

> See: Template Method pattern — Refactoring.Guru: https://refactoring.guru/design-patterns/template-method


---

# 5. Testing Summary

## 1. Test Plan

### 1.1 Testing Objectives
- Verify core game mechanics work correctly (collision detection, player movement, boost system)
- Ensure data persistence functions properly (high score management)
- Validate observer pattern implementation for player state changes
- Test boundary conditions and edge cases

### 1.2 Types of Testing Performed
- **Unit Testing:** Individual classes and methods mainly from Model and Controller layers
- **Integration Testing:** Covering initialization, state transitions, score persistence, and reset functionality

### 1.3 Testing Environment
- **Software:** Oracle OpenJDK 24, JavaFX 24
- **Testing Framework:** JUnit 5.10.2 (Jupiter)

---

## 2. Test Cases

### 2.1 Overview

**Unit Tests** — The following classes were tested in isolation:

- **Model Layer:** `LineSegment`, `HighScoreManager`, `Player`, `GameObject`, `Portal`, `PlayerAI`
- **Controller Layer:** `PlayerController`, `WallLayoutController`

**Integration Tests** — Component interactions tested across three game modes:

- **Survival Mode:** `SurvLogicStrategy` ↔ `Wall` ↔ `Portal` ↔ `GameData`
- **Story Mode:** `StoryLogicStrategy` ↔ `Wall` ↔ `Portal` ↔ `Player/AI` ↔ `GameData`
- **Two-Player Mode:** `TwoPlayerLogicStrategy` ↔ `Wall` ↔ `PlayerHuman` ↔ `GameData`

Each mode is tested for: Initialization, State Transitions, Score Persistence, and Reset.

---

## 2.2 Unit Test Cases

Below is a compact summary of unit test classes and their primary objectives. For a full list of test names, inputs and expected outcomes, see **Appendix A**.

| Test class | Core objective |
|---|---|
| LineSegmentTest | Verify geometric behavior of LineSegment (vertical/horizontal detection, min/max coords, zero-length segments) |
| HighScoreManagerTest | Validate score persistence, sorting, filtering by mode, and ScoreEntry parsing/comparison |
| PlayerTest | Check Player mechanics (boosts, velocity changes, collision/death handling, event notifications, out-of-bounds) |
| GameObjectCollisionTest | Test core collision cases (player vs player, player vs trails, edge cases) |
| PortalTest | Validate portal linking/teleportation, offsets, cooldown and exception handling |
| PlayerAITest | Ensure AI fundamentals (wall detection, non-trivial/random turning) |
| PlayerControllerTest | Confirm key-to-player mapping across single/two-player/mixed modes and invalid keys handling |
| WallLayoutControllerTest | Verify wall layout loading and spawn safety checks (`isPositionSafe()`) |

---

## 2.3 Integration Test Cases

Integration tests verify how components interact inside each game mode, focusing on four shared aspects: **Initialization**, **State Transitions**, **Score Persistence**, and **Reset**.

| Mode | Initialization | State Transitions | Score Persistence | Reset |
|---|---|---|---|---|
| Survival | Walls/portals load, player starts alive, score=0, state=PLAYING, GameData synced (6 tests) | Player death triggers GAME_OVER and stops the loop (2 tests) | Score increases each tick and GameData reflects updates (2 tests) | After reset: score=0, player alive, state=PLAYING, walls/portals reloaded, GameData synced (6 tests) |
| Story | Walls/portals load, 1 human + AI players, human is first, all alive, level=1 (7 tests) | Human death → GAME_OVER; all AI death → LEVEL_COMPLETE; game runs during play (3 tests) | Level progression increases score and AI count as expected (3 tests) | After reset: level=1, all players alive, state=PLAYING, walls/portals reloaded (4 tests) |
| Two-Player | Two human players created, walls loaded, both alive, scores=0, state=PLAYING (5 tests) | P1 death → P2_WIN; P2 death → P1_WIN; both death → TIE (4 tests) | Winner's score increases; tie doesn't change scores; scores accumulate across matches (4 tests) | After reset: both players alive, game running, walls reloaded (3 tests) |


---

# References

* Template Method pattern — Refactoring.Guru: https://refactoring.guru/design-patterns/template-method
* JavaFX Media API (SoundManager) — Oracle Docs: https://docs.oracle.com/javafx/2/api/javafx/scene/media/package-summary.html
* JavaFX Timeline — Oracle Docs: https://docs.oracle.com/javase/8/javafx/api/javafx/animation/Timeline.html

# Appendix

## A. Project File Structure

```
tron-master/
  pom.xml
  src/
    main/
      java/com/tron_master/tron/
        Game.java
        constant/GameConstant.java
        controller/
          interfaces/ 
          game_controller/ 
          sound/SoundEffectListener.java
        model/
          data/ 
          logic_strategy/ 
          object/ 
          sound/SoundManager.java
        view/
          game_view/  
          utils/  
      resources/  
        com/tron_master/tron/fxml/
          interfaces/
          custom_walls/
          javafx_settings.css
        images/ 
        sounds/ 
    test/java/com/tron_master/tron/
      unit/ 
      integration/ 
```

## B. Unit Test Tables

This appendix contains detailed test case tables for all unit test classes.

### LineSegment Tests

| Test Name                                                                 | Method Under Test                            | Test Inputs / Preconditions                      | Expected Outcome                     | Actual Outcome |
|---------------------------------------------------------------------------|----------------------------------------------|--------------------------------------------------|--------------------------------------|----------------|
| `LineSegmentTest.isVertical_returnsTrueWhenXCoordinatesAreEqual()`        | `LineSegment.isVertical()`                   | LineSegment(10, 0, 10, 100) where startX == endX | Returns `true`                       | PASS           |
| `LineSegmentTest.isVertical_returnsFalseWhenXCoordinatesDiffer()`         | `LineSegment.isVertical()`                   | LineSegment(0, 50, 100, 50) where startX != endX | Returns `false`                      | PASS           |
| `LineSegmentTest.isHorizontal_returnsTrueWhenYCoordinatesAreEqual()`      | `LineSegment.isHorizontal()`                 | LineSegment(0, 50, 100, 50) where startY == endY | Returns `true`                       | PASS           |
| `LineSegmentTest.isHorizontal_returnsFalseWhenYCoordinatesDiffer()`       | `LineSegment.isHorizontal()`                 | LineSegment(10, 0, 10, 100) where startY != endY | Returns `false`                      | PASS           |
| `LineSegmentTest.getMinMax_returnsCorrectValuesWhenStartLessThanEnd()`    | `LineSegment.getMinX/Y()`, `getMaxX/Y()`     | LineSegment(10, 20, 100, 200)                    | minX=10, maxX=100, minY=20, maxY=200 | PASS           |
| `LineSegmentTest.getMinMax_returnsCorrectValuesWhenStartGreaterThanEnd()` | `LineSegment.getMinX/Y()`, `getMaxX/Y()`     | LineSegment(100, 200, 10, 20)                    | minX=10, maxX=100, minY=20, maxY=200 | PASS           |
| `LineSegmentTest.zeroLengthSegment_isVerticalAndHorizontal()`             | `LineSegment.isVertical()`, `isHorizontal()` | LineSegment(50, 50, 50, 50) - a point            | Both return `true`                   | PASS           |

---

### HighScoreManager Tests

| Test Name                                                                  | Method Under Test                                | Test Inputs / Preconditions                  | Expected Outcome                  | Actual Outcome |
|----------------------------------------------------------------------------|--------------------------------------------------|----------------------------------------------|-----------------------------------|----------------|
| `HighScoreManagerTest.addScore_persistsAndReturnsInDescendingOrder()`      | `HighScoreManager.addScore()`, `getHighScores()` | Add scores 100, 300, 200 for SURVIVAL        | Returns [300, 200, 100]           | PASS           |
| `HighScoreManagerTest.getHighScores_filtersByGameMode()`                   | `HighScoreManager.getHighScores(GameMode)`       | Add 100(SURVIVAL), 200(STORY), 150(SURVIVAL) | SURVIVAL: [150,100], STORY: [200] | PASS           |
| `HighScoreManagerTest.getAllHighScores_returnsEmptyListWhenNoScores()`     | `HighScoreManager.getAllHighScores()`            | Empty score file                             | Returns empty list                | PASS           |
| `HighScoreManagerTest.scoreEntry_fromString_parsesValidFormat()`           | `ScoreEntry.fromString()`                        | Input: "150,SURVIVAL"                        | ScoreEntry(150, SURVIVAL)         | PASS           |
| `HighScoreManagerTest.scoreEntry_fromString_returnsNullForInvalidFormat()` | `ScoreEntry.fromString()`                        | Inputs: "invalid", "", null, "abc,SURVIVAL"  | All return `null`                 | PASS           |
| `HighScoreManagerTest.scoreEntry_compareTo_sortsDescending()`              | `ScoreEntry.compareTo()`                         | high(300) vs low(100)                        | high.compareTo(low) < 0           | PASS           |

---

### Player Tests

| Test Name                                               | Method Under Test                         | Test Inputs / Preconditions               | Expected Outcome                              | Actual Outcome |
|---------------------------------------------------------|-------------------------------------------|-------------------------------------------|-----------------------------------------------|----------------|
| `PlayerTest.startBoost_decreasesBoostCount()`           | `Player.startBoost()`                     | Player with boostLeft = 3                 | boostLeft = 2                                 | PASS           |
| `PlayerTest.startBoost_doesNothingWhenNoBoostLeft()`    | `Player.startBoost()`                     | Player with boostLeft = 0                 | boostLeft remains 0, not boosting             | PASS           |
| `PlayerTest.startBoost_updatesSurvivalGameData()`       | `Player.startBoost()`                     | Survival mode player                      | GameData.survivalBoost updated                | PASS           |
| `PlayerTest.boost_increasesVelocityWhenBoosting()`      | `Player.boost()`                          | After startBoost() called                 | velocityX = VELBOOST (5)                      | PASS           |
| `PlayerTest.boost_resetsVelocityAfterBoostEnds()`       | `Player.boost()`                          | After 20 boost ticks                      | velocityX = startVelocity, not boosting       | PASS           |
| `PlayerTest.crash_setsPlayerDeadOnIntersectionUp()`     | `Player.crash()`                          | Living player, crash(Intersection.UP)     | alive=false, velocityX=0, velocityY=0         | PASS           |
| `PlayerTest.crash_doesNothingOnIntersectionNone()`      | `Player.crash()`                          | crash(Intersection.NONE)                  | Player remains alive                          | PASS           |
| `PlayerTest.crash_doesNothingWhenAlreadyDead()`         | `Player.crash()`                          | Dead player, crash(UP)                    | No state change                               | PASS           |
| `PlayerTest.setAlive_notifiesListenerOnDeath()`         | `Player.setAlive()`, `addStateListener()` | Register listener, setAlive(false)        | onPlayerDied() called                         | PASS           |
| `PlayerTest.setAlive_doesNotNotifyWhenStillAlive()`     | `Player.setAlive()`                       | Register listener, setAlive(true)         | onPlayerDied() NOT called                     | PASS           |
| `PlayerTest.startBoost_notifiesListener()`              | `Player.startBoost()`                     | Register listener                         | onPlayerBoosted() called with remaining count | PASS           |
| `PlayerTest.removeStateListener_stopsNotifications()`   | `Player.removeStateListener()`            | Add then remove listener, setAlive(false) | onPlayerDied() NOT called                     | PASS           |
| `PlayerTest.accelerate_setsDeadWhenOutOfBoundsRight()`  | `Player.accelerate()`                     | Player x > GAME_AREA_WIDTH                | alive = false                                 | PASS           |
| `PlayerTest.accelerate_setsDeadWhenOutOfBoundsBottom()` | `Player.accelerate()`                     | Player y > GAME_AREA_HEIGHT               | alive = false                                 | PASS           |

---

### GameObject Collision Tests

| Test Name                                                                  | Method Under Test         | Test Inputs / Preconditions              | Expected Outcome                    | Actual Outcome |
|----------------------------------------------------------------------------|---------------------------|------------------------------------------|-------------------------------------|----------------|
| `GameObjectCollisionTest.intersects_returnsUpWhenPlayersOverlap()`         | `GameObject.intersects()` | Two players at (100, 100)                | Returns Intersection.UP             | PASS           |
| `GameObjectCollisionTest.intersects_returnsNoneWhenPlayersFarApart()`      | `GameObject.intersects()` | Player1(100,100), Player2(200,200)       | Returns Intersection.NONE           | PASS           |
| `GameObjectCollisionTest.intersects_returnsNoneWhenComparingSameObject()`  | `GameObject.intersects()` | player1.intersects(player1)              | Returns Intersection.NONE           | PASS           |
| `GameObjectCollisionTest.intersects_detectsCollisionWithHorizontalTrail()` | `GameObject.intersects()` | Player1(100,100), Player2 trail at y=100 | Returns Intersection.UP             | PASS           |
| `GameObjectCollisionTest.intersects_noCollisionWhenAboveHorizontalTrail()` | `GameObject.intersects()` | Player1(100,100), trail at y=200         | Returns Intersection.NONE           | PASS           |
| `GameObjectCollisionTest.intersects_detectsCollisionWithVerticalTrail()`   | `GameObject.intersects()` | Player1(100,100), Player2 trail at x=100 | Returns Intersection.UP             | PASS           |
| `GameObjectCollisionTest.intersects_noCollisionWhenBesideVerticalTrail()`  | `GameObject.intersects()` | Player1(100,100), trail at x=200         | Returns Intersection.NONE           | PASS           |
| `GameObjectCollisionTest.intersects_handlesEmptyPath()`                    | `GameObject.intersects()` | Player2 has no trail                     | Returns Intersection.NONE           | PASS           |
| `GameObjectCollisionTest.intersects_skipsLastTrailSegment()`               | `GameObject.intersects()` | Player2 has only 1 segment               | Returns Intersection.NONE (skipped) | PASS           |
| `GameObjectCollisionTest.intersects_detectsCollisionAtTrailEndpoint()`     | `GameObject.intersects()` | Player at trail endpoint                 | Returns Intersection.UP             | PASS           |

---

### Portal Tests

| Test Name                                                              | Method Under Test                          | Test Inputs / Preconditions                                  | Expected Outcome                                     | Actual Outcome |
|------------------------------------------------------------------------|--------------------------------------------|--------------------------------------------------------------|------------------------------------------------------|----------------|
| `PortalTest.linkToExit_successfullyLinksEntranceToExit()`              | `Portal.linkToExit()`, `handleCollision()` | Entrance portal links to exit, player collides with entrance | handleCollision returns `true` (teleport successful) | PASS           |
| `PortalTest.linkToExit_throwsExceptionWhenExitTriesToLink()`           | `Portal.linkToExit()`                      | Exit portal attempts to link                                 | Throws `IllegalStateException`                       | PASS           |
| `PortalTest.linkToExit_throwsExceptionWhenLinkingEntranceToEntrance()` | `Portal.linkToExit()`                      | Entrance links to another entrance                           | Throws `IllegalArgumentException`                    | PASS           |
| `PortalTest.teleport_movesPlayerToExitPosition()`                      | `Portal.teleport()`                        | Linked entrance, player teleports                            | Returns `true`, player X = exit center + offset (7)  | PASS           |
| `PortalTest.handleCollision_teleportsWhenPlayerCollidesWithEntrance()` | `Portal.handleCollision()`                 | Linked entrance, player inside portal bounds                 | Returns `true`                                       | PASS           |
| `PortalTest.handleCollision_returnsFalseWhenNoCollision()`             | `Portal.handleCollision()`                 | Linked entrance, player at (500, 500) far from portal        | Returns `false`                                      | PASS           |

---

### PlayerAI Tests

Focuses on two critical aspects of AI behavior: wall detection foundation and random turn behavior. Since AI "intelligence" is difficult to quantify (not too smart, not too dumb), only essential behaviors are tested.

| Test Name                                                             | Method Under Test      | Test Inputs / Preconditions  | Expected Outcome                                        | Actual Outcome |
|-----------------------------------------------------------------------|------------------------|------------------------------|---------------------------------------------------------|----------------|
| `PlayerAITest.GetWallLinesTest.singleWall_convertsToFourEdges()`      | `getWallLines()`       | Wall at (100,100) size 50x20 | Returns 4 LineSegments (top, bottom, left, right edges) | PASS           |
| `PlayerAITest.GetWallLinesTest.nullWalls_returnsEmptyList()`          | `getWallLines()`       | walls = null                 | Returns empty ArrayList                                 | PASS           |
| `PlayerAITest.GetWallLinesTest.emptyWalls_returnsEmptyList()`         | `getWallLines()`       | walls = empty array          | Returns empty ArrayList                                 | PASS           |
| `PlayerAITest.RandomTurnTest.randomTurn_eventuallyChangesDirection()` | `move()` (repeated 5x) | AI in open space, 200 frames | Direction changes ≥ 1 (AI is not a straight-line robot) | PASS           |

---

### PlayerController Tests

| Test Name                                                                          | Method Under Test                    | Test Inputs / Preconditions                              | Expected Outcome           | Actual Outcome |
|------------------------------------------------------------------------------------|--------------------------------------|----------------------------------------------------------|----------------------------|----------------|
| `PlayerControllerTest.EmptyPlayerList.shouldReturnNullWhenNoPlayers()`             | `PlayerController.getPlayerForKey()` | Empty player array, any key                              | Returns `null`             | PASS           |
| `PlayerControllerTest.SingleHumanPlayerMode.player1KeysShouldMapToPlayer1()`       | `PlayerController.getPlayerForKey()` | Single player, Arrow keys (UP/DOWN/LEFT/RIGHT), SPACE, B | Returns player1            | PASS           |
| `PlayerControllerTest.SingleHumanPlayerMode.player2KeysShouldFallbackToPlayer1()`  | `PlayerController.getPlayerForKey()` | Single player, WASD, Q, DIGIT1                           | Returns player1 (fallback) | PASS           |
| `PlayerControllerTest.TwoHumanPlayersMode.player1MovementKeysShouldMapToPlayer1()` | `PlayerController.getPlayerForKey()` | Two players, Arrow keys                                  | Returns player1            | PASS           |
| `PlayerControllerTest.TwoHumanPlayersMode.player1ActionKeysShouldMapToPlayer1()`   | `PlayerController.getPlayerForKey()` | Two players, SPACE, B                                    | Returns player1            | PASS           |
| `PlayerControllerTest.TwoHumanPlayersMode.player2MovementKeysShouldMapToPlayer2()` | `PlayerController.getPlayerForKey()` | Two players, WASD                                        | Returns player2            | PASS           |
| `PlayerControllerTest.TwoHumanPlayersMode.player2ActionKeysShouldMapToPlayer2()`   | `PlayerController.getPlayerForKey()` | Two players, Q, DIGIT1                                   | Returns player2            | PASS           |
| `PlayerControllerTest.MixedPlayerTypes.shouldOnlyRecognizeHumanPlayers()`          | `PlayerController.getPlayerForKey()` | Human + AI players                                       | Only human player returned | PASS           |
| `PlayerControllerTest.InvalidKeys.unrecognizedKeysShouldReturnNull()`              | `PlayerController.getPlayerForKey()` | F, ENTER, ESCAPE, TAB                                    | Returns `null`             | PASS           |

---

### WallLayoutController Tests

| Test Name                                                                                       | Method Under Test                       | Test Inputs / Preconditions                                    | Expected Outcome               | Actual Outcome |
|-------------------------------------------------------------------------------------------------|-----------------------------------------|----------------------------------------------------------------|--------------------------------|----------------|
| `WallLayoutControllerTest.PositionOutsideWalls.shouldReturnTrueWhenPositionFarFromWall()`       | `WallLayoutController.isPositionSafe()` | Wall at (100,100), position at (0,0), safeDistance=10          | Returns `true`                 | PASS           |
| `WallLayoutControllerTest.PositionOutsideWalls.shouldReturnTrueWhenJustOutsideSafeDistance()`   | `WallLayoutController.isPositionSafe()` | Wall at (100,100), position at (89,110), safeDistance=10       | Returns `true`                 | PASS           |
| `WallLayoutControllerTest.PositionInsideWallArea.shouldReturnFalseWhenInsideWall()`             | `WallLayoutController.isPositionSafe()` | Wall at (100,100) 50x20, position at (125,110), safeDistance=0 | Returns `false`                | PASS           |
| `WallLayoutControllerTest.PositionInsideWallArea.shouldReturnFalseWhenWithinSafeDistance()`     | `WallLayoutController.isPositionSafe()` | Wall at (100,100), position at (95,110), safeDistance=10       | Returns `false`                | PASS           |
| `WallLayoutControllerTest.PositionInsideWallArea.shouldReturnFalseWhenOnSafeDistanceBoundary()` | `WallLayoutController.isPositionSafe()` | Wall at (100,100), position at (90,110), safeDistance=10       | Returns `false`                | PASS           |
| `WallLayoutControllerTest.MultipleWalls.shouldReturnFalseIfNearAnyWall()`                       | `WallLayoutController.isPositionSafe()` | Two walls, position near wall2                                 | Returns `false`                | PASS           |
| `WallLayoutControllerTest.MultipleWalls.shouldReturnTrueOnlyIfSafeFromAllWalls()`               | `WallLayoutController.isPositionSafe()` | Two walls, position far from both                              | Returns `true`                 | PASS           |
| `WallLayoutControllerTest.EdgeCases.shouldWorkWithZeroSafeDistance()`                           | `WallLayoutController.isPositionSafe()` | Wall edge test, safeDistance=0                                 | (99,110)→true, (100,110)→false | PASS           |
| `WallLayoutControllerTest.EdgeCases.shouldHandleLargeSafeDistance()`                            | `WallLayoutController.isPositionSafe()` | Small wall, safeDistance=50                                    | Large exclusion zone           | PASS           |
