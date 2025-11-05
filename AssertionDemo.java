/**
 * ASSERTION DEMO
 * Analogy: The Chef's Recipe Check (Internal logic validation)
 * Note: Must be run with 'java -ea AssertionDemo' to enable assertions!
 */
class RecipeChecker {
    public static void checkPlateCount(int actualItems) {
        int expectedItems = 5; // The core assumption of our recipe logic

        System.out.println("Checking Recipe: Expected " + expectedItems + " items, found " + actualItems + ".");
        
        // ASSERTION: A check that this condition MUST be true based on my design.
        // If it fails, I know I made a mistake somewhere in the preparation logic.
        assert (actualItems == expectedItems) :
            "INTERNAL BUG: Recipe Check Failed! Expected 5 items, found " + actualItems;

        System.out.println("PASS: Chef's internal logic is correct. Dish is ready to go.");
    }
}

public class AssertionDemo {
    public static void main(String[] args) {
        // Scenario 1: The assumption (recipe logic) is TRUE.
        System.out.println("--- Scenario 1: Correct Plate Count ---");
        RecipeChecker.checkPlateCount(5); 

        // Scenario 2: The assumption is FALSE (This should crash the program to signal the bug!)
        System.out.println("\n--- Scenario 2: Incorrect Plate Count (Expect Crash if -ea is used) ---");
        RecipeChecker.checkPlateCount(4); 
    }
}
