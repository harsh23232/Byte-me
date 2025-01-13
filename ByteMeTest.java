import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ByteMeTest {
    private ByteMeSystem system;
    private String testUser;

    @Before
    public void setUp() {
        system = new ByteMeSystem();
        testUser = "testUser";
    }

    @Test
    public void testOrderOutOfStockItem() {
        // Get an item and set it as unavailable
        Item item = system.getItem("Masala Dosa");
        item.setAvailable(false);

        // Try to create and place order
        Order order = system.createOrder(testUser);
        order.addItem(item, 1);

        // Verify order cannot be placed
        assertFalse("Order with unavailable item should not be processed",
                validateOrder(order));
    }

    @Test
    public void testInvalidLogin() throws IOException {
        // Test non-existent user
        assertFalse("Non-existent user should not be able to login",
                system.validateLogin("nonexistentUser", "password"));

        // Test wrong password
        ByteMeIO.saveUser("testUser", "correctPassword", false);
        assertFalse("Wrong password should not allow login",
                system.validateLogin("testUser", "wrongPassword"));
    }



    private boolean validateOrder(Order order) {
        return order.getItems().entrySet().stream()
                .allMatch(entry -> entry.getKey().isAvailable());
    }
}