### PLEASE REPORT ANY BUGS FOUND AND UNCAUGHT EXCEPTIONS TO dangar13@ucm.es OR POST THEM AT THE ISSUES TAB.

#### ChessDelta Alpha v0.1:
> It is already possible to play a 1 vs 1 match for two human players on the same computer, following the rules.

- Clicking on the edge of a cell will make the program believe that the user clicked outside the cell, on a adjacent one. This will not cause any uncaught exception since the game already takes into account that the position must be valid. However, it could cause confusion to the user. This will be fixed in a later version.
- It is more than likely that most the side panel buttons for the game won't work. Some functions have not been implemented yet.

-----------------------------------------------------------------------------

#### ChessDelta Alpha v0.2:
> It is already possible to play a 1 vs 1 match for two human players on the same computer, following the rules.
> Some bugfixes from ChessDelta Alpha v0.1.

+ Clicking on the edge of a cell already works properly.
+ Other minor bugs fixed.
+ Side panel still needs to be modified.

-----------------------------------------------------------------------------

#### ChessDelta Alpha v0.3:
> It is already possible to play against a Dummy AI player and an experimental MinMax Player.

+ Fixed some bugs from last version.
+ Important changes to internal game structure. Although they are not visible, they improve efficiency and fix several issues.
+ Added a feature to highlight clicked tiles in green to enhance User Experience thanks to the feedback from a tester.
+ Artificial Intelligence is not working properly yet, although it's possible to play against it.
