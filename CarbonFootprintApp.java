import java.util.*;

// Abstract Base Class
abstract class EmissionSource {
    double emission;
    abstract double calculateEmission();
}

// Custom Exception
class InvalidUserInputException extends Exception {
    public InvalidUserInputException(String message) {
        super(message);
    }
}

// TransportEmission Class
class TransportEmission extends EmissionSource {
    private String vehicleType;
    private double km;

    private static final Map<Integer, String> vehicleTypes = Map.of(
        1, "Petrol Car", 2, "Diesel Car", 3, "Motorbike",
        4, "Bus", 5, "Train", 6, "Flight"
    );

    private static final Map<String, Double> emissionFactors = Map.of(
        "Petrol Car", 0.192, "Diesel Car", 0.171, "Motorbike", 0.072,
        "Bus", 0.027, "Train", 0.041, "Flight", 0.255
    );

    public TransportEmission(int vehicleChoice, double km) throws InvalidUserInputException {
        if (!vehicleTypes.containsKey(vehicleChoice) || km < 0) {
            throw new InvalidUserInputException("Invalid transport input.");
        }
        this.vehicleType = vehicleTypes.get(vehicleChoice);
        this.km = km;
    }

    @Override
    double calculateEmission() {
        emission = km * emissionFactors.get(vehicleType);
        return emission;
    }

    @Override
    public String toString() {
        return String.format("Transport:     %.2f kg CO2", calculateEmission());
    }
}

// ElectricityEmission Class
class ElectricityEmission extends EmissionSource {
    private double annualKWh;
    private static final double FACTOR = 0.82;

    public ElectricityEmission(double annualKWh) throws InvalidUserInputException {
        if (annualKWh < 0) throw new InvalidUserInputException("Electricity consumption cannot be negative.");
        this.annualKWh = annualKWh;
    }

    @Override
    double calculateEmission() {
        emission = annualKWh * FACTOR;
        return emission;
    }

    @Override
    public String toString() {
        return String.format("Electricity:   %.2f kg CO2", calculateEmission());
    }
}

// DietEmission Class
class DietEmission extends EmissionSource {
    private String dietType;

    private static final Map<Integer, String> dietTypes = Map.of(
        1, "Meat heavy", 2, "Average", 3, "Vegetarian", 4, "Vegan"
    );

    private static final Map<String, Double> annualEmission = Map.of(
        "Meat heavy", 3200.0, "Average", 2500.0,
        "Vegetarian", 1700.0, "Vegan", 1500.0
    );

    public DietEmission(int dietChoice) throws InvalidUserInputException {
        if (!dietTypes.containsKey(dietChoice)) {
            throw new InvalidUserInputException("Invalid diet input.");
        }
        this.dietType = dietTypes.get(dietChoice);
    }

    @Override
    double calculateEmission() {
        emission = annualEmission.get(dietType);
        return emission;
    }

    @Override
    public String toString() {
        return String.format("Diet:          %.2f kg CO2", calculateEmission());
    }
}

// Main Application
public class CarbonFootprintApp {
    private static final Scanner sc = new Scanner(System.in);
    private static final HashMap<String, EmissionSource> sources = new HashMap<>();

    public static void main(String[] args) {
        int choice;
        do {
            showMenu();
            choice = getIntInput("Choose option (1-5): ");
            try {
                switch (choice) {
                    case 1 -> addTransport();
                    case 2 -> addElectricity();
                    case 3 -> addDiet();
                    case 4 -> calculateFootprint();
                    case 5 -> System.out.println("Exiting... Goodbye!");
                    default -> throw new InvalidUserInputException("Option out of range.");
                }
            } catch (InvalidUserInputException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (choice != 5);
    }

    private static void showMenu() {
        System.out.println("\nMAIN MENU");
        System.out.println("---------");
        System.out.println("1. Enter / Modify Transport Data");
        System.out.println("2. Enter / Modify Electricity Data");
        System.out.println("3. Enter / Modify Diet Data");
        System.out.println("4. Calculate Carbon Footprint & Offset Cost");
        System.out.println("5. Exit");
    }

    private static void addTransport() throws InvalidUserInputException {
        System.out.println("\n-- Transport Data --");
        System.out.println("Vehicle type? (1 Petrol Car, 2 Diesel Car, 3 Motorbike, 4 Bus, 5 Train, 6 Flight): ");
        int type = getIntInput("");
        double km = getDoubleInput("Annual kilometres: ");
        sources.put("transport", new TransportEmission(type, km));
        System.out.println("Saved.");
    }

    private static void addElectricity() throws InvalidUserInputException {
        System.out.println("\n-- Electricity Data --");
        double kWh = getDoubleInput("Annual kWh consumption: ");
        sources.put("electricity", new ElectricityEmission(kWh));
        System.out.println("Saved.");
    }

    private static void addDiet() throws InvalidUserInputException {
        System.out.println("\n-- Diet Data --");
        System.out.println("Diet type? (1 Meat heavy, 2 Average, 3 Vegetarian, 4 Vegan): ");
        int diet = getIntInput("");
        sources.put("diet", new DietEmission(diet));
        System.out.println("Saved.");
    }

    private static void calculateFootprint() {
        if (sources.isEmpty()) {
            System.out.println("No data available. Please input at least one source.");
            return;
        }

        double total = 0;
        System.out.println("\n==== Annual Footprint ====");
        for (EmissionSource source : sources.values()) {
            double e = source.calculateEmission();
            total += e;
            System.out.println(source);
        }

        double tons = total / 1000;
        int cost = (int) Math.round(tons * 850);
        System.out.println("-------------------------------");
        System.out.printf("Total:         %.2f kg CO2  (≈ %.2f t)%n", total, tons);
        System.out.printf("\nApprox. offset cost @ Rs.850/t = Rs.%d%n", cost);
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                if (!prompt.isEmpty()) System.out.print(prompt);
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid decimal number. Try again.");
            }
        }
    }
}
