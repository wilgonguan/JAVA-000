package my.wilgonguan

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author wilgonguan
 * @date 2020/12/7 7
 */
public class MainApp {

    public static void main(String[] args) throws IOException, SQLException {
        OrderService orderService = new OrderService("application.yml");
        orderService.init();
        orderService.insert();

        orderService.selectAll();

        orderService.delete(32);

        orderService.selectByOrderId(2);
        orderService.selectAll();
        orderService.updatePriceByOrderId(2, 100);
        orderService.selectByOrderId(2);

        orderService.selectAll();

        orderService.cleanup();
    }
}
