package cyclechronicles;

import org.mockito.*;
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
        MockitoAnnotations.openMocks(this);
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
}
