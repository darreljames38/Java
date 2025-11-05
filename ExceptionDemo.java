/**
 * EXCEPTION HANDLING DEMO
 * Analogy: The Road Detour
 * Handles an external, recoverable error (Division by Zero)
 */
class RoadTripCalculator {
    public static void calculateDetourRatio(int totalMiles, int detourCount) {
        System.out.println("\n--- Calculating Ratio: " + totalMiles + " miles / " + detourCount + " detours ---");
        try {
            // TRY: The risky operation where a road block might occur
            int milesPerDetour = totalMiles / detourCount;
            System.out.println("SUCCESS: Total miles divided by detours = " + milesPerDetour + " miles/detour.");
        } catch (ArithmeticException e) {
            // CATCH: The 'Detour Sign' handler for the specific problem
            System.out.println("FAILURE (Road Block!): Cannot divide by zero.");
            System.out.println("RECOVERY: Reporting a clean error message instead of crashing the map system.");
        } finally {
            // FINALLY: Code that ALWAYS runs (cleanup)
            System.out.println("FINISH: Map calculation attempt complete.");
        }
    }
}

public class ExceptionDemo {
    public static void main(String[] args) {
        // Scenario 1: Success (No Detour)
        RoadTripCalculator.calculateDetourRatio(200, 5);
        
        // Scenario 2: Failure (Detour/Exception)
        RoadTripCalculator.calculateDetourRatio(200, 0); 
    }
}
