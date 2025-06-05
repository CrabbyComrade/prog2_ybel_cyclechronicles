package cyclechronicles;

/** An order for a bike shop. */
public record Order(String customer, Type bicycleType) {
    public Order {
        if (customer == null) { throw new IllegalArgumentException("customer cannot be null!"); }
        if (bicycleType == null) { throw new IllegalArgumentException("bycycle type cannot be null!"); }
    }
    public Type getBicycleType() {
        return bicycleType;
    }

    /**
     * Determine the customer who placed this order.
     *
     * @return name of customer
     */
    public String getCustomer() {
        return customer;
    }
}
