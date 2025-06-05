package cyclechronicles;

import org.mockito.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Stream;

public class ShopTest {

    @Mock
    private Order orderMock;

    private Shop shop;

    @BeforeEach
    void setUp() {
        orderMock = mock(Order.class);
        shop = new Shop();
    }

    @Test
    void testValidOrder() {
        when(orderMock.getBicycleType()).thenReturn(Type.RACE);
        when(orderMock.getCustomer()).thenReturn("Kunde");

        assertTrue(shop.accept(orderMock), "Bestellung sollte akzeptiert werden");
    }

    @Test
    void testInvalidType() {
        when(orderMock.getBicycleType()).thenReturn(Type.EBIKE);
        when(orderMock.getCustomer()).thenReturn("Kunde");

        assertFalse(shop.accept(orderMock), "Bestellung mit E-Bike sollte nicht akzeptiert werden");
    }

    @Test
    void testCustomerWithActiveOrder() {
        when(orderMock.getBicycleType()).thenReturn(Type.RACE);
        when(orderMock.getCustomer()).thenReturn("Kunde");

        shop.accept(orderMock); // Valid Order
        assertFalse(shop.accept(orderMock), "Bestellung mit E-Bike sollte nicht akzeptiert werden"); // Second order from same customer
    }

    @Test
    void testPendingOrdersTooHigh() {
        // Five previous orders
        Order existingOrder1 = mock(Order.class);
        when(existingOrder1.getCustomer()).thenReturn("Kunde1");
        assertTrue(shop.accept(existingOrder1));
        Order existingOrder2 = mock(Order.class);
        when(existingOrder2.getCustomer()).thenReturn("Kunde2");
        assertTrue(shop.accept(existingOrder2));
        Order existingOrder3 = mock(Order.class);
        when(existingOrder3.getCustomer()).thenReturn("Kunde3");
        assertTrue(shop.accept(existingOrder3));
        Order existingOrder4 = mock(Order.class);
        when(existingOrder4.getCustomer()).thenReturn("Kunde4");
        assertTrue(shop.accept(existingOrder4));
        Order existingOrder5 = mock(Order.class);
        when(existingOrder5.getCustomer()).thenReturn("Kunde5");
        assertTrue(shop.accept(existingOrder5));

        // New order
        when(orderMock.getBicycleType()).thenReturn(Type.RACE);
        when(orderMock.getCustomer()).thenReturn("Kunde");

        assertFalse(shop.accept(orderMock), "Bestellung mit E-Bike sollte nicht akzeptiert werden");
    }

    @Test
    void testValidWithMaxOfActiveOrders() {
        // Five previous orders
        Order existingOrder1 = mock(Order.class);
        when(existingOrder1.getCustomer()).thenReturn("Kunde1");
        assertTrue(shop.accept(existingOrder1));
        Order existingOrder2 = mock(Order.class);
        when(existingOrder2.getCustomer()).thenReturn("Kunde2");
        assertTrue(shop.accept(existingOrder2));
        Order existingOrder3 = mock(Order.class);
        when(existingOrder3.getCustomer()).thenReturn("Kunde3");
        assertTrue(shop.accept(existingOrder3));
        Order existingOrder4 = mock(Order.class);
        when(existingOrder4.getCustomer()).thenReturn("Kunde4");
        assertTrue(shop.accept(existingOrder4));

        // New order
        when(orderMock.getBicycleType()).thenReturn(Type.RACE);
        when(orderMock.getCustomer()).thenReturn("Kunde");

        assertTrue(shop.accept(orderMock), "Bestellung mit E-Bike sollte nicht akzeptiert werden");
    }

    // Tests for Task 2

    @Test
    void testRepair() {
        Shop shopMock = mock(Shop.class);
        Queue<Order> mockedPendingOrders = mock(Queue.class);
        Set<Order> mockedCompletedOrders = mock(Set.class);

        when(mockedPendingOrders.poll()).thenReturn(orderMock); // Return our orderMock when trying to take oldest order

        doAnswer(x -> { // simulated behaviour of repair()
            Order orderToRepair = mockedPendingOrders.poll(); // Retrieve and remove oldest element
            mockedCompletedOrders.add(orderToRepair);  // put element into completedorders
            return Optional.of(orderToRepair);
        }).when(shopMock).repair();

        Optional<Order> repairedOrder = shopMock.repair(); // Do mock repair
        assertEquals(orderMock, repairedOrder.get()); // Checks if repair was successfull

        verify(mockedCompletedOrders).add(orderMock); // Check if orderMock has been added to the completed orders
    }

    @Test
    void testDeliver() {
        // Mocked orders
        Order existingOrder1 = mock(Order.class);
        when(existingOrder1.getCustomer()).thenReturn("Kunde1");
        Order existingOrder2 = mock(Order.class);
        when(existingOrder2.getCustomer()).thenReturn("Kunde2");
        Order existingOrder3 = mock(Order.class);
        when(existingOrder3.getCustomer()).thenReturn("Kunde3");

         // Mock a real Set of orders
        List mockedCompletedOrders = new ArrayList<>(); // probably bad variable name bcz it's not a mock
        mockedCompletedOrders.add(existingOrder1);
        mockedCompletedOrders.add(existingOrder2);
        mockedCompletedOrders.add(existingOrder3);

        Shop spyShop = spy(shop);
        doAnswer(x -> {
            String customer = x.getArgument(0);  // Get customer from given String
            Iterator<Order> iterator = mockedCompletedOrders.iterator(); // Make iterator for mocked orders
            while (iterator.hasNext()) {
                Order order = iterator.next();
                if (order.getCustomer().equals(customer)) { // Check for whose order should be delivered
                    iterator.remove(); // Remove order from mockedCompletedOrders
                    return Optional.of(order);  // Returns finished order
                }
            }
            return Optional.empty();  // returns empty optional if no orders are found
        }).when(spyShop).deliver(anyString());

        Optional<Order> deliveredOrder = spyShop.deliver(existingOrder2.getCustomer()); // Do mock deliver()
        assertEquals(existingOrder2, deliveredOrder.get()); // Check right order has been delivered

        assertTrue(mockedCompletedOrders.size()==2, "Es sollte zwei copmleted orders nach deliver() geben."); // Check if delivered order has been removed

        // Test for nonexistent customer
        deliveredOrder = spyShop.deliver("X");
        assertFalse(deliveredOrder.isPresent(), "Deliver sollte empty zurückgeben");
    }
}