package hokumei.sys.brackeet.service;

import hokumei.sys.brackeet.entity.Order;
import hokumei.sys.brackeet.entity.User;

public interface EmailService {

    void sendMerchantOrderNotification(Order order, User customer);

    void sendCustomerOrderConfirmation(Order order, User customer);
}
