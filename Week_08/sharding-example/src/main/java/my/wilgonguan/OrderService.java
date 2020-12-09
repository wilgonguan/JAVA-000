package my.wilgonguan;

import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Random;

/**
 * @author wilgonguan
 * @date 2020/12/7
 */
public class OrderService {

    private final DataSource dataSource;

    OrderService(final String yamlConfigFile) throws IOException, SQLException {
        dataSource = YamlShardingSphereDataSourceFactory.createDataSource(getFile(yamlConfigFile));
    }

    private File getFile(final String fileName) {
        return new File(OrderService.class.getClassLoader().getResource(fileName).getFile());
    }

    public void init() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS t_order");
            statement.execute("CREATE TABLE `t_order` (\n" +
                    "  `order_id` bigint(20) NOT NULL,\n" +
                    "  `seller_id` bigint(20) NOT NULL COMMENT '卖家id',\n" +
                    "  `buyer_id` bigint(20) NOT NULL COMMENT '买家id',\n" +
                    "  `status` tinyint(4) NOT NULL COMMENT '订单状态 0未付款,1已付款,2已发货,3已签收,4退货申请,5退货中,6已退货,7取消交易,8已删除',\n" +
                    "  `create_at` bigint(20) NOT NULL COMMENT '订单创建时间',\n" +
                    "  `update_at` bigint(20) NOT NULL COMMENT '订单修改时间',\n" +
                    "  `remark` varchar(200) DEFAULT '' COMMENT '备注',\n" +
                    "  `total_price` bigint(20) NOT NULL COMMENT '订单总价',\n" +
                    "  `shipping` int(11) COMMENT '运费',\n" +
                    "  `receiver_address` varchar(100) COMMENT '收货地址',\n" +
                    "  `receiver_post_code` varchar(10) COMMENT '邮编',\n" +
                    "  `receiver_name` varchar(50) COMMENT '收货人名字',\n" +
                    "  `receiver_phone` varchar(20) COMMENT '收货人手机号',\n" +
                    "  PRIMARY KEY (`order_id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
        }
    }

    public void cleanup() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS t_order");
        }
    }

    public void insert() throws SQLException {
        Random random = new Random();
        try (Connection connection = dataSource.getConnection()) {
            int count = 0;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 16; j++) {
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO t_order (order_id, seller_id, buyer_id, status, create_at, update_at, total_price) VALUES (?, ?, ?, ?, ?, ?, ?)");
                    preparedStatement.setLong(1, ++count);
                    preparedStatement.setLong(2, random.nextInt(100));
                    preparedStatement.setLong(3, i + 1);
                    preparedStatement.setInt(4, random.nextInt(9));
                    preparedStatement.setLong(5, System.currentTimeMillis());
                    preparedStatement.setLong(6, System.currentTimeMillis());
                    preparedStatement.setLong(7, random.nextInt(10000));
                    preparedStatement.executeUpdate();
                }
            }
        }
    }

    public void delete(long orderId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM t_order WHERE order_id=?")) {
            preparedStatement.setLong(1, orderId);
            preparedStatement.executeUpdate();
        }
    }

    public void updatePriceByOrderId(long orderId, long totalPrice) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE t_order SET total_price=? WHERE order_id=?")) {
            preparedStatement.setLong(1, totalPrice);
            preparedStatement.setLong(2, orderId);
            preparedStatement.executeUpdate();
        }
    }

    public void selectByOrderId(long orderId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT order_id, seller_id, buyer_id, status, create_at, update_at, total_price FROM t_order WHERE order_id=?")) {
            preparedStatement.setLong(1, orderId);
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                System.out.println("order_id:" + resultSet.getLong(1));
                System.out.println("seller_id:" + resultSet.getLong(2));
                System.out.println("buyer_id:" + resultSet.getLong(3));
                System.out.println("status:" + resultSet.getLong(4));
                System.out.println("create_at:" + resultSet.getLong(5));
                System.out.println("update_at:" + resultSet.getLong(6));
                System.out.println("total_price:" + resultSet.getLong(7));
            }
        }
    }

    public int selectAll() throws SQLException {
        int result = 0;
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeQuery("SELECT COUNT(1) AS count FROM t_order");
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        }
        System.out.println(result);
        return result;
    }

}
