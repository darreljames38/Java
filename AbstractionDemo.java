/**
 * ABSTRACTION DEMO (Hiding Complexity)
 * Abstract Class Analogy: Vehicle (Partial Blueprint)
 * Interface Analogy: Chargeable (Legal Contract)
 */

// INTERFACE: The Contract (The rules, no code)
interface Chargeable {
    void plugIn(); // Every class that signs the contract MUST implement this.
}

// ABSTRACT CLASS: The Partial Blueprint (Cannot be instantiated)
abstract class Vehicle implements Chargeable {

    // 1. Finished Code: All vehicles share this
    public void activateLights() {
        System.out.println("-> Vehicle: Standard running lights are now on (Pre-built part).");
    }

    // 2. Missing Code (Abstract Method): Forces children to provide the code
    abstract void paintColor(String color);
}

// CONCRETE CHILD CLASS: The Completed Blueprint (Can be instantiated)
class Sedan extends Vehicle {
    private String carColor = "Primer";
     
    // Fulfilling the Abstract Class Requirement (Completing the paintColor step)
    @Override
    void paintColor(String color) {
        this.carColor = color;
        System.out.println("-> Sedan: Completed blueprint! Car is painted " + carColor + ".");
    }

    // Fulfilling the Interface Contract (Implementing plugIn)
    @Override
    public void plugIn() {
        System.out.println("-> Sedan: Battery indicator is charging (Contract fulfilled).");
    }
}

public class AbstractionDemo {
    public static void main(String[] args) {
        // Vehicle myVehicle = new Vehicle(); // <-- THIS IS FORBIDDEN (Cannot instantiate abstract class)
        
        Sedan myCar = new Sedan(); // We can only instantiate the completed blueprint.

        // Abstraction in action: Using the implemented methods
        myCar.paintColor("Ocean Blue");
        myCar.plugIn();

        // Using the common, pre-built method
        myCar.activateLights();
    }
}
