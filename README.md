# The Last Line

Tetris' implementation in Java using LibGDX. Gameplay rules mostly based on NES Tetris.

## Technical details

- Follow most of NES Tetris rules
- Lock down time not implemented.
- ARE/entry delay allow move and rotate pieces as in NES.

Useful links:

- [Tetris Guideline](https://tetris.wiki/Tetris_Guideline)
- [Tetris NES](https://tetris.fandom.com/wiki/Tetris_(NES,_Nintendo))

## GAME TYPES

- Marathon: 30 levels
- Sprint: Choose level and try to clear 40 lines as fast as possible.
- Ultra: score as many points as possible within a three-minute time span.

## TODO

- [ ] Clean ui.atlas
- [x] Pause/Resume
- [ ] Tile color by type
- [?] lines for level up depends on level (see NES)
- [ ] Level up animation
- [?] Lock down time (0.5 seconds) - Move time in the ground
- [?] Implement ARE/entry delay (0.2 seconds) - ARE depends on last line position
- [x] Split renderer into separate class
- [x] Use RectangleRenderer to render all the game
- [x] ScreenViewport instead of FitViewport
- [x] Add more game modes
  - [x] Marathon
  - [x] Sprint (40 lines): Choose level and try to clear 40 lines as fast as possible
  - [x] Ultra: a) score as many points, OR b) clear as many lines as possible within a two or three minute time span.
- [ ] Bag randomizer
- [ ] Sound effects
- [ ] Music