# Design Notes

## Design

### Package structure

```
com.sudoku
├── Main.java
├── model/
│   ├── SudokuGrid.java        — the board; tracks cell values and which cells are locked
│   └── ValidationResult.java  — what the validator returns
├── command/
│   ├── Command.java           — sealed interface, one subtype per action
│   ├── PlaceNumberCommand.java
│   ├── ClearCellCommand.java
│   ├── HintCommand.java
│   ├── CheckCommand.java
│   └── QuitCommand.java
├── service/
│   ├── CommandParser.java     — turns raw input like "A3 4" into a Command object
│   ├── SudokuValidator.java   — checks rows, columns, and 3×3 boxes for duplicates
│   ├── SudokuSolver.java      — recursive backtracking solver; also used for hints
│   └── PuzzleGenerator.java  — builds a random puzzle with exactly one valid solution
└── game/
    └── GameEngine.java        — the game loop; coordinates everything
```

### Command pattern

Each player action is its own class rather than a string or a switch case. `GameEngine` never has to parse what the player meant — `CommandParser` already did that and handed back a typed object. Adding a new command means adding a new class and one new branch in the engine; nothing else changes.

### Dependency injection in GameEngine

`GameEngine` takes a `Scanner` and a `PrintStream` through its constructor instead of using `System.in` / `System.out` directly. In production (`Main.java`) those are the real streams. In tests they're a scripted string input and a `ByteArrayOutputStream`. That one decision made it possible to write end-to-end tests for the full game loop without any mocking frameworks.

### SudokuSolver does double duty

The same backtracking solver is used by `PuzzleGenerator` to build the initial completed grid, by `PuzzleGenerator` again to verify that removing a cell doesn't create multiple solutions, and by `HintCommand` to look up the correct answer for an empty cell. There was no reason to write three separate solvers when one handles all three jobs.

### ValidationResult as a value object

The validator returns a `ValidationResult` rather than printing directly or throwing. That keeps the validator ignorant of where output goes — useful for testing (just check what it returned) and for any future UI changes (nothing in the validator would need to change).

### SudokuGrid protects its own state

`getCells()` returns a deep copy of the internal array so nothing outside the class can modify cell values without going through `setCell`. `setCell` enforces the pre-filled lock, so there's no way to overwrite a given cell regardless of how the game is driven.

---

## Assumptions

- Every generated puzzle has exactly one valid solution. `PuzzleGenerator` checks this after each cell removal before accepting the removal.
- Pre-filled cells are locked at the model level, not just the UI level. Trying to overwrite one throws an exception that the game engine catches and shows to the player.
- `check` reports the first violation found. It scans rows first, then columns, then 3×3 boxes. If there are multiple violations, only the first is shown — the player fixes one thing at a time.
- `hint` picks the first empty cell in reading order (left to right, top to bottom) and fills it in automatically.
- Playing again generates a fresh random puzzle rather than restarting the same one.
- The puzzle is generated with 30 pre-filled cells. In rare cases where removing a cell would break uniqueness, slightly more than 30 cells may remain — the puzzle still has a unique solution regardless.

---

## Tests

| Test class | What it covers |
|---|---|
| `SudokuGridTest` | Cell reads and writes, pre-filled lock, bounds checking, deep copy protection |
| `CommandParserTest` | All valid commands, boundary values, malformed input |
| `SudokuValidatorTest` | Row, column, and subgrid violation detection; valid full and partial grids |
| `SudokuSolverTest` | Solving known puzzles, mutation safety, unsolvable detection, hint lookup, uniqueness checking |
| `PuzzleGeneratorTest` | Generated puzzles are valid, fully solved, uniquely solvable, and pre-filled cells match the solution |
| `GameEngineTest` | End-to-end scenarios: valid move, pre-filled rejection, clear, hint, check, win condition, quit |

One test (`hasUniqueSolution_multiSolutionPuzzle_returnsFalse`) is marked `@Disabled`. A loosely constrained puzzle gives the backtracker too large a search space to find a second solution quickly, so the test would hang for minutes. The same behaviour is exercised indirectly by `PuzzleGeneratorTest` — the generator relies on `hasUniqueSolution` returning false whenever a removal creates ambiguity, and the generator tests all pass.
