import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * MODULE DEMONSTRATIONS IN THIS CLASS:
 * - Module 9: Static Nested Class (FileOperationResult).
 *              A static nested class is associated with its outer class but does NOT
 *              require an instance of the outer class to be created. It cannot access
 *              non-static members of the outer class directly.
 * - Module 13: File handling:
 *              - Character streams: FileReader and FileWriter (buffered for efficiency).
 *              - Byte streams: FileInputStream and FileOutputStream for binary backup/restore.
 *              - Console output: System.out.println for operational diagnostics.
 * - Module 14: Exception handling - checked exceptions (IOException),
 *              'throws' clause, 'try-catch-finally' / try-with-resources.
 */
public class LibraryFileManager {

    /**
     * Module 9: Static Nested Class demonstration.
     * Encapsulates the summary metadata resulting from a file operation (saving, loading, backup).
     * Because it is static, it can be instantiated independently:
     *   LibraryFileManager.FileOperationResult res = new LibraryFileManager.FileOperationResult(...);
     */
    public static class FileOperationResult {
        private final String operationName;
        private final boolean success;
        private final int recordsProcessed;
        private final String message;
        private final long timestampMillis;

        public FileOperationResult(String operationName, boolean success, int recordsProcessed, String message) {
            this.operationName = operationName;
            this.success = success;
            this.recordsProcessed = recordsProcessed;
            this.message = message;
            this.timestampMillis = System.currentTimeMillis();
        }

        public String getOperationName() {
            return operationName;
        }

        public boolean isSuccess() {
            return success;
        }

        public int getRecordsProcessed() {
            return recordsProcessed;
        }

        public String getMessage() {
            return message;
        }

        public long getTimestampMillis() {
            return timestampMillis;
        }

        @Override
        public String toString() {
            return "[" + operationName + "] " + (success ? "SUCCESS" : "FAILED") +
                   " - Records: " + recordsProcessed + " (" + message + ")";
        }
    }

    /**
     * Helper to ensure parent directories exist before writing.
     */
    private static void ensureParentDir(String filePath) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    // =========================================================================
    // Module 13: TEXT FILE HANDLING (Character Streams: FileReader & FileWriter)
    // =========================================================================

    /**
     * Saves an array of Book objects to a plain-text delimited file using FileWriter.
     * Module 14: Declares 'throws IOException' to propagate checked exception to caller.
     */
    public FileOperationResult saveBooks(Book[] books, int count, String filePath) throws IOException {
        ensureParentDir(filePath);
        FileWriter fw = null;
        try {
            fw = new FileWriter(filePath, false); // false = overwrite mode
            // Module 3: 'for' loop traversing 1D array
            for (int i = 0; i < count; i++) {
                if (books[i] != null) {
                    fw.write(books[i].toFileString() + System.lineSeparator());
                }
            }
            fw.flush();
            System.out.println("[LibraryFileManager] Saved " + count + " books to " + filePath);
            return new FileOperationResult("Save Books", true, count, "Saved successfully");
        } finally {
            // Module 14: 'finally' block guarantees closing of resource even if exception occurs
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    System.err.println("[LibraryFileManager] Error closing FileWriter: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Loads Book objects from text file using FileReader and BufferedReader.
     * Demonstrates 'while' loop for file line consumption and parsing.
     */
    public int loadBooks(String filePath, Book[] targetArray, int maxCapacity) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("[LibraryFileManager] Books file does not exist yet at: " + filePath);
            return 0;
        }

        int loadedCount = 0;
        // Module 14: try-with-resources handles automatic closing of BufferedReader and FileReader
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Module 3: 'while' loop reading each line until EOF (null)
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length >= 7) {
                    // Module 4: Wrapper class Integer.parseInt parsing numeric values
                    String id = parts[0];
                    String title = parts[1];
                    String author = parts[2];
                    String category = parts[3];
                    int total = Integer.parseInt(parts[4]);
                    int avail = Integer.parseInt(parts[5]);
                    int issued = Integer.parseInt(parts[6]);

                    if (loadedCount < maxCapacity) {
                        targetArray[loadedCount] = new Book(id, title, author, category, total, avail, issued);
                        loadedCount++;
                    }
                }
            }
        }
        System.out.println("[LibraryFileManager] Loaded " + loadedCount + " books from " + filePath);
        return loadedCount;
    }

    /**
     * Saves Member objects (including polymorphic StudentMember) using FileWriter.
     */
    public FileOperationResult saveMembers(Member[] members, int count, String filePath) throws IOException {
        ensureParentDir(filePath);
        try (FileWriter fw = new FileWriter(filePath, false)) {
            for (int i = 0; i < count; i++) {
                if (members[i] != null) {
                    fw.write(members[i].toFileString() + System.lineSeparator());
                }
            }
            fw.flush();
            System.out.println("[LibraryFileManager] Saved " + count + " members to " + filePath);
            return new FileOperationResult("Save Members", true, count, "Members saved successfully");
        }
    }

    /**
     * Loads Member and StudentMember objects using FileReader and BufferedReader.
     * Demonstrates polymorphism during object reconstruction.
     */
    public int loadMembers(String filePath, Member[] targetArray, int maxCapacity) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return 0;
        }

        int loadedCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length >= 6 && loadedCount < maxCapacity) {
                    String type = parts[0];
                    String id = parts[1];
                    String name = parts[2];
                    String contact = parts[3];
                    String memDate = parts[4];
                    int maxBooks = Integer.parseInt(parts[5]);

                    // Module 3: switch-case to construct appropriate subclass (Polymorphism)
                    switch (type) {
                        case "STUDENT":
                            if (parts.length >= 9) {
                                String regNo = parts[6];
                                String dept = parts[7];
                                int sem = Integer.parseInt(parts[8]);
                                // Module 10: Instantiate StudentMember and store in Member[] reference
                                targetArray[loadedCount] = new StudentMember(id, name, contact, memDate, maxBooks, regNo, dept, sem);
                            }
                            break;
                        case "STANDARD":
                        default:
                            targetArray[loadedCount] = new Member(id, name, contact, memDate, maxBooks);
                            break;
                    }
                    loadedCount++;
                }
            }
        }
        System.out.println("[LibraryFileManager] Loaded " + loadedCount + " members from " + filePath);
        return loadedCount;
    }

    /**
     * Saves IssueTransaction records to file.
     */
    public FileOperationResult saveTransactions(IssueTransaction[] txs, int count, String filePath) throws IOException {
        ensureParentDir(filePath);
        try (FileWriter fw = new FileWriter(filePath, false)) {
            for (int i = 0; i < count; i++) {
                if (txs[i] != null) {
                    fw.write(txs[i].toFileString() + System.lineSeparator());
                }
            }
            fw.flush();
            System.out.println("[LibraryFileManager] Saved " + count + " transactions to " + filePath);
            return new FileOperationResult("Save Transactions", true, count, "Transactions saved successfully");
        }
    }

    /**
     * Loads IssueTransaction records from file.
     */
    public int loadTransactions(String filePath, IssueTransaction[] targetArray, int maxCapacity) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return 0;
        }

        int loadedCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length >= 7 && loadedCount < maxCapacity) {
                    String txId = parts[0];
                    String bookId = parts[1];
                    String memberId = parts[2];
                    String issueDate = parts[3];
                    String dueDate = parts[4];
                    String returnDate = parts[5];
                    // Module 4: Wrapper class Boolean.parseBoolean
                    boolean returned = Boolean.parseBoolean(parts[6]);

                    targetArray[loadedCount] = new IssueTransaction(txId, bookId, memberId, issueDate, dueDate, returnDate, returned);
                    loadedCount++;
                }
            }
        }
        System.out.println("[LibraryFileManager] Loaded " + loadedCount + " transactions from " + filePath);
        return loadedCount;
    }

    // =========================================================================
    // Module 13: BYTE STREAM FILE HANDLING (FileInputStream & FileOutputStream)
    // =========================================================================

    /**
     * Creates a binary backup of an existing library text data file using byte streams.
     * Demonstrates FileInputStream and FileOutputStream reading and writing raw byte chunks.
     *
     * @param sourcePath Path of text file to back up.
     * @param backupPath Destination binary backup file path.
     * @return FileOperationResult summarizing byte count transferred.
     */
    public FileOperationResult backupLibraryData(String sourcePath, String backupPath) throws IOException {
        File srcFile = new File(sourcePath);
        if (!srcFile.exists()) {
            return new FileOperationResult("Backup", false, 0, "Source file does not exist: " + sourcePath);
        }

        ensureParentDir(backupPath);
        long totalBytesCopied = 0;

        // Module 13: FileInputStream and FileOutputStream for byte-level streaming
        try (FileInputStream inStream = new FileInputStream(srcFile);
             FileOutputStream outStream = new FileOutputStream(backupPath)) {

            byte[] buffer = new byte[1024]; // 1 KB buffer
            int bytesRead;

            // Module 3: 'while' loop reading byte chunks until end-of-stream (-1)
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
                totalBytesCopied += bytesRead;
            }
            outStream.flush();
        }

        System.out.println("[LibraryFileManager] Binary backup completed: " + totalBytesCopied + " bytes copied to " + backupPath);
        return new FileOperationResult("Backup Byte Stream", true, (int) totalBytesCopied, "Byte stream backup successful (" + totalBytesCopied + " bytes)");
    }

    /**
     * Restores library data from a binary backup file back to the target text file using byte streams.
     */
    public FileOperationResult restoreLibraryData(String backupPath, String targetPath) throws IOException {
        File bkpFile = new File(backupPath);
        if (!bkpFile.exists()) {
            return new FileOperationResult("Restore", false, 0, "Backup file does not exist: " + backupPath);
        }

        ensureParentDir(targetPath);
        long totalBytesCopied = 0;

        try (FileInputStream inStream = new FileInputStream(bkpFile);
             FileOutputStream outStream = new FileOutputStream(targetPath)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
                totalBytesCopied += bytesRead;
            }
            outStream.flush();
        }

        System.out.println("[LibraryFileManager] Binary restore completed: " + totalBytesCopied + " bytes restored to " + targetPath);
        return new FileOperationResult("Restore Byte Stream", true, (int) totalBytesCopied, "Restored " + totalBytesCopied + " bytes");
    }
}
