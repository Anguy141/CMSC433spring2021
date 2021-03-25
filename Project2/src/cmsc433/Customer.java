package cmsc433;

import java.util.List;

import cmsc433.Resturant.Table;

/**
 * Customers are simulation actors that have two fields: a name, and a list of
 * Food items that constitute the Customer's order. When running, an customer
 * attempts to enter the Ratsie's (only successful if the Ratsie's has a free
 * table), place its order, and then leave the Ratsie's when the order is
 * complete.
 */
public class Customer implements Runnable {
	// JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;
	// private final int orderNum;
	private Table currTable;
	private static Object CustomerLock;// = new Object();

	private static int runningCounter = 0;

	/**
	 * You can feel free modify this constructor. It must take at least the name and
	 * order but may take other parameters if you would find adding them useful.
	 */
	public Customer(String name, List<Food> order) {
		this.name = name;
		this.order = order;
		// this.orderNum = ++runningCounter;
		CustomerLock = new Object();
	}

	public String toString() {
		return name;
	}

	/**
	 * This method defines what an Customer does: The customer attempts to enter the
	 * Ratsie's (only successful when the Ratsie's has a free table), place its
	 * order, and then leave the Ratsie's when the order is complete.
	 */
	public void run() {
		// YOUR CODE GOES HERE...
		Simulation.logEvent(SimulationEvent.customerStarting(this));

		synchronized (CustomerLock) {
			try {
				while (!Resturant.oneFreeTables()) { // no room in restaurant
					CustomerLock.wait(); // customer has to wait
				}
				int tableIndex = Resturant.firstFreeTables(); // saves index of free table
				Resturant.getTables().get(tableIndex).tableForOne();// sits down at free table
				currTable = Resturant.getTables().get(tableIndex); // saves current table

			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		Simulation.logEvent(SimulationEvent.customerEnteredRatsies(this));
		int orderNum = ++runningCounter;

		synchronized (Orders.OrderLock) {
			Orders.placeOrder(order, orderNum); // places order
			Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, order, orderNum));
			Orders.OrderLock.notifyAll();
		}

		synchronized (Kitchen.KitchenLock) {
			while (!Kitchen.orderStatus(orderNum)) { // checks orderStatus
				try {
					Kitchen.KitchenLock.wait(); // if order is not ready, it waits
				} catch (InterruptedException e) {
					continue;
				}
			}
			Kitchen.KitchenLock.notifyAll();
		}

		Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, order, orderNum));
		Simulation.logEvent(SimulationEvent.customerLeavingRatsies(this));

		synchronized (CustomerLock) {
			currTable.leaving(); // leaves the table
			CustomerLock.notifyAll(); // notify that table is free
		}

	}
}
