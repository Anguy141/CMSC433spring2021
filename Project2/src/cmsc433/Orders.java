package cmsc433;

import java.util.*;

/*this keeps track of orders sent to cooks*/
public class Orders { // all of the orders sent to the kitchen

	static class Order { // single order and its order number
		private List<Food> order;
		private int orderNum;

		public Order(List<Food> order, int orderNum) {
			this.order = order;
			this.orderNum = orderNum;
		}

		public synchronized List<Food> getOrder() {
			return order;
		}

		public int getOrderNumber() {
			return orderNum;
		}

	} // end of inner class

	private static LinkedList<Order> ordersList;
	public static Object OrderLock; // = new Object();

	public static void Initialize() {
		ordersList = new LinkedList<Order>();
		OrderLock = new Object();
	}

	public synchronized static void placeOrder(List<Food> order, int orderNum) {
		synchronized (OrderLock) {
			ordersList.add(new Order(order, orderNum));
		}
	}

	// returns OrderList
	public synchronized static LinkedList<Order> getOrdersList() {
		synchronized (OrderLock) {
			return ordersList;
		}
	}
}
