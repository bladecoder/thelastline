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

- Marathon
  - 30 levels

## TODO

- [ ] Clean ui.atlas
- [ ] lines for level up depends on level (see NES)
- [ ] Level up animation
- [ ] Lock down time (0.5 seconds) - Move time in the ground
- [x] Implement ARE/entry delay (0.2 seconds)
- [ ] Split renderer into separate class
- [ ] Use RectangleRenderer to render all the game
- [ ] ScreenViewport instead of FitViewport
- [ ] Add more game modes
  - [ ] Marathon
  - [ ] Sprint (40 lines): Choose level and try to clear 40 lines as fast as possible
  - [ ] Ultra: a) score as many points, OR b) clear as many lines as possible within a two or three minute time span.
- [ ] Bag randomizer
- [ ] Sound effects
- [ ] Music