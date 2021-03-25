package cmsc433;

import java.util.*;

import cmsc433.Machines.MachineType;

// checks to see if the orders are completed or not
// also keep track of machine
public class Kitchen {

	final static boolean DONE = true;
	final static boolean NOT_DONE = false;
	public static Object KitchenLock;
	private static List<Integer> completedOrders;
	private static Machines fryer;
	private static Machines oven;
	private static Machines grill;
	private static Machines soda;

	/* constructs the kitchen with list of completed orders and building machines */
	public Kitchen(int cap) {
		completedOrders = new ArrayList<Integer>();
		KitchenLock = new Object();
		fryer = new Machines(MachineType.fryers, FoodType.fries, cap);
		oven = new Machines(MachineType.ovens, FoodType.pizza, cap);
		grill = new Machines(MachineType.grillPresses, FoodType.subs, cap);
		soda = new Machines(MachineType.sodaMachines, FoodType.soda, cap);
	}

	/* gets the appropriate machine for the appropriate food */
	public synchronized static Machines getMachine(Food food) throws InterruptedException {
		if (food.equals(FoodType.fries)) {
			return fryer;
		} else if (food.equals(FoodType.pizza)) {
			return oven;
		} else if (food.equals(FoodType.subs)) {
			return grill;
		} else if (food.equals(FoodType.soda)) {
			return soda;
		} else {
			throw new InterruptedException();
		}
	}

	/* shuts down all machines */
	public static void shutdownMachines() {
		Simulation.logEvent(SimulationEvent.machinesEnding(fryer));
		Simulation.logEvent(SimulationEvent.machinesEnding(oven));
		Simulation.logEvent(SimulationEvent.machinesEnding(grill));
		Simulation.logEvent(SimulationEvent.machinesEnding(soda));
	}

	/* completed orders go to completed orders list */
	public synchronized static void completeOrder(int orderNum) {
		completedOrders.add(orderNum);
	}

	/* check to see if order is completed */
	public synchronized static boolean orderStatus(int orderNum) {
		return completedOrders.contains(orderNum) ? DONE : NOT_DONE;
	}

	public static int getNumOfCompletedOrder() {
		return completedOrders.size();
	}

}
