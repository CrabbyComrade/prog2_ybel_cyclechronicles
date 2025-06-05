package cyclechronicles;

import java.io.IOException;
import java.util.*;
import java.util.logging.Formatter;
import java.util.logging.*;
import java.io.*;

/** A small bike shop. */
public class Shop {
    private final Queue<Order> pendingOrders = new LinkedList<>();
    private final Set<Order> completedOrders = new HashSet<>();

    public static final Logger logger = Logger.getLogger(Shop.class.getName()); // create logger
    private static final String log_file = "_contact_log.csv"; // specify log output
    static{
        try {
            FileHandler fileHandler = new FileHandler(log_file, true);
            fileHandler.setFormatter(new CSVFormatter());
            logger.addHandler(fileHandler);

            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Fehler beim Initialisieren des Loggers", e);
        }
    }
    public static class CSVFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return String.format("%s; %s; %s; %s\n",
                    record.getLevel(),          
                    record.getSourceMethodName(),  
                    record.getSourceClassName(),    
                    record.getMessage()    
            );
        }
    }

    /**
     * Accept a repair order.
     *
     * <p>The order will only be accepted if all conditions are met:
     *
     * <ul>
     *   <li>Gravel bikes cannot be repaired in this shop.
     *   <li>E-bikes cannot be repaired in this shop.
     *   <li>There can be no more than one pending order per customer.
     *   <li>There can be no more than five pending orders at any time.
     * </ul>
     *
     * <p>Implementation note: Accepted orders are added to the end of {@code pendingOrders}.
     *
     * @param o order to be accepted
     * @return {@code true} if all conditions are met and the order has been accepted, {@code false}
     *     otherwise
     */
    public boolean accept(Order o) {
        if (o.getBicycleType() == Type.GRAVEL) return false;
        if (o.getBicycleType() == Type.EBIKE) return false;
        if (pendingOrders.stream().anyMatch(x -> x.getCustomer().equals(o.getCustomer())))
            return false;
        if (pendingOrders.size() > 4) return false;

        return pendingOrders.add(o);
    }

    /**
     * Take the oldest pending order and repair this bike.
     *
     * <p>Implementation note: Take the top element from {@code pendingOrders}, "repair" the bicycle
     * and put this order in {@code completedOrders}.
     *
     * @return finished order
     */
    public Optional<Order> repair() {
            Order orderToRepair = pendingOrders.poll(); // Retrieve and remove oldest element
            logger.info("removed order :"+ orderToRepair.getCustomer()+orderToRepair.getBicycleType()+"from pendingOrders");
            completedOrders.add(orderToRepair);  // put element into completedorders
            logger.info("added order :"+ orderToRepair.getCustomer()+orderToRepair.getBicycleType()+"to completedOrders");
            return Optional.of(orderToRepair);
    }

    /**
     * Deliver a repaired bike to a customer.
     *
     * <p>Implementation note: Find any order in {@code completedOrders} with matching customer and
     * deliver this order. Will remove the order from {@code completedOrders}.
     *
     * @param c search for any completed orders of this customer
     * @return any finished order for given customer, {@code Optional.empty()} if none found
     */
    public Optional<Order> deliver(String c) {
            Iterator<Order> iterator = completedOrders.iterator(); // Make iterator for orders
            while (iterator.hasNext()) {
                Order order = iterator.next();
                if (order.getCustomer().equals(c)) { // Check for whose order should be delivered
                    iterator.remove(); // Remove order from CompletedOrders
                    logger.info("removed order :"+ order.getCustomer()+order.getBicycleType()+"from completedOrders");
                    return Optional.of(order);  // Returns finished order
                }
            }
            return Optional.empty();  // returns empty optional if no orders are found
    }
}
