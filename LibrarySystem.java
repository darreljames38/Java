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
    // File names (simple CSV storage)
    private static final String BORROWERS_FILE = "borrowers.txt";
    private static final String MATERIALS_FILE = "materials.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";

    // Date formatter
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public static void main(String[] args) {
        Library lib = new Library();
        lib.loadAll(); // load data from files (creates files if absent)
        lib.run();     // start interactive menu loop
    }

    // -------------------------
    // Library manager class
    // -------------------------
    static class Library {
        private final Scanner scanner = new Scanner(System.in);
        private final List<Borrower> borrowers = new ArrayList<>();
        private final List<Material> materials = new ArrayList<>();
        private final List<Transaction> transactions = new ArrayList<>();

        Library() {
            // nothing
        }

        void loadAll() {
            try {
                Files.createFileIfNotExists(BORROWERS_FILE);
                Files.createFileIfNotExists(MATERIALS_FILE);
                Files.createFileIfNotExists(TRANSACTIONS_FILE);
            } catch (IOException e) {
                System.err.println("Error ensuring data files exist: " + e.getMessage());
            }

            loadBorrowers();
            loadMaterials();
            loadTransactions();
        }

        void run() {
            boolean running = true;
            while (running) {
                try {
                    System.out.println("\n--- FANTASTIC4 LIBRARY SYSTEM ---");
                    System.out.println("1. Borrowers Management");
                    System.out.println("2. Asset Management");
                    System.out.println("3. Borrow");
                    System.out.println("4. Return Book");
                    System.out.println("5. Borrower History");
                    System.out.println("6. Book (Material) History");
                    System.out.println("7. Exit");
                    System.out.print("Choose an option (1-7): ");
                    String choice = scanner.nextLine().trim();
                    switch (choice) {
                        case "1": borrowersManagementMenu(); break;
                        case "2": assetsManagementMenu(); break;
                        case "3": borrowMaterial(); break;
                        case "4": returnMaterial(); break;
                        case "5": showBorrowerHistory(); break;
                        case "6": showMaterialHistory(); break;
                        case "7": exit(); running = false; break;
                        default: System.out.println("Invalid choice. Please choose between 1 and 7."); break;
                    }
                } catch (Exception ex) {
                    System.err.println("An unexpected error occurred: " + ex.getMessage());
                }
            }
        }

        private void exit() {
            saveBorrowers();
            saveMaterials();
            saveTransactions();
            System.out.println("\n--- Exiting Library System ---");
            System.out.println("Group: Fantastic4");
            System.out.println("Members: Alice, Bob, Charlie, Daryl (edit names in code if needed)");
            System.out.println("Thank you for using the system!");
        }

        // --------------------------
        // Borrowers Management
        // --------------------------
        private void borrowersManagementMenu() {
            boolean back = false;
            while (!back) {
                System.out.println("\n-- Borrowers Management --");
                System.out.println("1. Add Borrower");
                System.out.println("2. Edit Borrower");
                System.out.println("3. Delete Borrower");
                System.out.println("4. View All Borrowers");
                System.out.println("5. Back");
                System.out.print("Choice: ");
                String ch = scanner.nextLine().trim();
                switch (ch) {
                    case "1": addBorrower(); break;
                    case "2": editBorrower(); break;
                    case "3": deleteBorrower(); break;
                    case "4": viewBorrowers(); break;
                    case "5": back = true; break;
                    default: System.out.println("Invalid choice."); break;
                }
            }
        }

        private void addBorrower() {
            try {
                System.out.println("\n-- Add Borrower --");
                String id = promptNonEmpty("Borrower ID: ");
                if (findBorrowerById(id) != null) {
                    System.out.println("Borrower with this ID already exists.");
                    return;
                }
                String first = promptValidated("First name: ", Library::validateName, "Name must only contain letters, spaces, hyphen or apostrophe.");
                String middle = promptOptionalValidated("Middle name (or leave blank): ", Library::validateName, "Invalid name.");
                String last = promptValidated("Last name: ", Library::validateName, "Name must only contain letters, spaces, hyphen or apostrophe.");
                String gender = promptValidated("Gender (M/F): ", s -> s.equalsIgnoreCase("M") || s.equalsIgnoreCase("F"), "Enter M or F.");
                LocalDate birthday = promptDate("Birthday (YYYY-MM-DD): ");
                String contact = promptValidated("Contact number (digits only): ", Library::validatePhone, "Phone must contain digits only (7-15 digits).");
                String email = promptValidated("Email: ", Library::validateEmail, "Invalid email format.");
                String address = promptNonEmpty("Address: ");
                int violations = 0;

                // prevent duplicate by same full name + email
                if (borrowers.stream().anyMatch(b -> b.getFirstName().equalsIgnoreCase(first)
                        && b.getLastName().equalsIgnoreCase(last) && b.getEmail().equalsIgnoreCase(email))) {
                    System.out.println("This borrower seems already registered (same name & email).");
                    return;
                }

                Borrower b = new Borrower(id, first, middle, last, gender.toUpperCase(), birthday, contact, email, address, violations);
                borrowers.add(b);
                saveBorrowers();
                System.out.println("Borrower added.");
            } catch (Exception ex) {
                System.out.println("Failed to add borrower: " + ex.getMessage());
            }
        }

        private void editBorrower() {
            System.out.println("\n-- Edit Borrower --");
            String id = promptNonEmpty("Enter Borrower ID to edit: ");
            Borrower b = findBorrowerById(id);
            if (b == null) { System.out.println("Borrower not found."); return; }
            System.out.println("Editing borrower: " + b.getFullName() + " (leave blank to keep current)");

            try {
                String first = promptMaybe("First name [" + b.getFirstName() + "]: ");
                if (!first.isBlank()) {
                    if (!validateName(first)) { System.out.println("Invalid name format. Edit aborted."); return; }
                    b.setFirstName(first);
                }
                String middle = promptMaybe("Middle name [" + b.getMiddleName() + "]: ");
                if (!middle.isBlank()) {
                    if (!validateName(middle)) { System.out.println("Invalid name format. Edit aborted."); return; }
                    b.setMiddleName(middle);
                }
                String last = promptMaybe("Last name [" + b.getLastName() + "]: ");
                if (!last.isBlank()) {
                    if (!validateName(last)) { System.out.println("Invalid name format. Edit aborted."); return; }
                    b.setLastName(last);
                }
                String gender = promptMaybe("Gender (M/F) [" + b.getGender() + "]: ");
                if (!gender.isBlank()) {
                    if (!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F")) { System.out.println("Invalid gender. Edit aborted."); return; }
                    b.setGender(gender.toUpperCase());
                }
                String bd = promptMaybe("Birthday [" + b.getBirthday().format(DATE_FMT) + "]: ");
                if (!bd.isBlank()) {
                    b.setBirthday(LocalDate.parse(bd));
                }
                String contact = promptMaybe("Contact [" + b.getContactNumber() + "]: ");
                if (!contact.isBlank()) {
                    if (!validatePhone(contact)) { System.out.println("Invalid contact. Edit aborted."); return; }
                    b.setContactNumber(contact);
                }
                String email = promptMaybe("Email [" + b.getEmail() + "]: ");
                if (!email.isBlank()) {
                    if (!validateEmail(email)) { System.out.println("Invalid email. Edit aborted."); return; }
                    b.setEmail(email);
                }
                String address = promptMaybe("Address [" + b.getAddress() + "]: ");
                if (!address.isBlank()) {
                    b.setAddress(address);
                }
                saveBorrowers();
                System.out.println("Borrower updated.");
            } catch (Exception ex) {
                System.out.println("Error updating borrower: " + ex.getMessage());
            }
        }

        private void deleteBorrower() {
            System.out.println("\n-- Delete Borrower --");
            String id = promptNonEmpty("Borrower ID to delete: ");
            Borrower b = findBorrowerById(id);
            if (b == null) { System.out.println("Not found."); return; }
            // ensure borrower currently has no active borrowings
            boolean hasActive = transactions.stream().anyMatch(t -> t.getBorrowerId().equals(id) && !t.isReturned());
            if (hasActive) {
                System.out.println("Borrower has active borrowed materials and cannot be deleted.");
                return;
            }
            borrowers.removeIf(x -> x.getId().equals(id));
            saveBorrowers();
            System.out.println("Borrower deleted.");
        }

        private void viewBorrowers() {
            System.out.println("\n-- List of Borrowers --");
            if (borrowers.isEmpty()) { System.out.println("No borrowers registered."); return; }
            for (Borrower b : borrowers) {
                System.out.println(b);
            }
        }

        // --------------------------
        // Asset Management
        // --------------------------
        private void assetsManagementMenu() {
            boolean back = false;
            while (!back) {
                System.out.println("\n-- Asset Management --");
                System.out.println("1. Add Material");
                System.out.println("2. Edit Material");
                System.out.println("3. Delete Material");
                System.out.println("4. View All Materials");
                System.out.println("5. Back");
                System.out.print("Choice: ");
                String ch = scanner.nextLine().trim();
                switch (ch) {
                    case "1": addMaterial(); break;
                    case "2": editMaterial(); break;
                    case "3": deleteMaterial(); break;
                    case "4": viewMaterials(); break;
                    case "5": back = true; break;
                    default: System.out.println("Invalid choice."); break;
                }
            }
        }

        private void addMaterial() {
            try {
                System.out.println("\n-- Add Material --");
                System.out.println("Types: 1.Book 2.Journal 3.Magazine 4.ThesisBook");
                String typeChoice = promptNonEmpty("Choose type (1-4): ");
                String id = promptNonEmpty("Material ID: ");
                if (findMaterialById(id) != null) {
                    System.out.println("Material with this ID already exists.");
                    return;
                }
                System.out.print("Year published (YYYY): ");
                int year = Integer.parseInt(scanner.nextLine().trim());
                String publisher = promptNonEmpty("Publisher: ");
                System.out.print("Number of copies: ");
                int copies = Integer.parseInt(scanner.nextLine().trim());
                Material mat = null;
                switch (typeChoice) {
                    case "1": // Book
                        String titleB = promptNonEmpty("Book Title: ");
                        String authorB = promptNonEmpty("Author: ");
                        mat = new Book(id, titleB, authorB, year, publisher, copies);
                        break;
                    case "2": // Journal
                        String jname = promptNonEmpty("Journal Name: ");
                        mat = new Journal(id, jname, year, publisher, copies);
                        break;
                    case "3": // Magazine
                        String mname = promptNonEmpty("Magazine Name: ");
                        mat = new Magazine(id, mname, year, publisher, copies);
                        break;
                    case "4": // ThesisBook
                        String titleT = promptNonEmpty("Thesis Title: ");
                        String authorT = promptNonEmpty("Author: ");
                        mat = new ThesisBook(id, titleT, authorT, year, publisher, copies);
                        break;
                    default: System.out.println("Invalid type."); return;
                }

                materials.add(mat);
                saveMaterials();
                System.out.println("Material added.");
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid number entered. Add material aborted.");
            } catch (Exception ex) {
                System.out.println("Error adding material: " + ex.getMessage());
            }
        }

        private void editMaterial() {
            System.out.println("\n-- Edit Material --");
            String id = promptNonEmpty("Material ID to edit: ");
            Material m = findMaterialById(id);
            if (m == null) { System.out.println("Material not found."); return; }
            System.out.println("Editing: " + m.getDisplayTitle() + " (leave blank to keep current)");
            try {
                String title = promptMaybe("Title/Name [" + m.getDisplayTitle() + "]: ");
                if (!title.isBlank()) { m.setTitle(title); }
                String author = promptMaybe("Author (if applicable) [" + (m.getAuthor()==null? "N/A":m.getAuthor()) + "]: ");
                if (!author.isBlank()) m.setAuthor(author);
                String pub = promptMaybe("Publisher [" + m.getPublisher() + "]: ");
                if (!pub.isBlank()) m.setPublisher(pub);
                String year = promptMaybe("Year [" + m.getYearPublished() + "]: ");
                if (!year.isBlank()) m.setYearPublished(Integer.parseInt(year));
                String copies = promptMaybe("Total copies [" + m.getTotalCopies() + "]: ");
                if (!copies.isBlank()) m.setTotalCopies(Integer.parseInt(copies));
                saveMaterials();
                System.out.println("Material updated.");
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid number. Edit aborted.");
            } catch (Exception ex) {
                System.out.println("Error editing material: " + ex.getMessage());
            }
        }

        private void deleteMaterial() {
            System.out.println("\n-- Delete Material --");
            String id = promptNonEmpty("Enter Material ID to delete: ");
            Material m = findMaterialById(id);
            if (m == null) { System.out.println("Not found."); return; }
            // ensure no active borrowings of this material
            boolean hasActive = transactions.stream().anyMatch(t -> t.getMaterialId().equals(id) && !t.isReturned());
            if (hasActive) {
                System.out.println("This material has active borrowings and cannot be deleted.");
                return;
            }
            materials.removeIf(x -> x.getId().equals(id));
            saveMaterials();
            System.out.println("Material deleted.");
        }

        private void viewMaterials() {
            System.out.println("\n-- List of Materials --");
            if (materials.isEmpty()) { System.out.println("No materials."); return; }
            for (Material m : materials) {
                System.out.println(m);
            }
        }

        // --------------------------
        // Borrowing Logic
        // --------------------------
        private void borrowMaterial() {
            try {
                System.out.println("\n-- Borrow Material --");
                String borrowerId = promptNonEmpty("Borrower ID: ");
                Borrower b = findBorrowerById(borrowerId);
                if (b == null) { System.out.println("Borrower not registered."); return; }
                if (b.getViolations() >= 3) { System.out.println("Borrower has 3 or more strikes and cannot borrow."); return; }
                // check borrower doesn't have an active borrow already (requirement: any borrower can only borrow one material at moment)
                boolean hasActive = transactions.stream().anyMatch(t -> t.getBorrowerId().equals(borrowerId) && !t.isReturned());
                if (hasActive) { System.out.println("Borrower already has a borrowed material. Return it first to borrow another."); return; }

                String materialId = promptNonEmpty("Material ID to borrow: ");
                Material m = findMaterialById(materialId);
                if (m == null) { System.out.println("Material not found."); return; }
                if (m.getAvailableCopies() <= 0) { System.out.println("No available copies to borrow."); return; }

                LocalDate borrowedDate = LocalDate.now();
                LocalDate dueDate = borrowedDate.plusDays(m.getLoanDays());

                // create transaction
                Transaction t = new Transaction(UUID.randomUUID().toString(), borrowerId, materialId, borrowedDate, dueDate, false, null);
                transactions.add(t);

                // decrement available copy (we represent availability via total copies vs active borrows)
                m.incrementBorrowedCopies(1);

                saveMaterials();
                saveTransactions();
                System.out.println("Borrow successful. Due date: " + dueDate.format(DATE_FMT));
            } catch (Exception ex) {
                System.out.println("Error during borrow: " + ex.getMessage());
            }
        }

        private void returnMaterial() {
            try {
                System.out.println("\n-- Return Material --");
                String borrowerId = promptNonEmpty("Borrower ID: ");
                Borrower b = findBorrowerById(borrowerId);
                if (b == null) { System.out.println("Borrower not registered."); return; }

                // find active transaction for this borrower
                Optional<Transaction> opt = transactions.stream()
                        .filter(t -> t.getBorrowerId().equals(borrowerId) && !t.isReturned())
                        .findFirst();
                if (opt.isEmpty()) { System.out.println("This borrower has no active borrowed materials."); return; }

                Transaction t = opt.get();
                Material m = findMaterialById(t.getMaterialId());
                if (m == null) { System.out.println("Material record not found (data inconsistency)."); return; }

                LocalDate returnedOn = LocalDate.now();
                t.setReturned(true);
                t.setReturnedDate(returnedOn);

                // check lateness
                if (returnedOn.isAfter(t.getDueDate())) {
                    b.incrementViolations(1);
                    System.out.println("Material returned late. Borrower receives 1 strike. Total strikes: " + b.getViolations());
                } else {
                    System.out.println("Material returned on time. No strike.");
                }

                // reduce borrowed copies count
                m.incrementBorrowedCopies(-1);

                saveBorrowers();
                saveMaterials();
                saveTransactions();
                System.out.println("Return processed.");
            } catch (Exception ex) {
                System.out.println("Error processing return: " + ex.getMessage());
            }
        }

        // --------------------------
        // History views
        // --------------------------
        private void showBorrowerHistory() {
            System.out.println("\n-- Borrower History --");
            String id = promptNonEmpty("Enter Borrower ID: ");
            Borrower b = findBorrowerById(id);
            if (b == null) { System.out.println("Borrower not found."); return; }
            System.out.println("History for: " + b.getFullName());

            List<Transaction> list = new ArrayList<>();
            for (Transaction t : transactions) if (t.getBorrowerId().equals(id)) list.add(t);
            if (list.isEmpty()) { System.out.println("No transactions for this borrower."); return; }
            for (Transaction t : list) {
                Material m = findMaterialById(t.getMaterialId());
                System.out.printf("Material: %s | Borrowed: %s | Due: %s | Returned: %s | ReturnedDate: %s\n",
                        (m==null? t.getMaterialId(): m.getDisplayTitle()),
                        t.getBorrowedDate().format(DATE_FMT),
                        t.getDueDate().format(DATE_FMT),
                        t.isReturned() ? "Yes":"No",
                        t.getReturnedDate() == null ? "-" : t.getReturnedDate().format(DATE_FMT));
            }
        }

        private void showMaterialHistory() {
            System.out.println("\n-- Material History --");
            String id = promptNonEmpty("Enter Material ID: ");
            Material m = findMaterialById(id);
            if (m == null) { System.out.println("Material not found."); return; }
            System.out.println("History for: " + m.getDisplayTitle());
            List<Transaction> list = new ArrayList<>();
            for (Transaction t : transactions) if (t.getMaterialId().equals(id)) list.add(t);
            if (list.isEmpty()) { System.out.println("No transactions for this material."); return; }
            for (Transaction t : list) {
                Borrower b = findBorrowerById(t.getBorrowerId());
                System.out.printf("Borrower: %s | Borrowed: %s | Due: %s | Returned: %s | ReturnedDate: %s\n",
                        (b==null? t.getBorrowerId(): b.getFullName()),
                        t.getBorrowedDate().format(DATE_FMT),
                        t.getDueDate().format(DATE_FMT),
                        t.isReturned() ? "Yes":"No",
                        t.getReturnedDate() == null ? "-" : t.getReturnedDate().format(DATE_FMT)
                );
            }
        }

        // --------------------------
        // File I/O
        // --------------------------
        private void loadBorrowers() {
            borrowers.clear();
            try (BufferedReader br = new BufferedReader(new FileReader(BORROWERS_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    // CSV: id|first|middle|last|gender|birthday|contact|email|address|violations
                    String[] parts = line.split("\\|", -1);
                    if (parts.length < 10) continue;
                    Borrower b = new Borrower(parts[0], parts[1], parts[2], parts[3], parts[4],
                            LocalDate.parse(parts[5]), parts[6], parts[7], parts[8], Integer.parseInt(parts[9]));
                    borrowers.add(b);
                }
            } catch (IOException e) {
                System.err.println("Failed to load borrowers: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Malformed borrower data: " + e.getMessage());
            }
        }

        private void saveBorrowers() {
            try (PrintWriter pw = new PrintWriter(new FileWriter(BORROWERS_FILE))) {
                for (Borrower b : borrowers) {
                    // id|first|middle|last|gender|birthday|contact|email|address|violations
                    pw.printf("%s|%s|%s|%s|%s|%s|%s|%s|%s|%d%n",
                            b.getId(), escape(b.getFirstName()), escape(b.getMiddleName()), escape(b.getLastName()), b.getGender(),
                            b.getBirthday().format(DATE_FMT), b.getContactNumber(), b.getEmail(), escape(b.getAddress()), b.getViolations());
                }
            } catch (IOException e) {
                System.err.println("Failed to save borrowers: " + e.getMessage());
            }
        }

        private void loadMaterials() {
            materials.clear();
            try (BufferedReader br = new BufferedReader(new FileReader(MATERIALS_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    // CSV: type|id|title|author|year|publisher|totalCopies|borrowedCopies
                    String[] parts = line.split("\\|", -1);
                    if (parts.length < 8) continue;
                    String type = parts[0];
                    String id = parts[1];
                    String title = unescape(parts[2]);
                    String author = unescape(parts[3]);
                    int year = Integer.parseInt(parts[4]);
                    String publisher = unescape(parts[5]);
                    int totalCopies = Integer.parseInt(parts[6]);
                    int borrowedCopies = Integer.parseInt(parts[7]);

                    Material m = null;
                    switch (type) {
                        case "BOOK": m = new Book(id, title, author, year, publisher, totalCopies); break;
                        case "JOURNAL": m = new Journal(id, title, year, publisher, totalCopies); break;
                        case "MAGAZINE": m = new Magazine(id, title, year, publisher, totalCopies); break;
                        case "THESIS": m = new ThesisBook(id, title, author, year, publisher, totalCopies); break;
                        default: continue;
                    }
                    m.setBorrowedCopies(borrowedCopies);
                    materials.add(m);
                }
            } catch (IOException e) {
                System.err.println("Failed to load materials: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Malformed material data: " + e.getMessage());
            }
        }

        private void saveMaterials() {
            try (PrintWriter pw = new PrintWriter(new FileWriter(MATERIALS_FILE))) {
                for (Material m : materials) {
                    // type|id|title|author|year|publisher|totalCopies|borrowedCopies
                    pw.printf("%s|%s|%s|%s|%d|%s|%d|%d%n",
                            m.getTypeTag(), m.getId(), escape(m.getTitle()), escape(m.getAuthor()==null? "": m.getAuthor()),
                            m.getYearPublished(), escape(m.getPublisher()), m.getTotalCopies(), m.getBorrowedCopies());
                }
            } catch (IOException e) {
                System.err.println("Failed to save materials: " + e.getMessage());
            }
        }

        private void loadTransactions() {
            transactions.clear();
            try (BufferedReader br = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    // CSV: txId|borrowerId|materialId|borrowedDate|dueDate|returned(true/false)|returnedDate or ""
                    String[] parts = line.split("\\|", -1);
                    if (parts.length < 7) continue;
                    String txId = parts[0];
                    String borrowerId = parts[1];
                    String materialId = parts[2];
                    LocalDate borrowedDate = LocalDate.parse(parts[3]);
                    LocalDate dueDate = LocalDate.parse(parts[4]);
                    boolean returned = Boolean.parseBoolean(parts[5]);
                    LocalDate returnedDate = parts[6].isBlank() ? null : LocalDate.parse(parts[6]);

                    Transaction t = new Transaction(txId, borrowerId, materialId, borrowedDate, dueDate, returned, returnedDate);
                    transactions.add(t);
                }
            } catch (IOException e) {
                System.err.println("Failed to load transactions: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Malformed transaction data: " + e.getMessage());
            }
        }

        private void saveTransactions() {
            try (PrintWriter pw = new PrintWriter(new FileWriter(TRANSACTIONS_FILE))) {
                for (Transaction t : transactions) {
                    // txId|borrowerId|materialId|borrowedDate|dueDate|returned|returnedDate
                    pw.printf("%s|%s|%s|%s|%s|%b|%s%n",
                            t.getId(), t.getBorrowerId(), t.getMaterialId(),
                            t.getBorrowedDate().format(DATE_FMT), t.getDueDate().format(DATE_FMT),
                            t.isReturned(), t.getReturnedDate() == null ? "" : t.getReturnedDate().format(DATE_FMT));
                }
            } catch (IOException e) {
                System.err.println("Failed to save transactions: " + e.getMessage());
            }
        }

        // --------------------------
        // Utilities & helpers
        // --------------------------
        private Borrower findBorrowerById(String id) {
            return borrowers.stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null);
        }

        private Material findMaterialById(String id) {
            return materials.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
        }

        // Prompts & input helpers
        private String promptNonEmpty(String prompt) {
            while (true) {
                System.out.print(prompt);
                String s = scanner.nextLine().trim();
                if (!s.isEmpty()) return s;
                System.out.println("Input cannot be empty.");
            }
        }

        private String promptMaybe(String prompt) {
            System.out.print(prompt);
            return scanner.nextLine();
        }

        private String promptValidated(String prompt, ValidateFn fn, String errorMsg) {
            while (true) {
                System.out.print(prompt);
                String s = scanner.nextLine().trim();
                if (fn.test(s)) return s;
                System.out.println(errorMsg);
            }
        }

        private String promptOptionalValidated(String prompt, ValidateFn fn, String errorMsg) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) return "";
            if (fn.test(s)) return s;
            System.out.println(errorMsg);
            return promptOptionalValidated(prompt, fn, errorMsg);
        }

        private LocalDate promptDate(String prompt) {
            while (true) {
                System.out.print(prompt);
                String s = scanner.nextLine().trim();
                try {
                    return LocalDate.parse(s);
                } catch (Exception e) {
                    System.out.println("Invalid date format. Use YYYY-MM-DD.");
                }
            }
        }

        private static boolean validateName(String s) {
            if (s == null) return false;
            s = s.trim();
            if (s.isEmpty()) return false;
            // letters, spaces, hyphen, apostrophe
            return Pattern.matches("[A-Za-z\\s\\-']+", s);
        }

        private static boolean validateEmail(String s) {
            if (s == null) return false;
            s = s.trim();
            // basic pattern
            return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", s);
        }

        private static boolean validatePhone(String s) {
            if (s == null) return false;
            s = s.trim();
            return Pattern.matches("\\d{7,15}", s);
        }

        private static String escape(String s) {
            if (s == null) return "";
            return s.replace("\n"," ").replace("|","/");
        }

        private static String unescape(String s) {
            if (s == null) return "";
            return s.replace("/", "|");
        }

        // small functional interface for validation
        private interface ValidateFn { boolean test(String s); }
    }

    // -------------------------
    // Domain classes
    // -------------------------
    static class Borrower {
        private final String id;
        private String firstName;
        private String middleName;
        private String lastName;
        private String gender;
        private LocalDate birthday;
        private String contactNumber;
        private String email;
        private String address;
        private int violations;

        Borrower(String id, String firstName, String middleName, String lastName,
                 String gender, LocalDate birthday, String contactNumber, String email,
                 String address, int violations) {
            this.id = id;
            this.firstName = firstName;
            this.middleName = middleName == null ? "" : middleName;
            this.lastName = lastName;
            this.gender = gender;
            this.birthday = birthday;
            this.contactNumber = contactNumber;
            this.email = email;
            this.address = address;
            this.violations = violations;
        }

        String getId() { return id; }
        String getFirstName() { return firstName; }
        String getMiddleName() { return middleName; }
        String getLastName() { return lastName; }
        String getFullName() { return firstName + " " + (middleName.isEmpty() ? "" : (middleName + " ")) + lastName; }
        String getGender() { return gender; }
        LocalDate getBirthday() { return birthday; }
        String getContactNumber() { return contactNumber; }
        String getEmail() { return email; }
        String getAddress() { return address; }
        int getViolations() { return violations; }

        void setFirstName(String s) { firstName = s; }
        void setMiddleName(String s) { middleName = s; }
        void setLastName(String s) { lastName = s; }
        void setGender(String s) { gender = s; }
        void setBirthday(LocalDate d) { birthday = d; }
        void setContactNumber(String s) { contactNumber = s; }
        void setEmail(String s) { email = s; }
        void setAddress(String s) { address = s; }
        void setViolations(int v) { violations = v; }
        void incrementViolations(int v) { violations += v; }

        @Override
        public String toString() {
            return String.format("ID: %s | Name: %s | Gender: %s | Birthday: %s | Contact: %s | Email: %s | Address: %s | Strikes: %d",
                    id, getFullName(), gender, birthday.format(DateTimeFormatter.ISO_LOCAL_DATE), contactNumber, email, address, violations);
        }
    }

    // Material base class (OOP: inheritance, polymorphism)
    static abstract class Material {
        private final String id;
        private String title; // name or title
        private String author; // optional
        private int yearPublished;
        private String publisher;
        private int totalCopies;
        private int borrowedCopies = 0; // number currently borrowed

        Material(String id, String title, String author, int yearPublished, String publisher, int totalCopies) {
            this.id = id;
            this.title = title;
            this.author = (author == null || author.isBlank()) ? null : author;
            this.yearPublished = yearPublished;
            this.publisher = publisher;
            this.totalCopies = Math.max(0, totalCopies);
        }

        String getId() { return id; }
        String getTitle() { return title; }
        String getAuthor() { return author; }
        int getYearPublished() { return yearPublished; }
        String getPublisher() { return publisher; }
        int getTotalCopies() { return totalCopies; }
        int getBorrowedCopies() { return borrowedCopies; }

        void setTitle(String t) { title = t; }
        void setAuthor(String a) { author = a; }
        void setYearPublished(int y) { yearPublished = y; }
        void setPublisher(String p) { publisher = p; }
        void setTotalCopies(int c) { totalCopies = Math.max(0, c); }
        void setBorrowedCopies(int b) { borrowedCopies = Math.max(0, b); }

        void incrementBorrowedCopies(int delta) {
            borrowedCopies += delta;
            if (borrowedCopies < 0) borrowedCopies = 0;
            if (borrowedCopies > totalCopies) borrowedCopies = totalCopies;
        }

        int getAvailableCopies() { return totalCopies - borrowedCopies; }

        // each subclass supplies loan days
        abstract int getLoanDays();
        abstract String getTypeTag();
        String getDisplayTitle() { return title + (author==null? "": (" by " + author)); }

        @Override
        public String toString() {
            return String.format("[%s] ID:%s | %s | Year:%d | Publisher:%s | Copies: %d (Available: %d)",
                    getTypeTag(), id, getDisplayTitle(), yearPublished, publisher, totalCopies, getAvailableCopies());
        }
    }

    static class Book extends Material {
        Book(String id, String title, String author, int yearPublished, String publisher, int totalCopies) {
            super(id, title, author, yearPublished, publisher, totalCopies);
        }
        @Override int getLoanDays() { return 7; }
        @Override String getTypeTag() { return "BOOK"; }
    }

    static class Journal extends Material {
        Journal(String id, String title, int yearPublished, String publisher, int totalCopies) {
            super(id, title, null, yearPublished, publisher, totalCopies);
        }
        @Override int getLoanDays() { return 3; }
        @Override String getTypeTag() { return "JOURNAL"; }
    }

    static class Magazine extends Material {
        Magazine(String id, String title, int yearPublished, String publisher, int totalCopies) {
            super(id, title, null, yearPublished, publisher, totalCopies);
        }
        @Override int getLoanDays() { return 0; } // must be returned same day
        @Override String getTypeTag() { return "MAGAZINE"; }
    }

    static class ThesisBook extends Material {
        ThesisBook(String id, String title, String author, int yearPublished, String publisher, int totalCopies) {
            super(id, title, author, yearPublished, publisher, totalCopies);
        }
        @Override int getLoanDays() { return 2; }
        @Override String getTypeTag() { return "THESIS"; }
    }

    // Transaction record
    static class Transaction {
        private final String id;
        private final String borrowerId;
        private final String materialId;
        private final LocalDate borrowedDate;
        private final LocalDate dueDate;
        private boolean returned;
        private LocalDate returnedDate;

        Transaction(String id, String borrowerId, String materialId, LocalDate borrowedDate, LocalDate dueDate, boolean returned, LocalDate returnedDate) {
            this.id = id;
            this.borrowerId = borrowerId;
            this.materialId = materialId;
            this.borrowedDate = borrowedDate;
            this.dueDate = dueDate;
            this.returned = returned;
            this.returnedDate = returnedDate;
        }

        String getId() { return id; }
        String getBorrowerId() { return borrowerId; }
        String getMaterialId() { return materialId; }
        LocalDate getBorrowedDate() { return borrowedDate; }
        LocalDate getDueDate() { return dueDate; }
        boolean isReturned() { return returned; }
        LocalDate getReturnedDate() { return returnedDate; }

        void setReturned(boolean r) { returned = r; }
        void setReturnedDate(LocalDate d) { returnedDate = d; }
    }

    // -------------------------
    // Helper: Files.createFileIfNotExists using NIO
    // -------------------------
    static class Files {
        static void createFileIfNotExists(String filename) throws IOException {
            Path p = Paths.get(filename);
            if (!java.nio.file.Files.exists(p)) {
                java.nio.file.Files.createFile(p);
            }
        }
    }
}
