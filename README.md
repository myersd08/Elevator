## Elevator Program
This program simulates an elevator
- The elevator can be programmed for how many floors, capacity and speed.
- When the elevator is on a current floor, it can pick up passengers that are on that floor by typing a destination floor number (assumes starting floor is the current floor)
- The elevator can be called from other floors to specify the starting floor and the destination floor.  These are maintained in a separate queue and added to the passengers queue when they are picked up.
- When the elevator stops at a floor, it drops off passengers first, then picks up passengers (if any).
- The elevator tries to maintain it's current direction (UP or DOWN), but will change direction automatically if there is no need to continue in the current direction (no drop offs or pick ups)
- The elevator will not allow new passengers if it is full
- User input is validated.  It is also superstitous so there is no floor 13 :)
- This programs includes some tests, but more tests should be added

*** The elevator program has a speed control function that has the elevator accelerate and decelerate
*** Messages are color coded in the console to make it easier to read

## Additional Features/Ideas
- Make this a multi-elevator system (array of elevators).  The elevator bank controller will determine what elevator to use based on the one closest to the person and going the direction the person wants to go.
- Add a PID loop to the elevator controller to simulate soft start and stop of an elevator.  Right now it just simulates a soft start/stop.
