import java.sql.*;
import java.util.Scanner;

public class HostelManagementApp {
    // Update your database URL, username, and password accordingly
    private static final String URL = "jdbc:mysql://localhost:3306/hostel_db";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            int choice;
            do {
                System.out.println("\n--- KIET Hostel Management System ---");
                System.out.println("1. Insert Hostel Student Records (a)");
                System.out.println("2. Conditional Retrieval and Sorting (b)");
                System.out.println("3. Update Hostel Fee (c)");
                System.out.println("4. Search and Modify Hostel Details (d)");
                System.out.println("5. Aggregate Query and Conditional Deletion (e)");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        insertRecords(conn, scanner);
                        break;
                    case 2:
                        retrieveAndSort(conn, scanner);
                        break;
                    case 3:
                        updateFee(conn, scanner);
                        break;
                    case 4:
                        searchAndModifyRoom(conn, scanner);
                        break;
                    case 5:
                        aggregateAndDelete(conn, scanner);
                        break;
                    case 6:
                        System.out.println("Exiting application. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            } while (choice != 6);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // (a) Insert Hostel Student Records using PreparedStatement
    private static void insertRecords(Connection conn, Scanner scanner) {
        String sql = "INSERT INTO HostelStudent (student_id, student_name, roll_number, hostel_name, room_number, fee) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            System.out.println("Inserting 3 sample student records...");

            // Sample Data 1
            pstmt.setInt(1, 101);
            pstmt.setString(2, "Aarav Sharma");
            pstmt.setString(3, "23001001");
            pstmt.setString(4, "Bhabha");
            pstmt.setString(5, "102");
            pstmt.setDouble(6, 55000.0);
            pstmt.executeUpdate();

            // Sample Data 2
            pstmt.setInt(1, 102);
            pstmt.setString(2, "Priya Verma");
            pstmt.setString(3, "23001002");
            pstmt.setString(4, "Ramanujan");
            pstmt.setString(5, "204");
            pstmt.setDouble(6, 48000.0);
            pstmt.executeUpdate();

            // Sample Data 3
            pstmt.setInt(1, 103);
            pstmt.setString(2, "Rohan Singh");
            pstmt.setString(3, "23001003");
            pstmt.setString(4, "Bhabha");
            pstmt.setString(5, "105");
            pstmt.setDouble(6, 62000.0);
            pstmt.executeUpdate();

            System.out.println("Records inserted successfully!");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error: Duplicate student ID encountered. Record already exists[cite: 1].");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // (b) Conditional Retrieval and Sorting (Fee > 50,000, sorted descending)
    private static void retrieveAndSort(Connection conn, Scanner scanner) {
        scanner.nextLine(); // consume newline
        System.out.print("Enter hostel name: ");
        String hostelName = scanner.nextLine();

        String sql = "SELECT * FROM HostelStudent WHERE hostel_name = ? AND fee > 50000 ORDER BY fee DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hostelName);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- Students in " + hostelName + " with Fee > ₹50,000 (Sorted Descending) ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("ID: " + rs.getInt("student_id") +
                        ", Name: " + rs.getString("student_name") +
                        ", Roll No: " + rs.getString("roll_number") +
                        ", Room: " + rs.getString("room_number") +
                        ", Fee: ₹" + rs.getDouble("fee"));
            }
            if (!found) {
                System.out.println("No records found matching the criteria.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // (c) Update Hostel Fee
    private static void updateFee(Connection conn, Scanner scanner) {
        System.out.print("Enter Student ID: ");
        int studentId = scanner.nextInt();
        System.out.print("Enter New Hostel Fee: ");
        double newFee = scanner.nextDouble();

        String checkSql = "SELECT * FROM HostelStudent WHERE student_id = ?";
        String updateSql = "UPDATE HostelStudent SET fee = ? WHERE student_id = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            checkStmt.setInt(1, studentId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                updateStmt.setDouble(1, newFee);
                updateStmt.setInt(2, studentId);
                updateStmt.executeUpdate();
                System.out.println("Hostel fee updated successfully for Student ID: " + studentId);
            } else {
                System.out.println("Error: Student record with ID " + studentId + " does not exist[cite: 1].");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // (d) Search and Modify Hostel Details (Room Number)
    private static void searchAndModifyRoom(Connection conn, Scanner scanner) {
        System.out.print("Enter Student ID: ");
        int studentId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Enter New Room Number: ");
        String newRoom = scanner.nextLine();

        String selectSql = "SELECT * FROM HostelStudent WHERE student_id = ?";
        String updateSql = "UPDATE HostelStudent SET room_number = ? WHERE student_id = ?";

        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            selectStmt.setInt(1, studentId);
            ResultSet rs = selectStmt.executeQuery(); // Standard syntax: selectStmt.executeQuery();

            if (rs.next()) {
                // Update room
                updateStmt.setString(1, newRoom);
                updateStmt.setInt(2, studentId);
                updateStmt.executeUpdate();

                System.out.println("\nRoom updated successfully! Updated Student Details:");
                System.out.println("ID: " + rs.getInt("student_id"));
                System.out.println("Name: " + rs.getString("student_name"));
                System.out.println("Roll Number: " + rs.getString("roll_number"));
                System.out.println("Hostel Name: " + rs.getString("hostel_name"));
                System.out.println("Updated Room Number: " + newRoom);
                System.out.println("Fee: ₹" + rs.getDouble("fee"));
            } else {
                System.out.println("Error: Student record does not exist.");
            }
        } catch (SQLException e) {
            // Fallback for clean execution if needed
            e.printStackTrace();
        }
    }

    // (e) Aggregate Query (SUM) and Conditional Deletion
    private static void aggregateAndDelete(Connection conn, Scanner scanner) {
        String aggSql = "SELECT SUM(fee) AS total_fee FROM HostelStudent";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(aggSql)) {
            if (rs.next()) {
                System.out.println("\nTotal Hostel Fee Collected: ₹" + rs.getDouble("total_fee"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.print("Enter Student ID to delete record: ");
        int studentId = scanner.nextInt();

        String checkSql = "SELECT fee FROM HostelStudent WHERE student_id = ?";
        String deleteSql = "DELETE FROM HostelStudent WHERE student_id = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {

            checkStmt.setInt(1, studentId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                double fee = rs.getDouble("fee");
                if (fee > 0) { // Fully paid condition check
                    deleteStmt.setInt(1, studentId);
                    deleteStmt.executeUpdate();
                    System.out.println("Student record deleted successfully (fee was fully paid > 0)");
                } else {
                    System.out.println("Error: Record cannot be deleted as the fee condition is not met (fee is 0 or negative)[cite: 1].");
                }
            } else {
                System.out.println("Error: Student record does not exist[cite: 1].");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}