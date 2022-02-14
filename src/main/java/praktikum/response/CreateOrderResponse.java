package praktikum.response;

public class CreateOrderResponse {
    private String name;
    private OrderResponse order;
    private boolean success;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderResponse getOrder() {
        return order;
    }

    public void setOrder(OrderResponse order) {
        this.order = order;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
