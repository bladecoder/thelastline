# The Last Line

This is a Tetris implementation for modern devices. Most of the gameplay rules are based on the NES Tetris.

The game is written in Java using [libGDX](https://libgdx.badlogicgames.com/).

Packages are available for Windows, Linux, Macos and Android devices here: [Releases](https://github.com/bladecoder/thelastline/releases).

During the Christmas holidays of 2023 a boy named Blue Scutti beat the NES Tetris, after many years, this event awakened my interest in Tetris again. So I decided to write my own version as a relaxing holiday activity.

After a couple of weeks, this is the result. I hope you enjoy it.

## Technical details

- Follow most of NES Tetris rules.
- ARE/entry delay allow move and rotate pieces as in NES.
- Lock down time is not implemented.

Useful links:

- [Tetris Guideline](https://tetris.wiki/Tetris_Guideline)
- [Tetris NES](https://tetris.fandom.com/wiki/Tetris_(NES,_Nintendo))

## GAME TYPES

- Marathon: 30 levels. Level up every 10 lines.
- Sprint: Choose level and try to clear 40 lines as fast as possible.
- Ultra: score as many points as possible within a three-minute time span.

## Nice to have

- [ ] Rotate right por touch screens
- [ ] Music
- [ ] Statistics screen
- [ ] Lines for level up depends on level (see NES)
- [ ] iOS version

## WON'T DO

- Lock down time (Move in the ground)
- Bag randomizer
- Ghost piece
- Hold piece
- Next pieces
