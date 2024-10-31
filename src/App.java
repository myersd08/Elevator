import java.util.Scanner;
import Enums.ElevatorSpeed;
import Enums.Colors;

public class App {
    private static final int MIN_FLOORS = 2;
    private static final int MAX_FLOORS = 100;
    private static final int MIN_CAPACITY = 1;
    private static final int MAX_CAPACITY = 10;
    private static final int FORBIDDEN_FLOOR = 13;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        try {
            ElevatorController controller = setupElevator(input);
            runElevator(controller, input);
        } finally {
            input.close();
        }
    }

    private static ElevatorController setupElevator(Scanner input) {
        System.out.println("Welcome to this elevator! When prompted, type the floor you want to go to.\n");
        System.out.println("Type the number of the floor you want to go to from the current floor.\n");
        System.out.println("Type the 'call' to call the elevator from a different floor than the current one.\n");
        System.out.println("Now, let's set up the elevator.");
        
        int numberOfFloors = getValidInput(
            input,
            String.format("How many floors are in this building (between %d and %d)?", MIN_FLOORS, MAX_FLOORS),
            MIN_FLOORS,
            MAX_FLOORS
        );

        int elevatorCapacity = getValidInput(
            input,
            String.format("How many people can this elevator hold at once (between %d and %d)?", MIN_CAPACITY, MAX_CAPACITY),
            MIN_CAPACITY,
            MAX_CAPACITY
        );

        ElevatorSpeed elevatorSpeed = getValidSpeed(input);
        
        System.out.println("\n\nAll set! Let's visit some floors!\n");
        return new ElevatorController(numberOfFloors, elevatorCapacity, elevatorSpeed);
    }

    private static int getValidInput(Scanner input, String prompt, int min, int max) {
        while (true) {
            System.out.println(prompt);
            if (input.hasNextInt()) {
                int value = input.nextInt();
                if (value >= min && value <= max) {
                    input.nextLine(); // consume newline
                    return value;
                }
            } else {
                input.nextLine(); // consume invalid input
            }
            System.out.printf(Colors.ANSI_RED + "Please enter a number between %d and %d\n" + Colors.ANSI_RESET, min, max);
        }
    }

    private static ElevatorSpeed getValidSpeed(Scanner input) {
        while (true) {
            System.out.println("How fast would you like the elevator to be? (slow, medium, fast)");
            try {
                return ElevatorSpeed.valueOf(input.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println(Colors.ANSI_RED + "Please enter either 'slow', 'medium', or 'fast'" + Colors.ANSI_RESET);
            }
        }
    }

    private static void runElevator(ElevatorController controller, Scanner input) {
        System.out.println("If everyone is done loading, type 'go'.");
        System.out.println("To end the program, type 'exit'.");

        while (true) {
            System.out.println();
            if (!processPassengers(controller, input)) {
                break;
            }
        }
    }

    private static boolean processPassengers(ElevatorController controller, Scanner input) {
        boolean hasAddedPassengers = false;
        
        while (true) {
            if (!hasAddedPassengers) {
                System.out.println("This elevator is currently on floor " + controller.getCurrentFloor() + ".");
                System.out.println("Type a number to go to a floor, Type 'call' to call from another floor. If not type 'go'.");
            } else if (controller.elevatorIsFull()) {
                System.out.println("Type a number to go to a floor, 'call' to call from another floor or type 'go'.");
            } else {
                System.out.println("Anyone else? Type 'call' to call from another floor. If not type 'go'.");
            }

            if (input.hasNextInt()) {
                int destinationFloor = input.nextInt();
                input.nextLine(); // consume newline
                
                if (destinationFloor == FORBIDDEN_FLOOR) {
                    System.out.println(Colors.ANSI_RED + "There is no floor 13, please try again." + Colors.ANSI_RESET);
                    continue;
                }
                controller.addPassenger(destinationFloor);
                hasAddedPassengers = true;
            } else {
                String command = input.nextLine().trim();
                if (command.equalsIgnoreCase("go")) {
                    controller.move();
                    return true;
                } else if (command.equalsIgnoreCase("call")) {
                    int startFloor = getValidInput(
                        input,
                        "What floor are you calling from?",
                        1,
                        controller.getTopFloor()
                    );
                    
                    if (startFloor == FORBIDDEN_FLOOR) {
                        System.out.println(Colors.ANSI_RED + "There is no floor 13, please try again." + Colors.ANSI_RESET);
                        continue;
                    }

                    int destinationFloor = getValidInput(
                        input,
                        "What floor would you like to go to?",
                        1,
                        controller.getTopFloor()
                    );
                    
                    if (destinationFloor == FORBIDDEN_FLOOR) {
                        System.out.println(Colors.ANSI_RED + "There is no floor 13, please try again." + Colors.ANSI_RESET);
                        continue;
                    }

                    if (startFloor == destinationFloor) {
                        System.out.println(Colors.ANSI_RED + "Start and destination floors cannot be the same." + Colors.ANSI_RESET);
                        continue;
                    }
                    controller.addPassengerCall(startFloor, destinationFloor);
                    return true;
                } else if (command.equalsIgnoreCase("exit")) {
                    return false;
                } else {
                    System.out.println(Colors.ANSI_RED + "That is not a valid command, please try again." + Colors.ANSI_RESET);
                }
            }
        }
    }
}