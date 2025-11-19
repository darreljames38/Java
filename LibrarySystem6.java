import java.nio.file.*;
import java.util.*;
import java.io.*;
import java.time.*;

/**
 * LibrarySystem.java
 * Console-based Library System.
 *
 * Group: Fantastic4
 */


public class LibrarySystem {
    private static ArrayList<Borrower> borrowers = new ArrayList<>();
    private static ArrayList<Material> materials = new ArrayList<>();
    private static ArrayList<Transaction> transactions = new ArrayList<>();
    private static int borrowerIdCounter = 2025000;
    private static int materialIdCounter = 0;
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadFiles(); // Load existing data from files
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

    // Adds a new borrower
    private static void addBorrower() {
        try {
            System.out.print("Enter first name: ");
            String first = sc.nextLine();
            if (!first.matches("[A-Za-z]+")) { System.out.println("Invalid first name."); return; }

            System.out.print("Enter middle name (optional): ");
            String middle = sc.nextLine();
            if (!middle.matches("[A-Za-z]*")) { System.out.println("Invalid middle name."); return; }

            System.out.print("Enter last name: ");
            String last = sc.nextLine();
            if (!last.matches("[A-Za-z]+")) { System.out.println("Invalid last name."); return; }

            System.out.print("Enter age: ");
            int age = Integer.parseInt(sc.nextLine());

            System.out.print("Enter gender (M/F): ");
            String gender = sc.nextLine();
            if (!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F")) { System.out.println("Invalid gender."); return; }

            System.out.print("Enter birthday (YYYY-MM-DD): ");
            String birthday = sc.nextLine();

            System.out.print("Enter contact number: ");
            String contact = sc.nextLine();

            System.out.print("Enter email: ");
            String email = sc.nextLine();
            if (!email.contains("@") || !email.contains(".")) { System.out.println("Invalid email format."); return; }

            System.out.print("Enter address: ");
            String address = sc.nextLine();

            for (Borrower b : borrowers) {
                if (b.getEmail().equalsIgnoreCase(email)) {
                    System.out.println("Borrower already registered!");
                    return;
                }
            }

            Borrower borrower = new Borrower(borrowerIdCounter++, first, middle, last, age, gender, birthday, contact, email, address);
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
                if (b.getBorrowerId() == id) {
                    System.out.print("Enter new first name: "); b.setFirstName(sc.nextLine());
                    System.out.print("Enter new middle name: "); b.setMiddleName(sc.nextLine());
                    System.out.print("Enter new last name: "); b.setLastName(sc.nextLine());
                    System.out.print("Enter new gender (M/F): "); b.setGender(sc.nextLine());
                    System.out.print("Enter new birthday (YYYY-MM-DD): "); b.setBirthday(sc.nextLine());
                    System.out.print("Enter new contact number: "); b.setContactNumber(sc.nextLine());
                    System.out.print("Enter new email: "); b.setEmail(sc.nextLine());
                    System.out.print("Enter new address: "); b.setAddress(sc.nextLine());
                    saveBorrowers();
                    System.out.println("Borrower info updated!");
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
            borrowers.removeIf(b -> b.getBorrowerId() == id);
            saveBorrowers();
            System.out.println("Borrower deleted successfully!");
        } catch (Exception e) {
            System.out.println("Error deleting borrower.");
        }
    }

    private static void viewBorrowers() {
        if (borrowers.isEmpty()) { System.out.println("No borrowers found."); return; }
        System.out.println("\n--- List of Borrowers ---");
        for (Borrower b : borrowers) System.out.println(b);
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
        System.out.println("Choose type: 1.Book 2.Journal 3.Magazine 4.Thesis Book");
        String type = sc.nextLine();
        System.out.print("Enter title/name: ");
        String title = sc.nextLine();
        System.out.print("Enter author/publisher: ");
        String author = sc.nextLine();
        System.out.print("Enter year published: ");
        int year = Integer.parseInt(sc.nextLine());
        System.out.print("Enter total copies: ");
        int copies = Integer.parseInt(sc.nextLine());

        Material m = switch (type) {
            case "1" -> new Book(materialIdCounter++, title, author, year, copies);
            case "2" -> new Journal(materialIdCounter++, title, author, year, copies);
            case "3" -> new Magazine(materialIdCounter++, title, author, year, copies);
            case "4" -> new ThesisBook(materialIdCounter++, title, author, year, copies);
            default -> null;
        };

        if (m != null) {
            materials.add(m);
            saveMaterials();
            System.out.println("Material added!");
        } else System.out.println("Invalid type.");
    }

    private static void editMaterial() {
        System.out.print("Enter material ID to edit: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            for (Material m : materials) {
                if (m.getMaterialId() == id) {
                    System.out.print("Enter new title/name: "); m.setTitle(sc.nextLine());
                    System.out.print("Enter new author/publisher: "); m.setAuthor(sc.nextLine());
                    System.out.print("Enter new year published: "); m.setYearPublished(Integer.parseInt(sc.nextLine()));
                    System.out.print("Enter new total copies: "); m.setTotalCopies(Integer.parseInt(sc.nextLine()));
                    saveMaterials();
                    System.out.println("Material updated!");
                    return;
                }
            }
            System.out.println("Material not found.");
        } catch (Exception e) { System.out.println("Invalid input."); }
    }

    private static void deleteMaterial() {
        System.out.print("Enter material ID to delete: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            materials.removeIf(m -> m.getMaterialId() == id);
            saveMaterials();
            System.out.println("Material deleted.");
        } catch (Exception e) { System.out.println("Error deleting material."); }
    }

    private static void viewMaterials() {
        if (materials.isEmpty()) { System.out.println("No materials found."); return; }
        System.out.println("\n--- Library Materials ---");
        for (Material m : materials) System.out.println(m);
    }

    // ====================== BORROW & RETURN ======================
    private static void borrowMaterial() {
        try {
            System.out.print("Enter borrower ID: "); int bid = Integer.parseInt(sc.nextLine());
            Borrower borrower = findBorrower(bid);
            if (borrower == null) { System.out.println("Borrower not found."); return; }
            if (borrower.getViolations() >= 3) { System.out.println("Borrower has 3 strikes."); return; }

            for (Transaction t : transactions) {
                if (t.getBorrowerId() == bid && !t.isReturned()) {
                    System.out.println("Borrower already has a borrowed material."); return;
                }
            }

            System.out.print("Enter material ID: "); int mid = Integer.parseInt(sc.nextLine());
            Material material = findMaterial(mid);
            if (material == null || material.getTotalCopies() <= 0) { System.out.println("Material unavailable."); return; }

            LocalDate dueDate = LocalDate.now().plusDays(material.getReturnDays());
            material.setTotalCopies(material.getTotalCopies() - 1);
            transactions.add(new Transaction(bid, mid, LocalDate.now(), dueDate));
            saveMaterials();
            saveTransactions();
            System.out.println("Borrowed! Due date: " + dueDate);

        } catch (Exception e) { System.out.println("Error during borrow: " + e.getMessage()); }
    }

    private static void returnMaterial() {
        System.out.print("Enter borrower ID: ");
        try {
            int bid = Integer.parseInt(sc.nextLine());
            for (Transaction t : transactions) {
                if (t.getBorrowerId() == bid && !t.isReturned()) {
                    Material m = findMaterial(t.getMaterialId());
                    m.setTotalCopies(m.getTotalCopies() + 1);
                    if (LocalDate.now().isAfter(t.getDueDate())) {
                        findBorrower(bid).setViolations(findBorrower(bid).getViolations() + 1);
                        System.out.println("Late return! Strike added.");
                    }
                    t.setReturned(true);
                    saveMaterials();
                    saveTransactions();
                    saveBorrowers();
                    System.out.println("Material returned successfully.");
                    return;
                }
            }
            System.out.println("No borrowed material found for this borrower.");
        } catch (Exception e) { System.out.println("Error during return."); }
    }

    // ====================== HISTORY ======================
    private static void showBorrowerHistory() {
        System.out.print("Enter borrower ID: ");
        int bid = Integer.parseInt(sc.nextLine());
        for (Transaction t : transactions) {
            if (t.getBorrowerId() == bid) {
                Material m = findMaterial(t.getMaterialId());
                System.out.println(m.getTitle() + " | Borrowed: " + t.getBorrowDate() + " | Due: " + t.getDueDate() + " | Returned: " + t.isReturned());
            }
        }
    }

    private static void showBookHistory() {
        System.out.print("Enter material ID: ");
        int mid = Integer.parseInt(sc.nextLine());
        for (Transaction t : transactions) {
            if (t.getMaterialId() == mid) {
                Borrower b = findBorrower(t.getBorrowerId());
                System.out.println(b.getFirstName() + " " + b.getLastName() + " | Borrowed: " + t.getBorrowDate() + " | Returned: " + t.isReturned());
            }
        }
    }

    // ====================== FILE I/O ======================
    private static void saveBorrowers() {
        try (PrintWriter pw = new PrintWriter("borrowers.txt")) {
            for (Borrower b : borrowers) {
                pw.println(b.getBorrowerId() + "," + b.getFirstName() + "," + b.getMiddleName() + "," +
                        b.getLastName() + "," + b.getAge() + "," + b.getGender() + "," + b.getBirthday() +
                        "," + b.getContactNumber() + "," + b.getEmail() + "," + b.getAddress() + "," + b.getViolations());
            }
        } catch (Exception e) { System.out.println("Error saving borrowers: " + e.getMessage()); }
    }

    private static void saveMaterials() {
        try (PrintWriter pw = new PrintWriter("materials.txt")) {
            for (Material m : materials) {
                pw.println(m.getMaterialId() + "," + m.getType() + "," + m.getTitle() + "," + m.getAuthor() + "," + m.getYearPublished() + "," + m.getTotalCopies());
            }
        } catch (Exception e) { System.out.println("Error saving materials: " + e.getMessage()); }
    }

    private static void saveTransactions() {
        try (PrintWriter pw = new PrintWriter("transactions.txt")) {
            for (Transaction t : transactions) {
                pw.println(t.getBorrowerId() + "," + t.getMaterialId() + "," + t.getBorrowDate() + "," + t.getDueDate() + "," + t.isReturned());
            }
        } catch (Exception e) { System.out.println("Error saving transactions: " + e.getMessage()); }
    }

    private static void loadFiles() {
        // Load borrowers
        try {
            File f = new File("borrowers.txt");
            if (f.exists()) {
                Scanner s = new Scanner(f);
                while (s.hasNextLine()) {
                    String[] data = s.nextLine().split(",");
                    borrowers.add(new Borrower(Integer.parseInt(data[0]), data[1], data[2], data[3],
                            Integer.parseInt(data[4]), data[5], data[6], data[7], data[8], data[9],
                            Integer.parseInt(data[10])));
                    borrowerIdCounter = Math.max(borrowerIdCounter, Integer.parseInt(data[0]) + 1);
                }
                s.close();
            }
        } catch (Exception e) { System.out.println("Error loading borrowers: " + e.getMessage()); }

        // Load materials
        try {
            File f = new File("materials.txt");
            if (f.exists()) {
                Scanner s = new Scanner(f);
                while (s.hasNextLine()) {
                    String[] data = s.nextLine().split(",");
                    Material m = switch (data[1]) {
                        case "Book" -> new Book(Integer.parseInt(data[0]), data[2], data[3], Integer.parseInt(data[4]), Integer.parseInt(data[5]));
                        case "Journal" -> new Journal(Integer.parseInt(data[0]), data[2], data[3], Integer.parseInt(data[4]), Integer.parseInt(data[5]));
                        case "Magazine" -> new Magazine(Integer.parseInt(data[0]), data[2], data[3], Integer.parseInt(data[4]), Integer.parseInt(data[5]));
                        case "ThesisBook" -> new ThesisBook(Integer.parseInt(data[0]), data[2], data[3], Integer.parseInt(data[4]), Integer.parseInt(data[5]));
                        default -> null;
                    };
                    if (m != null) materials.add(m);
                    materialIdCounter = Math.max(materialIdCounter, Integer.parseInt(data[0]) + 1);
                }
                s.close();
            }
        } catch (Exception e) { System.out.println("Error loading materials: " + e.getMessage()); }

        // Load transactions
        try {
            File f = new File("transactions.txt");
            if (f.exists()) {
                Scanner s = new Scanner(f);
                while (s.hasNextLine()) {
                    String[] data = s.nextLine().split(",");
                    transactions.add(new Transaction(Integer.parseInt(data[0]), Integer.parseInt(data[1]),
                            LocalDate.parse(data[2]), LocalDate.parse(data[3]), Boolean.parseBoolean(data[4])));
                }
                s.close();
            }
        } catch (Exception e) { System.out.println("Error loading transactions: " + e.getMessage()); }
    }

    // ====================== EXIT ======================
    private static void exitSystem() {
        System.out.println("\nThank you for using the Library System!");
        System.out.println("Group: Fantastic4");
        System.out.println("Members: Sanchez, Perez, Ison, Evangelista");
        System.exit(0);
    }

    // ====================== HELPERS ======================
    private static Borrower findBorrower(int id) { return borrowers.stream().filter(b -> b.getBorrowerId() == id).findFirst().orElse(null); }
    private static Material findMaterial(int id) { return materials.stream().filter(m -> m.getMaterialId() == id).findFirst().orElse(null); }
}

// ====================== BORROWER CLASS ======================
class Borrower {
    private int borrowerId, age, violations = 0;
    private String firstName, middleName, lastName, gender, birthday, contactNumber, email, address;

    Borrower(int id, String f, String m, String l, int a, String g, String bday, String contact, String e, String addr) {
        borrowerId = id; firstName = f; middleName = m; lastName = l; age = a;
        gender = g; birthday = bday; contactNumber = contact; email = e; address = addr;
    }

    Borrower(int id, String f, String m, String l, int a, String g, String bday, String contact, String e, String addr, int v) {
        this(id, f, m, l, a, g, bday, contact, e, addr);
        violations = v;
    }

    public int getBorrowerId() { return borrowerId; }
    public void setBorrowerId(int borrowerId) { this.borrowerId = borrowerId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public int getViolations() { return violations; }
    public void setViolations(int violations) { this.violations = violations; }

    public String toString() {
        return borrowerId + " | " + firstName + " " + middleName + " " + lastName +
               " | Age: " + age + " | Gender: " + gender + " | Birthday: " + birthday +
               " | Contact: " + contactNumber + " | Email: " + email + " | Address: " + address +
               " | Violations: " + violations;
    }
}

// ====================== MATERIAL CLASSES ======================
abstract class Material {
    private int materialId, yearPublished, totalCopies;
    private String title, author;
    public Material(int id, String t, String a, int year, int copies) {
        materialId = id; title = t; author = a; yearPublished = year; totalCopies = copies;
    }
    public int getMaterialId() { return materialId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getYearPublished() { return yearPublished; }
    public void setYearPublished(int year) { yearPublished = year; }
    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int copies) { totalCopies = copies; }
    public abstract String getType();
    public abstract int getReturnDays();
    public String toString() { return materialId + " | " + getType() + " | " + title + " | " + author + " | Year: " + yearPublished + " | Copies: " + totalCopies; }
}

class Book extends Material { public Book(int id, String t, String a, int year, int copies) { super(id, t, a, year, copies); } public String getType() { return "Book"; } public int getReturnDays() { return 7; } }
class Journal extends Material { public Journal(int id, String t, String a, int year, int copies) { super(id, t, a, year, copies); } public String getType() { return "Journal"; } public int getReturnDays() { return 3; } }
class Magazine extends Material { public Magazine(int id, String t, String a, int year, int copies) { super(id, t, a, year, copies); } public String getType() { return "Magazine"; } public int getReturnDays() { return 0; } }
class ThesisBook extends Material { public ThesisBook(int id, String t, String a, int year, int copies) { super(id, t, a, year, copies); } public String getType() { return "ThesisBook"; } public int getReturnDays() { return 2; } }

// ====================== TRANSACTION CLASS ======================
class Transaction {
    private int borrowerId, materialId; private LocalDate borrowDate, dueDate; private boolean returned = false;
    public Transaction(int bid, int mid, LocalDate borrow, LocalDate due) { borrowerId = bid; materialId = mid; borrowDate = borrow; dueDate = due; }
    public Transaction(int bid, int mid, LocalDate borrow, LocalDate due, boolean ret) { this(bid, mid, borrow, due); returned = ret; }
    public int getBorrowerId() { return borrowerId; }
    public int getMaterialId() { return materialId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isReturned() { return returned; }
    public void setReturned(boolean r) { returned = r; }
}
