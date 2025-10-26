import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

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
    private static Scanner sc = new Scanner(System.in);
    private static ArrayList<Borrower> borrowers = new ArrayList<>();
    private static ArrayList<Material> materials = new ArrayList<>();
    private static ArrayList<Transaction> transactions = new ArrayList<>();

    // Auto ID counters
    private static int borrowerIdCounter = 2025000;
    private static int materialIdCounter = 0;

    public static void main(String[] args) {
        loadCounters();
        loadBorrowers();
        loadMaterials();
        loadTransactions();

        int choice;
        do {
            System.out.println("\n=== FANTASTIC4 LIBRARY SYSTEM ===");
            System.out.println("[1] Borrowers Management");
            System.out.println("[2] Asset Management");
            System.out.println("[3] Borrow Material");
            System.out.println("[4] Return Material");
            System.out.println("[5] Borrower History");
            System.out.println("[6] Book History");
            System.out.println("[7] Exit");
            System.out.print("Choose an option: ");
            choice = getIntInput();

            switch (choice) {
                case 1 -> manageBorrowers();
                case 2 -> manageMaterials();
                case 3 -> borrowMaterial();
                case 4 -> returnMaterial();
                case 5 -> borrowerHistory();
                case 6 -> bookHistory();
                case 7 -> {
                    saveBorrowers();
                    saveMaterials();
                    saveTransactions();
                    saveCounters();
                    System.out.println("\nThank you for using Fantastic4 Library System!");
                    System.out.println("Group name: Fantastic4");
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        } while (choice != 7);
    }

    // ---------------- Borrowers Management ----------------
    private static void manageBorrowers() {
        int choice;
        do {
            System.out.println("\n--- Borrowers Management ---");
            System.out.println("[1] Add Borrower");
            System.out.println("[2] Edit Borrower");
            System.out.println("[3] Delete Borrower");
            System.out.println("[4] View Borrowers");
            System.out.println("[5] Back");
            System.out.print("Choose: ");
            choice = getIntInput();

            switch (choice) {
                case 1 -> addBorrower();
                case 2 -> editBorrower();
                case 3 -> deleteBorrower();
                case 4 -> viewBorrowers();
                case 5 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 5);
    }

    private static void addBorrower() {
        System.out.print("Enter first name: ");
        String first = sc.nextLine();
        System.out.print("Enter last name: ");
        String last = sc.nextLine();
        System.out.print("Enter age: ");
        int age = getIntInput();
        System.out.print("Enter email: ");
        String email = sc.nextLine();

        // basic input validation
        if (!Pattern.matches("^[A-Za-z ]+$", first) || !Pattern.matches("^[A-Za-z ]+$", last)) {
            System.out.println("Invalid name input!");
            return;
        }

        for (Borrower b : borrowers) {
            if (b.email.equalsIgnoreCase(email)) {
                System.out.println("Borrower with that email already exists!");
                return;
            }
        }

        Borrower borrower = new Borrower(first, last, age, email);
        borrowers.add(borrower);
        System.out.println("Borrower added successfully! ID: " + borrower.borrowerId);
    }

    private static void editBorrower() {
        System.out.print("Enter borrower ID to edit: ");
        int id = getIntInput();
        for (Borrower b : borrowers) {
            if (b.borrowerId == id) {
                System.out.print("Enter new email: ");
                b.email = sc.nextLine();
                System.out.println("Borrower info updated!");
                return;
            }
        }
        System.out.println("Borrower not found!");
    }

    private static void deleteBorrower() {
        System.out.print("Enter borrower ID to delete: ");
        int id = getIntInput();
        borrowers.removeIf(b -> b.borrowerId == id);
        System.out.println("Borrower deleted (if existed).");
    }

    private static void viewBorrowers() {
        if (borrowers.isEmpty()) {
            System.out.println("No borrowers registered.");
            return;
        }
        for (Borrower b : borrowers) {
            System.out.printf("ID: %d | %s %s | Age: %d | Email: %s | Violations: %d%n",
                    b.borrowerId, b.firstName, b.lastName, b.age, b.email, b.violations);
        }
    }

    // ---------------- Asset Management ----------------
    private static void manageMaterials() {
        int choice;
        do {
            System.out.println("\n--- Asset Management ---");
            System.out.println("[1] Add Material");
            System.out.println("[2] Edit Material");
            System.out.println("[3] Delete Material");
            System.out.println("[4] View Materials");
            System.out.println("[5] Back");
            System.out.print("Choose: ");
            choice = getIntInput();

            switch (choice) {
                case 1 -> addMaterial();
                case 2 -> editMaterial();
                case 3 -> deleteMaterial();
                case 4 -> viewMaterials();
                case 5 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 5);
    }

    private static void addMaterial() {
        System.out.print("Enter title: ");
        String title = sc.nextLine();
        System.out.print("Enter author: ");
        String author = sc.nextLine();
        System.out.print("Enter year published: ");
        int year = getIntInput();
        System.out.print("Enter publisher: ");
        String publisher = sc.nextLine();
        System.out.print("Enter number of copies: ");
        int copies = getIntInput();

        for (Material m : materials) {
            if (m.title.equalsIgnoreCase(title) && m.author.equalsIgnoreCase(author)) {
                System.out.println("Duplicate material not allowed!");
                return;
            }
        }

        Material material = new Material(title, author, year, publisher, copies);
        materials.add(material);
        System.out.println("Material added successfully! ID: " + material.materialId);
    }

    private static void editMaterial() {
        System.out.print("Enter material ID to edit: ");
        int id = getIntInput();
        for (Material m : materials) {
            if (m.materialId == id) {
                System.out.print("Enter new number of copies: ");
                m.copies = getIntInput();
                System.out.println("Material info updated!");
                return;
            }
        }
        System.out.println("Material not found!");
    }

    private static void deleteMaterial() {
        System.out.print("Enter material ID to delete: ");
        int id = getIntInput();
        materials.removeIf(m -> m.materialId == id);
        System.out.println("Material deleted (if existed).");
    }

    private static void viewMaterials() {
        if (materials.isEmpty()) {
            System.out.println("No materials in library.");
            return;
        }
        for (Material m : materials) {
            System.out.printf("ID: %d | Title: %s | Author: %s | Year: %d | Publisher: %s | Copies: %d%n",
                    m.materialId, m.title, m.author, m.yearPublished, m.publisher, m.copies);
        }
    }

    // ---------------- Borrow/Return ----------------
    private static void borrowMaterial() {
        System.out.print("Enter borrower ID: ");
        int bId = getIntInput();
        Borrower borrower = findBorrower(bId);
        if (borrower == null) {
            System.out.println("Borrower not found!");
            return;
        }
        if (borrower.violations >= 3) {
            System.out.println("Borrower has reached the violation limit!");
            return;
        }

        System.out.print("Enter material ID: ");
        int mId = getIntInput();
        Material material = findMaterial(mId);
        if (material == null) {
            System.out.println("Material not found!");
            return;
        }
        if (material.copies <= 0) {
            System.out.println("No copies available!");
            return;
        }

        material.copies--;
        transactions.add(new Transaction(bId, mId, LocalDate.now()));
        System.out.println("Material borrowed successfully!");
    }

    private static void returnMaterial() {
        System.out.print("Enter borrower ID: ");
        int bId = getIntInput();
        System.out.print("Enter material ID: ");
        int mId = getIntInput();

        for (Transaction t : transactions) {
            if (t.borrowerId == bId && t.materialId == mId && t.returnDate == null) {
                t.returnDate = LocalDate.now();

                Material material = findMaterial(mId);
                if (material != null) material.copies++;

                long daysBorrowed = Duration.between(t.borrowDate.atStartOfDay(), t.returnDate.atStartOfDay()).toDays();
                if (daysBorrowed > 7) {
                    Borrower b = findBorrower(bId);
                    if (b != null) b.violations++;
                    System.out.println("Late return! Violation added.");
                } else {
                    System.out.println("Material returned successfully!");
                }
                return;
            }
        }
        System.out.println("No active borrow record found.");
    }

    // ---------------- History ----------------
    private static void borrowerHistory() {
        System.out.print("Enter borrower ID: ");
        int id = getIntInput();
        for (Transaction t : transactions) {
            if (t.borrowerId == id) {
                System.out.printf("Material ID: %d | Borrowed: %s | Returned: %s%n",
                        t.materialId, t.borrowDate, t.returnDate == null ? "Not Returned" : t.returnDate);
            }
        }
    }

    private static void bookHistory() {
        System.out.print("Enter material ID: ");
        int id = getIntInput();
        for (Transaction t : transactions) {
            if (t.materialId == id) {
                System.out.printf("Borrower ID: %d | Borrowed: %s | Returned: %s%n",
                        t.borrowerId, t.borrowDate, t.returnDate == null ? "Not Returned" : t.returnDate);
            }
        }
    }

    // ---------------- Helper Methods ----------------
    private static Borrower findBorrower(int id) {
        for (Borrower b : borrowers) if (b.borrowerId == id) return b;
        return null;
    }

    private static Material findMaterial(int id) {
        for (Material m : materials) if (m.materialId == id) return m;
        return null;
    }

    private static int getIntInput() {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid input, enter a number!");
            return -1;
        }
    }

    // ---------------- File Handling ----------------
    private static void saveBorrowers() {
        try (PrintWriter pw = new PrintWriter("borrowers.txt")) {
            for (Borrower b : borrowers) {
                pw.printf("%d,%s,%s,%d,%s,%d%n", b.borrowerId, b.firstName, b.lastName, b.age, b.email, b.violations);
            }
        } catch (IOException e) {
            System.out.println("Error saving borrowers!");
        }
    }

    private static void loadBorrowers() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("borrowers.txt"));
            for (String line : lines) {
                String[] p = line.split(",");
                Borrower b = new Borrower(Integer.parseInt(p[0]), p[1], p[2], Integer.parseInt(p[3]), p[4], Integer.parseInt(p[5]));
                borrowers.add(b);
            }
        } catch (IOException ignored) {}
    }

    private static void saveMaterials() {
        try (PrintWriter pw = new PrintWriter("materials.txt")) {
            for (Material m : materials) {
                pw.printf("%d,%s,%s,%d,%s,%d%n", m.materialId, m.title, m.author, m.yearPublished, m.publisher, m.copies);
            }
        } catch (IOException e) {
            System.out.println("Error saving materials!");
        }
    }

    private static void loadMaterials() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("materials.txt"));
            for (String line : lines) {
                String[] p = line.split(",");
                Material m = new Material(Integer.parseInt(p[0]), p[1], p[2], Integer.parseInt(p[3]), p[4], Integer.parseInt(p[5]));
                materials.add(m);
            }
        } catch (IOException ignored) {}
    }

    private static void saveTransactions() {
        try (PrintWriter pw = new PrintWriter("transactions.txt")) {
            for (Transaction t : transactions) {
                pw.printf("%d,%d,%s,%s%n", t.borrowerId, t.materialId, t.borrowDate,
                        t.returnDate == null ? "null" : t.returnDate);
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions!");
        }
    }

    private static void loadTransactions() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("transactions.txt"));
            for (String line : lines) {
                String[] p = line.split(",");
                LocalDate borrowDate = LocalDate.parse(p[2]);
                LocalDate returnDate = p[3].equals("null") ? null : LocalDate.parse(p[3]);
                Transaction t = new Transaction(Integer.parseInt(p[0]), Integer.parseInt(p[1]), borrowDate, returnDate);
                transactions.add(t);
            }
        } catch (IOException ignored) {}
    }

    private static void saveCounters() {
        try (PrintWriter pw = new PrintWriter("idCounters.txt")) {
            pw.println(borrowerIdCounter);
            pw.println(materialIdCounter);
        } catch (IOException e) {
            System.out.println("Error saving counters!");
        }
    }

    private static void loadCounters() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("idCounters.txt"));
            borrowerIdCounter = Integer.parseInt(lines.get(0));
            materialIdCounter = Integer.parseInt(lines.get(1));
        } catch (IOException ignored) {}
    }

    // ---------------- Classes ----------------
    static class Borrower {
        int borrowerId;
        String firstName;
        String lastName;
        int age;
        String email;
        int violations;

        Borrower(String first, String last, int age, String email) {
            this.borrowerId = borrowerIdCounter++;
            this.firstName = first;
            this.lastName = last;
            this.age = age;
            this.email = email;
            this.violations = 0;
        }

        Borrower(int id, String first, String last, int age, String email, int violations) {
            this.borrowerId = id;
            this.firstName = first;
            this.lastName = last;
            this.age = age;
            this.email = email;
            this.violations = violations;
        }
    }

    static class Material {
        int materialId;
        String title;
        String author;
        int yearPublished;
        String publisher;
        int copies;

        Material(String title, String author, int yearPublished, String publisher, int copies) {
            this.materialId = materialIdCounter++;
            this.title = title;
            this.author = author;
            this.yearPublished = yearPublished;
            this.publisher = publisher;
            this.copies = copies;
        }

        Material(int id, String title, String author, int yearPublished, String publisher, int copies) {
            this.materialId = id;
            this.title = title;
            this.author = author;
            this.yearPublished = yearPublished;
            this.publisher = publisher;
            this.copies = copies;
        }
    }

    static class Transaction {
        int borrowerId;
        int materialId;
        LocalDate borrowDate;
        LocalDate returnDate;

        Transaction(int borrowerId, int materialId, LocalDate borrowDate) {
            this.borrowerId = borrowerId;
            this.materialId = materialId;
            this.borrowDate = borrowDate;
            this.returnDate = null;
        }

        Transaction(int borrowerId, int materialId, LocalDate borrowDate, LocalDate returnDate) {
            this.borrowerId = borrowerId;
            this.materialId = materialId;
            this.borrowDate = borrowDate;
            this.returnDate = returnDate;
        }
    }
}
