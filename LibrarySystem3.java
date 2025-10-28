import java.util.*;
import java.io.*;
import java.time.*;
import java.nio.file.*;

/**
 * LibrarySystem.java
 *
 * Console-based Library System implementing the given project specification.
 *
 * Usage:
 *  javac LibrarySystem.java
 *  java LibrarySystem
 *
 * Data files:
 *  - borrowers.txt
 *  - materials.txt
 *  - transactions.txt
 *
 * Group name: Fantastic4
 */

public class LibrarySystem {
    private static ArrayList<Borrower> borrowers = new ArrayList<>();
    private static ArrayList<Material> materials = new ArrayList<>();
    private static ArrayList<Transaction> transactions = new ArrayList<>();
    private static int borrowerIdCounter = 2025000;
    private static int materialIdCounter = 0;
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadFiles();
        while (true) {
            System.out.println("\n====== FANTASTIC4 LIBRARY SYSTEM ======");
            System.out.println("1. Borrowers Management");
            System.out.println("2. Asset Management");
            System.out.println("3. Borrow");
            System.out.println("4. Return");
            System.out.println("5. Borrower History");
            System.out.println("6. Book History");
            System.out.println("7. Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> manageBorrowers();
                case "2" -> manageAssets();
                case "3" -> borrowMaterial();
                case "4" -> returnMaterial();
                case "5" -> showBorrowerHistory();
                case "6" -> showBookHistory();
                case "7" -> exitSystem();
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // ====================== BORROWER MANAGEMENT ======================
    private static void manageBorrowers() {
        while (true) {
            System.out.println("\n--- Borrowers Management ---");
            System.out.println("1. Add Borrower");
            System.out.println("2. Edit Borrower Information");
            System.out.println("3. Delete Borrower");
            System.out.println("4. View Borrowers");
            System.out.println("5. Back");
            System.out.print("Choose: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> addBorrower();
                case "2" -> editBorrower();
                case "3" -> deleteBorrower();
                case "4" -> viewBorrowers();
                case "5" -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void addBorrower() {
        try {
            System.out.print("Enter first name: ");
            String first = sc.nextLine();
            if (!first.matches("[A-Za-z]+")) {
                System.out.println("Invalid first name.");
                return;
            }

            System.out.print("Enter last name: ");
            String last = sc.nextLine();
            if (!last.matches("[A-Za-z]+")) {
                System.out.println("Invalid last name.");
                return;
            }

            System.out.print("Enter age: ");
            int age = Integer.parseInt(sc.nextLine());

            System.out.print("Enter email: ");
            String email = sc.nextLine();
            if (!email.contains("@") || !email.contains(".")) {
                System.out.println("Invalid email format.");
                return;
            }

            for (Borrower b : borrowers) {
                if (b.email.equalsIgnoreCase(email)) {
                    System.out.println("Borrower already registered!");
                    return;
                }
            }

            Borrower borrower = new Borrower(borrowerIdCounter++, first, last, age, email);
            borrowers.add(borrower);
            saveBorrowers();
            System.out.println("Borrower added successfully!");

        } catch (Exception e) {
            System.out.println("Error adding borrower: " + e.getMessage());
        }
    }

    private static void editBorrower() {
        System.out.print("Enter borrower ID to edit: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            for (Borrower b : borrowers) {
                if (b.borrowerId == id) {
                    
                    System.out.print("Enter new first name: ");
                    b.firstName = sc.nextLine();
                    
                    System.out.print("Enter new last name: ");
                    b.lastName = sc.nextLine();
                    
                    System.out.print("Enter new email: ");
                    String email = sc.nextLine();
                    if (!email.contains("@") || !email.contains(".")) {
                        System.out.println("Invalid email format.");
                        return;
                    }
                    b.email = email;
                    saveBorrowers();
                    System.out.println("Email updated successfully!");
                    return;
                }
            }
            System.out.println("Borrower not found.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private static void deleteBorrower() {
        System.out.print("Enter borrower ID to delete: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            borrowers.removeIf(b -> b.borrowerId == id);
            saveBorrowers();
            System.out.println("Borrower deleted successfully!");
        } catch (Exception e) {
            System.out.println("Error deleting borrower.");
        }
    }

    private static void viewBorrowers() {
        if (borrowers.isEmpty()) {
            System.out.println("No borrowers found.");
            return;
        }
        System.out.println("\n--- List of Borrowers ---");
        for (Borrower b : borrowers) {
            System.out.println(b);
        }
    }

    // ====================== ASSET MANAGEMENT ======================
    private static void manageAssets() {
        while (true) {
            System.out.println("\n--- Asset Management ---");
            System.out.println("1. Add Material");
            System.out.println("2. Edit Material");
            System.out.println("3. Delete Material");
            System.out.println("4. View Materials");
            System.out.println("5. Back");
            System.out.print("Choose: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> addMaterial();
                case "2" -> editMaterial();
                case "3" -> deleteMaterial();
                case "4" -> viewMaterials();
                case "5" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void addMaterial() {
        try {
            System.out.print("Enter material type (Book/Journal/Magazine/Thesis): ");
            String type = sc.nextLine();
            System.out.print("Enter title/name: ");
            String title = sc.nextLine();
            System.out.print("Enter author/publisher: ");
            String author = sc.nextLine();
            System.out.print("Enter year published: ");
            int year = Integer.parseInt(sc.nextLine());
            System.out.print("Enter total copies: ");
            int copies = Integer.parseInt(sc.nextLine());

            for (Material m : materials) {
                if (m.title.equalsIgnoreCase(title)) {
                    System.out.println("Duplicate material not allowed!");
                    return;
                }
            }

            Material mat = new Material(materialIdCounter++, type, title, author, year, copies);
            materials.add(mat);
            saveMaterials();
            System.out.println("Material added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding material.");
        }
    }

    private static void editMaterial() {
        System.out.print("Enter material ID to edit: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            for (Material m : materials) {
                if (m.materialId == id) {
                    System.out.print("Enter new number of copies: ");
                    m.totalCopies = Integer.parseInt(sc.nextLine());
                    saveMaterials();
                    System.out.println("Material updated successfully!");
                    return;
                }
            }
            System.out.println("Material not found.");
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    private static void deleteMaterial() {
        System.out.print("Enter material ID to delete: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            materials.removeIf(m -> m.materialId == id);
            saveMaterials();
            System.out.println("Material deleted successfully!");
        } catch (Exception e) {
            System.out.println("Error deleting material.");
        }
    }

    private static void viewMaterials() {
        if (materials.isEmpty()) {
            System.out.println("No materials found.");
            return;
        }
        System.out.println("\n--- Library Materials ---");
        for (Material m : materials) {
            System.out.println(m);
        }
    }

    // ====================== BORROW & RETURN ======================
    private static void borrowMaterial() {
        try {
            System.out.print("Enter borrower ID: ");
            int bid = Integer.parseInt(sc.nextLine());
            Borrower borrower = findBorrower(bid);
            if (borrower == null) {
                System.out.println("Borrower not found.");
                return;
            }
            if (borrower.violations >= 3) {
                System.out.println("Borrower has 3 strikes. Cannot borrow.");
                return;
            }

            System.out.print("Enter material ID: ");
            int mid = Integer.parseInt(sc.nextLine());
            Material material = findMaterial(mid);
            if (material == null || material.totalCopies <= 0) {
                System.out.println("Material not available.");
                return;
            }

            LocalDate borrowDate = LocalDate.now();
            LocalDate dueDate = borrowDate.plusDays(getReturnDays(material.type));
            material.totalCopies--;
            transactions.add(new Transaction(bid, mid, borrowDate, dueDate));
            saveMaterials();
            saveTransactions();
            System.out.println("Material borrowed successfully!");
        } catch (Exception e) {
            System.out.println("Error borrowing material.");
        }
    }

    private static void returnMaterial() {
        try {
            System.out.print("Enter borrower ID: ");
            int bid = Integer.parseInt(sc.nextLine());
            for (Transaction t : transactions) {
                if (t.borrowerId == bid && !t.returned) {
                    Material material = findMaterial(t.materialId);
                    LocalDate now = LocalDate.now();
                    t.returned = true;
                    material.totalCopies++;
                    if (now.isAfter(t.dueDate)) {
                        Borrower b = findBorrower(bid);
                        b.violations++;
                        System.out.println("Late return! Violation recorded.");
                    }
                    saveMaterials();
                    saveTransactions();
                    saveBorrowers();
                    System.out.println("Material returned successfully!");
                    return;
                }
            }
            System.out.println("No active borrow found for this borrower.");
        } catch (Exception e) {
            System.out.println("Error returning material.");
        }
    }

    private static int getReturnDays(String type) {
        return switch (type.toLowerCase()) {
            case "book" -> 7;
            case "journal" -> 3;
            case "magazine" -> 0;
            case "thesis" -> 2;
            default -> 7;
        };
    }

    // ====================== HISTORY ======================
    private static void showBorrowerHistory() {
        System.out.print("Enter borrower ID: ");
        int bid = Integer.parseInt(sc.nextLine());
        System.out.println("\n--- Borrower History ---");
        for (Transaction t : transactions) {
            if (t.borrowerId == bid)
                System.out.println(t);
        }
    }

    private static void showBookHistory() {
        System.out.print("Enter material ID: ");
        int mid = Integer.parseInt(sc.nextLine());
        System.out.println("\n--- Book History ---");
        for (Transaction t : transactions) {
            if (t.materialId == mid)
                System.out.println(t);
        }
    }

    // ====================== FILE HANDLING ======================
    private static void loadFiles() {
        try {
            Path borrowerPath = Paths.get("borrowers.txt");
            Path materialPath = Paths.get("materials.txt");
            Path transPath = Paths.get("transactions.txt");

            if (!Files.exists(borrowerPath)) Files.createFile(borrowerPath);
            if (!Files.exists(materialPath)) Files.createFile(materialPath);
            if (!Files.exists(transPath)) Files.createFile(transPath);

            // Load Borrowers
            for (String line : Files.readAllLines(borrowerPath)) {
                String[] p = line.split(",");
                borrowers.add(new Borrower(Integer.parseInt(p[0]), p[1], p[2], Integer.parseInt(p[3]), p[4], Integer.parseInt(p[5])));
                borrowerIdCounter = Math.max(borrowerIdCounter, Integer.parseInt(p[0]) + 1);
            }

            // Load Materials
            for (String line : Files.readAllLines(materialPath)) {
                String[] p = line.split(",");
                materials.add(new Material(Integer.parseInt(p[0]), p[1], p[2], p[3], Integer.parseInt(p[4]), Integer.parseInt(p[5])));
                materialIdCounter = Math.max(materialIdCounter, Integer.parseInt(p[0]) + 1);
            }

            // Load Transactions
            for (String line : Files.readAllLines(transPath)) {
                String[] p = line.split(",");
                transactions.add(new Transaction(Integer.parseInt(p[0]), Integer.parseInt(p[1]), LocalDate.parse(p[2]), LocalDate.parse(p[3]), Boolean.parseBoolean(p[4])));
            }

        } catch (Exception e) {
            System.out.println("Error loading files: " + e.getMessage());
        }
    }

    private static void saveBorrowers() {
        try (PrintWriter pw = new PrintWriter("borrowers.txt")) {
            for (Borrower b : borrowers)
                pw.println(b.borrowerId + "," + b.firstName + "," + b.lastName + "," + b.age + "," + b.email + "," + b.violations);
        } catch (Exception e) {
            System.out.println("Error saving borrowers.");
        }
    }

    private static void saveMaterials() {
        try (PrintWriter pw = new PrintWriter("materials.txt")) {
            for (Material m : materials)
                pw.println(m.materialId + "," + m.type + "," + m.title + "," + m.author + "," + m.year + "," + m.totalCopies);
        } catch (Exception e) {
            System.out.println("Error saving materials.");
        }
    }

    private static void saveTransactions() {
        try (PrintWriter pw = new PrintWriter("transactions.txt")) {
            for (Transaction t : transactions)
                pw.println(t.borrowerId + "," + t.materialId + "," + t.borrowDate + "," + t.dueDate + "," + t.returned);
        } catch (Exception e) {
            System.out.println("Error saving transactions.");
        }
    }

    private static Borrower findBorrower(int id) {
        for (Borrower b : borrowers) if (b.borrowerId == id) return b;
        return null;
    }

    private static Material findMaterial(int id) {
        for (Material m : materials) if (m.materialId == id) return m;
        return null;
    }

    private static void exitSystem() {
        System.out.println("\nThank you for using the Fantastic4 Library System!");
        System.exit(0);
    }
}

// ====================== CLASSES ======================
class Borrower {
    int borrowerId;
    String firstName, lastName, email;
    int age, violations = 0;

    Borrower(int id, String f, String l, int a, String e) {
        borrowerId = id; firstName = f; lastName = l; age = a; email = e;
    }

    Borrower(int id, String f, String l, int a, String e, int v) {
        borrowerId = id; firstName = f; lastName = l; age = a; email = e; violations = v;
    }

    public String toString() {
        return borrowerId + " | " + firstName + " " + lastName + " | Age: " + age + " | Email: " + email + " | Violations: " + violations;
    }
}

class Material {
    int materialId, year, totalCopies;
    String type, title, author;

    Material(int id, String t, String ti, String a, int y, int c) {
        materialId = id; type = t; title = ti; author = a; year = y; totalCopies = c;
    }

    public String toString() {
        return materialId + " | " + type + " | " + title + " | " + author + " | Year: " + year + " | Copies: " + totalCopies;
    }
}

class Transaction {
    int borrowerId, materialId;
    LocalDate borrowDate, dueDate;
    boolean returned;

    Transaction(int bid, int mid, LocalDate b, LocalDate d) {
        borrowerId = bid; materialId = mid; borrowDate = b; dueDate = d; returned = false;
    }

    Transaction(int bid, int mid, LocalDate b, LocalDate d, boolean r) {
        borrowerId = bid; materialId = mid; borrowDate = b; dueDate = d; returned = r;
    }

    public String toString() {
        return "Borrower ID: " + borrowerId + " | Material ID: " + materialId +
               " | Borrowed: " + borrowDate + " | Due: " + dueDate + " | Returned: " + returned;
    }
}
