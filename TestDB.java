public class TestDB {
    public static void main(String[] args) {
        if (DBConnection.getConnection() != null) {
            System.out.println("Database connected!");
        } else {
            System.out.println("Connection failed.");
        }
    }
}
