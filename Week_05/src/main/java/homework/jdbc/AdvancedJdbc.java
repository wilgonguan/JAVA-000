package homework.jdbc;

import java.sql.*;

/**
 * @author Levi
 * @date 2020/11/16 18:58
 */
public class AdvancedJdbc {
    private static final String JDBC_URL = "jdbc:h2:~/test";
    private static final String USER = "sa";
    private static final String PASSWORD = "sa";
    private static final String DRIVER_CLASS = "org.h2.Driver";

    public static void main(String[] args) throws Exception {
        Class.forName(DRIVER_CLASS);
        Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        String sql = "DROP TABLE IF EXISTS STUDENT;" + "\n" +
                "CREATE TABLE STUDENT(id INT PRIMARY KEY,name VARCHAR(100))";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.execute();

        sql = "INSERT INTO STUDENT VALUES(?, ?)";
        preparedStatement = conn.prepareStatement(sql);
        String[] names = {"张三", "李四", "王五"};
        // 增  批处理方式
        for (int i = 0; i < names.length; i++) {
            preparedStatement.setInt(1, i + 1);
            preparedStatement.setString(2, names[i]);
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        // 删
        sql = "DELETE FROM STUDENT WHERE name=?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, "张三");
        preparedStatement.execute();
        // 改  增加事务
        try {
            conn.setAutoCommit(false);
            sql = "UPDATE STUDENT SET name=? WHERE name=?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, "李四2");
            preparedStatement.setString(2, "李四");
            preparedStatement.execute();
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
        }
        // 查
        sql = "SELECT * FROM STUDENT WHERE id=?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, 3);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getString("id") + "," + resultSet.getString("name"));
        }
        // 查数据集
        sql = "SELECT * FROM STUDENT";
        preparedStatement = conn.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getString("id") + "," + rs.getString("name"));
        }
        //释放资源
        preparedStatement.close();
        //关闭连接
        conn.close();
    }
}
