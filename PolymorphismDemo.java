/**
 * POLYMORPHISM DEMO (Many Forms)
 * Overriding Analogy: Restaurant ServeFood (Child changes parent behavior)
 * Overloading Analogy: Chef Cut (Same name, different inputs)
 */
class Restaurant {
    // Parent's default behavior
    public void serveFood(String dish) {
        System.out.println("-> Generic Restaurant: Placing the " + dish + " carefully on the table (Generic Service).");
    }
}

class FastFood extends Restaurant {
    // METHOD OVERRIDING: Same method name, same input, but different behavior (The Smart Remote changes the TV channel)
    @Override
    public void serveFood(String dish) {
        System.out.println("-> Fast Food: Sliding the wrapped " + dish + " across the counter (Quick Service).");
    }
}

class Chef {
    // METHOD OVERLOADING: Same method name, but different inputs (The Chef's Knife cuts different things)
    
    // Overload 1: Cuts a single string item.
    public void cut(String item) {
        System.out.println("-> Chef's Knife: Using precision slice on one " + item + ".");
    }

    // Overload 2: Cuts two string items.
    public void cut(String item1, String item2) {
        System.out.println("-> Chef's Knife: Using quick chop method for " + item1 + " and " + item2 + ".");
    }

    // Overload 3: Cuts an integer amount (e.g., number of servings).
    public void cut(int amount) {
        System.out.println("-> Chef's Knife: Preparing a large batch for " + amount + " servings.");
    }
}

public class PolymorphismDemo {
    public static void main(String[] args) {
        // --- Overriding Test ---
        Restaurant genericService = new Restaurant();
        FastFood quickService = new FastFood();
        
        System.out.println("--- Overriding (Serving) ---");
        genericService.serveFood("Pasta"); // Calls Parent's method
        quickService.serveFood("Burger");  // Calls Child's (Overridden) method

        // --- Overloading Test ---
        Chef masterChef = new Chef();
        
        System.out.println("\n--- Overloading (Cutting) ---");
        masterChef.cut("Watermelon");         // Calls method with 1 String
        masterChef.cut("Carrot", "Celery");   // Calls method with 2 Strings
        masterChef.cut(50);                   // Calls method with 1 Int
    }
}
