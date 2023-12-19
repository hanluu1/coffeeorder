import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import java.io.*;

public class Main {
    private static Map<String, Integer> inventory = new TreeMap<String, Integer>();
    private static List<CoffeeOrder> orders = new ArrayList<CoffeeOrder>();
    private static String logFile = "OrderLog.txt";
    private static String inventoryFile = "Inventory.txt";

    public static void main(String[] args) {
        readInventory(inventoryFile);
        System.out.println("Welcome to Java Coffee Co.!");
        try (Scanner input = new Scanner(System.in)) {
            boolean addOrder;
            do {
                CoffeeOrder order = buildOrder();
                orders.add(order);
                System.out.println(order.printOrder());

                System.out.println("\nWould you like to enter another order (Y or N)?");
                String yn = input.nextLine();
                while (!(yn.equalsIgnoreCase("N") || yn.equalsIgnoreCase("Y"))) {
                    System.out.println("Please enter Y or N.");
                    yn = input.nextLine();
                }
                addOrder = !yn.equalsIgnoreCase("N");
            } while (addOrder);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        if (orders.size() > 0) {
            writeOrderLog(logFile);
        }
        writeInventory(inventoryFile);
    }

    // use buildOrder()
// to build a new CoffeeOrder and add it to orders.
    private static CoffeeOrder buildOrder() {
        CoffeeOrder order = new CoffeeOrder();
        try {
            Scanner input = new Scanner(System.in);
            boolean addCoffee = true;
            while (addCoffee) {
                // prompt user to select base coffee type
                System.out.println("Select coffee type:");
                System.out.println("\t1. Black Coffee");
                System.out.println("\t2. Espresso");
                Coffee coffee;

                int option = 0;
                while (option < 1 || option > 2) {
                    if (!input.hasNextInt()) {
                        System.out.println("Please enter a valid number.");
                        input.nextLine();
                    } else {
                        option = input.nextInt();
                        if (option < 1 || option > 2) System.out.println("Please enter a valid option.");
                    }
                }

                input.nextLine(); // nextInt() doesn't consume newline
                if (option == 2) {
                    // Espresso is a specific case
                    coffee = new Espresso();
                    inventory.put("Espresso",inventory.get("Espresso")-1);
                } else {
                    if (!isInInventory("Black Coffee")) {
                        System.out.println("Sorry, the selected base coffee is not available.");
                        continue;
                    }
                    // make BlackCoffee the default case
                    coffee = new BlackCoffee();
                    inventory.put("Black Coffee",inventory.get("Black Coffee")-1);

                }

//Update buildOrder() so that it uses isInInventory to check if an ingredient exists before
// using it. For example, if there is no Black Coffee in inventory, your program should not proceed
// with creating a new BlackCoffee, but should warn the user that no black coffee is left.
//When an ingredient is used in a new Coffee, inventory should be updated to subtract 1 from the current quantity.


                // prompt user for any customizations
                while (option != 0) {
                    System.out.println(String.format("Coffee brewing: %s.", coffee.printCoffee()));
                    System.out.println("Would you like to add anything to your coffee?");
                    System.out.println("\t1. Flavored Syrup");
                    System.out.println("\t2. Hot Water");
                    System.out.println("\t3. Milk");
                    System.out.println("\t4. Sugar");
                    System.out.println("\t5. Whipped Cream");
                    System.out.println("\t0. NO - Finish Coffee");

                    while (!input.hasNextInt()) {
                        System.out.println("Please enter a valid number.");
                        input.nextLine();
                    }
                    option = input.nextInt();
                    input.nextLine();
                    coffee = switch (option) {
                        case 1 -> {
                            System.out.println("Please select a flavor:");
                            for (WithFlavor.Syrup flavor : WithFlavor.Syrup.values()) {
                                System.out.println("\t" + String.format("%d. %s", flavor.ordinal() + 1, flavor));
                            }
                            int max = WithFlavor.Syrup.values().length;
                            option = 0;
                            while (option < 1 || option > max) {
                                if (!input.hasNextInt()) {
                                    System.out.println("Please enter a valid number.");
                                    input.nextLine();
                                } else {
                                    option = input.nextInt();
                                    if (option < 1 || option > max)
                                        System.out.println("Please enter a valid option.");
                                }
                            }
                            input.nextLine();
                            WithFlavor.Syrup flavor = WithFlavor.Syrup.values()[option - 1];
                                inventory.put(flavor+ " Syrup",inventory.get(flavor+ " Syrup")-1);
                            option = 1;
                            yield new WithFlavor(coffee, flavor);

                        }
                        case 2 ->{
                            inventory.put("Hot Water",inventory.get("Hot Water")-1);
                            yield new WithHotWater(coffee);

                        }
                        case 3 ->{
                            inventory.put("Milk",inventory.get("Milk")-1);
                            yield new WithMilk(coffee);
                        }
                        case 4 ->{
                            inventory.put("Sugar", inventory.get("Sugar") - 1);
                            yield new WithSugar(coffee);
                        }
                        case 5 -> {
                            inventory.put("Whipped Cream", inventory.get("Whipped Cream") - 1);
                            yield new WithWhippedCream(coffee);
                        }
                        default -> {
                            if (option != 0) System.out.println("Please enter valid option.");
                            yield coffee;
                        }
                    };
                }
                order.addCoffee(coffee);


                System.out.println("Would you like to order another coffee (Y or N)?");
                String yn = input.nextLine();
                while (!(yn.equalsIgnoreCase("N") || yn.equalsIgnoreCase("Y"))) {
                    System.out.println("Please enter Y or N.");
                    yn = input.nextLine();
                }
                addCoffee = !yn.equalsIgnoreCase("N");
            }
        } catch (Exception e) {
            System.out.println("Error building order: " + e.getMessage());
        }
        return order;
    }

    private static Map<String, Integer> readInventory(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line; //read in each line from the file and parse out the ingredient's name and quantity.
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String ingredient = parts[0].trim();
                    int quantity = Integer.parseInt(parts[1].trim());
                    inventory.put(ingredient, quantity);
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading inventory: " + e.getMessage());
        }
        return inventory;
    }


    private static void writeInventory(String filePath) {
        try {
            FileWriter output = new FileWriter(filePath);
            BufferedWriter writer = new BufferedWriter(output);
            for (String ingredient : inventory.keySet()) {
                int quantity = inventory.get(ingredient);
                writer.write(ingredient + " = " + quantity);
                writer.newLine();
            }
            writer.close();
            output.close();

        } catch (Exception e) {
            System.out.println("Error writing inventory: " + e.getMessage());
        }

    }

    private static List<CoffeeOrder> readOrderLog(String filePath) {
        return null;
    }

    private static void writeOrderLog(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            for (CoffeeOrder order : orders) {
                writer.write(order.printOrder());
                writer.newLine();
            }
            orders.clear();
            System.out.println("successfully print order log.");

        } catch (Exception e) {
            System.out.println("Error writing order log: " + e.getMessage());
        }
    }
//Implement isInInventory() so that it returns true if the input ingredient both exists in
// inventory and has a non-zero quantity, and false otherwise.
//Calling isInInventory("apple") should return false, not throw an exception.
//Calling isInInventory("Black Coffee") should return false if looking up "Black Coffee"
// in inventory returns a value of 0.

    private static boolean isInInventory(String i) {
        int quantity = inventory.getOrDefault(i, 0);
        return quantity > 0;
    }
}

