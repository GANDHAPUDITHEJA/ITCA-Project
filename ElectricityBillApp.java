import java.util.*;
abstract class Consumer {
    String consumerId;
    String name;
    int units;
    double billAmount;

    Consumer(String consumerId, String name, int units) throws InvalidUnitException {
        if (units < 0) throw new InvalidUnitException("Units consumed cannot be negative.");
        this.consumerId = consumerId;
        this.name = name;
        this.units = units;
    }

    abstract double calculateBill();
}
class DomesticConsumer extends Consumer {
    DomesticConsumer(String id, String name, int units) throws InvalidUnitException {
        super(id, name, units);
    }

    @Override
    double calculateBill() {
        int u = units;
        billAmount = 0;
        if (u <= 100) billAmount = u * 1.5;
        else if (u <= 300) billAmount = 100 * 1.5 + (u - 100) * 2.5;
        else if (u <= 500) billAmount = 100 * 1.5 + 200 * 2.5 + (u - 300) * 4.0;
        else billAmount = 100 * 1.5 + 200 * 2.5 + 200 * 4.0 + (u - 500) * 6.0;
        return billAmount;
    }
}
class CommercialConsumer extends Consumer {
    CommercialConsumer(String id, String name, int units) throws InvalidUnitException {
        super(id, name, units);
    }

    @Override
    double calculateBill() {
        billAmount = units * 6.5;
        billAmount += billAmount * 0.10; // 10% tax
        return billAmount;
    }
}
class IndustrialConsumer extends Consumer {
    IndustrialConsumer(String id, String name, int units) throws InvalidUnitException {
        super(id, name, units);
    }

    @Override
    double calculateBill() {
        billAmount = 0;
        int u = units;
        if (u <= 500) billAmount = u * 5.0;
        else if (u <= 1500) billAmount = 500 * 5.0 + (u - 500) * 6.5;
        else billAmount = 500 * 5.0 + 1000 * 6.5 + (u - 1500) * 8.0;

        billAmount += billAmount * 0.05; // 5% Fuel Charge
        billAmount += 250; // Demand charge
        return billAmount;
    }
}
class InvalidUnitException extends Exception {

    public InvalidUnitException(String message) {
        super(message);
    }
}
public class ElectricityBillApp {
    public static void main(String[] args) throws NumberFormatException {
        Scanner sc = new Scanner(System.in);
        ArrayList<Consumer> consumers = new ArrayList<>();
        boolean running = true;

        while (running) {
            System.out.println("\nMAIN MENU\n---------");
            System.out.println("1. Generate New Bill");
            System.out.println("2. Display All Bills");
            System.out.println("3. Exit");
            System.out.print("Enter option (1–3): ");
            int choice = Integer.parseInt(sc.nextLine());

            try {
                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter Consumer ID: ");
                        String id = sc.nextLine();

                        System.out.print("Enter Name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter Units Consumed: ");
                        int units = Integer.parseInt(sc.nextLine());

                        System.out.print("Enter Type (1–Domestic, 2–Commercial, 3–Industrial): ");
                        int type = Integer.parseInt(sc.nextLine());

                        Consumer consumer = switch (type) {
                            case 1 -> new DomesticConsumer(id, name, units);
                            case 2 -> new CommercialConsumer(id, name, units);
                            case 3 -> new IndustrialConsumer(id, name, units);
                            default -> throw new IllegalArgumentException("Invalid consumer type.");
                        };

                        consumer.calculateBill();
                        consumers.add(consumer);
                        System.out.printf("\n---- Bill Details ----\nConsumer: %s (%s)\nUnits: %d\nTotal Bill: ₹%.2f\n",
                                consumer.name,
                                switch (type) {
                                    case 1 -> "Domestic";
                                    case 2 -> "Commercial";
                                    case 3 -> "Industrial";
                                    default -> "Unknown";
                                },
                                consumer.units,
                                consumer.billAmount
                        );
                    }

                    case 2 -> {
                        if (consumers.isEmpty()) {
                            System.out.println("No bills to display.");
                        } else {
                            System.out.printf("\n%-10s %-15s %-10s %-12s %-10s\n",
                                    "ID", "Name", "Units", "Type", "Bill (Rs.)");
                            System.out.println("-----------------------------------------------------");
                            for (Consumer c : consumers) {
                                System.out.printf("%-10s %-15s %-10d %-12s Rs.%.2f\n",
                                        c.consumerId,
                                        c.name,
                                        c.units,
                                        c instanceof DomesticConsumer ? "Domestic" :
                                                c instanceof CommercialConsumer ? "Commercial" : "Industrial",
                                        c.billAmount);
                            }
                        }
                    }

                    case 3 -> {
                        running = false;
                        System.out.println("Exiting...");
                    }

                    default -> System.out.println("Invalid option.");
                }
            } catch (InvalidUnitException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        sc.close();
    }
}
