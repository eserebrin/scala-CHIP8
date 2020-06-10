# scala-CHIP8

This was my first attempt at any sort of emulation. It has a debugger and as a whole seems to work fairly well.

There is one known issue, having something to do with rendering or collision detection. Most roms are still playable, though.

## Usage

### Running the Program
- If you'd like to test it out, you will need [Scala Build Tool](https://www.scala-sbt.org/) installed.
- use ```sbt run <rom> debug``` to run the emulator.
  - ```rom``` should be a file in the project directory.
  - ```debug``` is *optional*. If included, will start the emulator in debug mode.

### Controls
**Hex Keyboard**  
123C  ->  1234  
456D  ->  qwer  
789E  ->  asdf  
A0BF  ->  zxcv  

\[Space\] -> Step once (in debug mode)
