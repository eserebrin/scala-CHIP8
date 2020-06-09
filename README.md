# scala-CHIP8

This was my first attempt at any sort of emulation. It has a debugger and as a whole seems to work fairly well.

There is one known issue, having something to do with rendering or collision detection. Most roms are still playable, though.

## Usage
- If you'd like to test it out, you will need [Scala Build Tool](https://www.scala-sbt.org/) installed.
- use ```sbt run``` to run the emulator.
- To change the rom currently in use, edit the source code in Chip8.scala on line 16, where the memory is created.
