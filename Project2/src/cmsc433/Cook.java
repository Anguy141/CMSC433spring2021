package cmsc433;

import cmsc433.Orders.Order;
import java.util.*;

/**
 * Cooks are simulation actors that have at least one field, a name. When
 * running, a cook attempts to retrieve outstanding orders placed by Customer
 * and process them.
 */
public class Cook implements Runnable {
	private final String name;

	/**
	 * You can feel free modify this constructor. It must take at least the name,
	 * but may take other parameters if you would find adding them useful.
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows. The cook tries to retrieve orders placed by
	 * Customers. For each order, a List<Food>, the cook submits each Food item in
	 * the List to an appropriate Machine type, by calling makeFood(). Once all
	 * machines have produced the desired Food, the order is complete, and the
	 * Customer is notified. The cook can then go to process the next order. If
	 * during its execution the cook is interrupted (i.e., some other thread calls
	 * the interrupt() method on it, which could raise InterruptedException if the
	 * cook is blocking), then it terminates.
	 */
	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while (true) {
				// YOUR CODE GOES HERE..

				HashMap<Integer, HashMap<Food, ArrayList<Thread>>> foodOrder = new HashMap<>();
				Order currOrder;
				int currOrderNum;

				synchronized (Orders.OrderLock) {

					if (Kitchen.getNumOfCompletedOrder() >= Resturant.getNumOfCustomers()) {
						throw new InterruptedException();
					}

					while (Orders.getOrdersList().isEmpty()
							&& Kitchen.getNumOfCompletedOrder() < Resturant.getNumOfCustomers()) {
						Orders.OrderLock.wait();
					}

					currOrder = Orders.getOrdersList().poll();

					Simulation.logEvent(
							SimulationEvent.cookReceivedOrder(this, currOrder.getOrder(), currOrder.getOrderNumber()));
					currOrderNum = currOrder.getOrderNumber(); // gets currOrderNum
					Orders.OrderLock.notifyAll();
				}

				synchronized (Machines.MachineLock) {
					for (Food food : currOrder.getOrder()) {
						Machines currMachine = Kitchen.getMachine(food); // gets machine for appropriate food
						Simulation.logEvent(SimulationEvent.cookStartedFood(this, food, currOrderNum));
						Thread t = (Thread) currMachine.makeFood(food); // gets the thread of food

						Machines.MachineLock.wait(); // waits for the machine to cook the food

						if (!foodOrder.containsKey(currOrderNum)) {
							HashMap<Food, ArrayList<Thread>> inner = new HashMap<>();
							foodOrder.put(currOrderNum, inner);
						}

						if (!foodOrder.get(currOrderNum).containsKey(food)) {
							foodOrder.get(currOrderNum).put(food, new ArrayList<Thread>());
							foodOrder.get(currOrderNum).get(food).add(t);
						} else {
							foodOrder.get(currOrderNum).get(food).add(t); // adds thread to thread order
						}
						Machines.MachineLock.notifyAll();
					}
				}

				synchronized (Kitchen.KitchenLock) {
					for (Food food : foodOrder.get(currOrderNum).keySet()) {
						for (Thread t : foodOrder.get(currOrderNum).get(food)) {
							t.join();
						}
						Simulation.logEvent(SimulationEvent.cookFinishedFood(this, food, currOrderNum));
					}
					Kitchen.completeOrder(currOrderNum);
					Kitchen.KitchenLock.notifyAll();
					Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, currOrderNum));
				}

				// throw new InterruptedException(); // REMOVE THIS
			}
		} catch (InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Kitchen.shutdownMachines();
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}
