package praktikum.response;

import java.util.List;

public class UserOrdersResponse {
    private boolean success;
    private List<OrderList> orders;
    private int total;
    private int totalToday;

    public boolean isSuccess() {
        return success;
    }

    public List<OrderList> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderList> orders) {
        this.orders = orders;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalToday() {
        return totalToday;
    }

    public void setTotalToday(int totalToday) {
        this.totalToday = totalToday;
    }
}
