# Repository Guidelines

## Project Structure & Module Organization
- Root contains `README.md`, `LICENSE`, and the `tron-master/` game source.
- All Java sources sit directly inside `tron-master/` (e.g., `Game.java`, `Player.java`, `TronMap*.java`) and share the default package so compile/run from that folder.
- UI assets (`*.png`, `*.jpg`) and `HighScores.txt` also live in `tron-master/`; `Picture` loads them by filename, so keep relative paths unchanged.

## Build, Test, and Development Commands
- `cd tron-master && javac *.java` — compiles every class against the standard JDK (Swing/AWT only). Clean `*.class` files before committing.
- `cd tron-master && java Game` — launches the main menu; use this for local play-testing.
- `rm tron-master/*.class` — quick reset when switching JDKs or before packaging.

## Coding Style & Naming Conventions
- Follow the existing tab-based indentation (1 tab ≈ 4 spaces) and brace-on-same-line Java style.
- Classes stay in PascalCase (`TronMapSurvival`), fields/methods/counters in lowerCamelCase, and constants in ALL_CAPS.
- Keep logic in the relevant `TronMap*` subclasses; UI wiring belongs in `Game` while mechanics live in `Player*`, `Score`, or `Intersection`.
- Reuse helper methods rather than duplicating event-handler code; extract repeated listeners into private methods when possible.

## Testing Guidelines
- No automated tests yet; rely on manual regression checks via `java Game`.
- Exercise all three modes (Story, Survival, Two Player) plus menus after changes touching `Player*`, `TronMap*`, or image resources.
- When editing scoring, update `HighScores.txt` test data and confirm the HUD labels refresh (Survival menu shows `Score`/`Boost`).

## Commit & Pull Request Guidelines
- Keep commit subjects short and descriptive, mirroring existing history (`Init raw project.`); use imperative tone when possible.
- Group UI assets with the feature that needs them, and mention image names in the commit body if new files are added.
- PRs should describe gameplay impact, list modified classes, and attach screenshots of any new screens (`main_menu`, `survival`, etc.).
- Link tracking issues or TODOs, note manual test scenarios run, and call out any assets or config files that require reviewers’ attention.
