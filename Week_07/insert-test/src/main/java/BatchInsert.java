import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * @author Levi
 * @date 2020/12/1 17:30
 */
public class BatchInsert {

    public static final String JDBC_URL = "jdbc:h2:~/test";
    public static final String USER = "sa";
    public static final String PASSWORD = "sa";
    public static final String DRIVER_CLASS = "org.h2.Driver";

    public static final String CREATE_TABLE = "DROP TABLE IF EXISTS STUDENT;" + "\n" +
            "CREATE TABLE STUDENT(id INT PRIMARY KEY,name VARCHAR(100))";

    public static void main(String[] args) throws Exception {
        batchInsert();
        batchInsert();
        batchInsert();

        batchInsert();

        batchInsert2();

        batchInsert3();

        batchInsert4();
    }

    public static void batchInsert() throws Exception {
        Class.forName(DRIVER_CLASS);
        Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

        String sql = CREATE_TABLE;
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.execute();

        sql = "INSERT INTO STUDENT VALUES(?, ?)";
        long start = System.currentTimeMillis();
        preparedStatement = conn.prepareStatement(sql);
        for (int i = 0; i < 1000000; i++) {
            preparedStatement.setInt(1, i + 1);
            preparedStatement.setString(2, "张三");
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        long end = System.currentTimeMillis();
        System.out.println(end - start);

        preparedStatement.close();
        conn.close();
    }

    public static void batchInsert2() throws Exception {
        Class.forName(DRIVER_CLASS);
        Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

        String sql = CREATE_TABLE;
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.execute();

        sql = "INSERT INTO STUDENT VALUES(?, ?)";
        long start = System.currentTimeMillis();
        preparedStatement = conn.prepareStatement(sql);
        for (int i = 0; i < 100000; i++) {
            for (int j = 0; j < 10; j++) {
                preparedStatement.setInt(1, i * 10 + j);
                preparedStatement.setString(2, "张三");
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);

        preparedStatement.close();
        conn.close();
    }

    public static void batchInsert3() throws Exception {
        Class.forName(DRIVER_CLASS);
        Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

        String sql = CREATE_TABLE;
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.execute();

        sql = "INSERT INTO STUDENT VALUES(?, ?), (?, ?)";
        long start = System.currentTimeMillis();
        preparedStatement = conn.prepareStatement(sql);
        for (int i = 0; i < 500000; i++) {
            preparedStatement.setInt(1, i * 2);
            preparedStatement.setString(2, "张三");
            preparedStatement.setInt(3, i * 2 + 1);
            preparedStatement.setString(4, "张三");
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);

        preparedStatement.close();
        conn.close();
    }

    public static void batchInsert4() throws Exception {
        Class.forName(DRIVER_CLASS);
        Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

        String sql = CREATE_TABLE;
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.execute();

        sql = "INSERT INTO STUDENT VALUES(?, ?), (?, ?), (?, ?), (?, ?), (?, ?), (?, ?), (?, ?), (?, ?), (?, ?), (?, ?)";
        long start = System.currentTimeMillis();
        preparedStatement = conn.prepareStatement(sql);
        for (int i = 0; i < 100000; i++) {
            for (int j = 0; j < 10; j++) {
                preparedStatement.setInt(j * 2 + 1, i * 10 + j);
                preparedStatement.setString(j * 2 + 2, "张三");
            }
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);

        preparedStatement.close();
        conn.close();
    }
}
