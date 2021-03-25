package cmsc433;

import java.util.List;

/**
 * This class represents each of the salient events that occur during the
 * simulation. These events are created by the public factory methods
 * provided by the class. DO NOT CHANGE THIS CLASS.
 */
public class SimulationEvent {
	public enum EventType {
		
		/* General events */
		SimulationStarting,
		SimulationEnded,

		/* Customer events */
		CustomerStarting,
		CustomerEnteredRatsies,
		CustomerPlacedOrder,
		CustomerReceivedOrder,
		CustomerLeavingRatsies,
		
		/* Cook Events */
		CookStarting,
		CookReceivedOrder,
		CookStartedFood,
		CookFinishedFood,
		CookCompletedOrder,
		CookEnding,
		
		/* Machines events */
		MachinesStarting,
		MachinesStartingFood,
		MachinesDoneFood,
		MachinesEnding
		
	};

	public final EventType event;

	/*
	 * Not all of these fields are relevant for every event;
	 * see factory methods below
	 */
	public final Cook cook;
	public final Customer customer;
	public final Machines machines;
	public final Food food;
	public final List<Food> orderFood;
	public final int orderNumber;
	public final int[] simParams;

	private SimulationEvent(EventType event,
		Cook cook,
		Customer customer,
		Machines machines,
		Food food,
		List<Food> orderFood,
		int orderNumber,
		int[] simParams) {

		this.event = event;
		this.cook = cook;
		this.customer = customer;
		this.machines = machines;
		this.food = food;
		this.orderFood = orderFood;
		this.orderNumber = orderNumber;
		this.simParams = simParams;
	}

	/* Factory methods */

	/* Simulation events */
	public static SimulationEvent startSimulation(int numCustomers,
		int numCooks,
		int numTables,
		int count) {
		
		int[] params = new int[4];
		params[0] = numCustomers;
		params[1] = numCooks;
		params[2] = numTables;
		params[3] = count;
		return new SimulationEvent(EventType.SimulationStarting, null, null, null, null, null, 0,
			params);
	}

	public static SimulationEvent endSimulation() {
		return new SimulationEvent(EventType.SimulationEnded, null, null, null, null, null, 0, null);
	}

	/* Customer events */
	public static SimulationEvent customerStarting(Customer customer) {
		return new SimulationEvent(EventType.CustomerStarting, null,
			customer, null, null, null, 0, null);
	}

	public static SimulationEvent customerEnteredRatsies(Customer customer) {
		return new SimulationEvent(EventType.CustomerEnteredRatsies, null,
			customer, null, null, null, 0, null);
	}

	public static SimulationEvent customerPlacedOrder(Customer customer,
		List<Food> order,
		int orderNumber) {
		
		return new SimulationEvent(EventType.CustomerPlacedOrder, null,
			customer, null, null,
			order,
			orderNumber, null);
	}

	public static SimulationEvent customerReceivedOrder(Customer customer,
		List<Food> order,
		int orderNumber) {
		
		return new SimulationEvent(EventType.CustomerReceivedOrder, null,
			customer, null, null,
			order,
			orderNumber, null);
	}

	public static SimulationEvent customerLeavingRatsies(Customer customer) {
		return new SimulationEvent(EventType.CustomerLeavingRatsies, null,
			customer, null, null, null, 0, null);
	}

	/* Cook events */
	public static SimulationEvent cookStarting(Cook cook) {
		return new SimulationEvent(EventType.CookStarting,
			cook, null, null, null, null, 0, null);
	}

	public static SimulationEvent cookReceivedOrder(Cook cook,
		List<Food> order,
		int orderNumber) {
		
		return new SimulationEvent(EventType.CookReceivedOrder,
			cook, null, null, null,
			order, 
			orderNumber, null);
	}

	public static SimulationEvent cookStartedFood(Cook cook, 
		Food food,
		int orderNumber) {
		
		return new SimulationEvent(EventType.CookStartedFood,
			cook, null, null,
			food, null,
			orderNumber, null);
	}

	public static SimulationEvent cookFinishedFood(Cook cook, 
		Food food,
		int orderNumber) {
		
		return new SimulationEvent(EventType.CookFinishedFood,
			cook, null, null,
			food, null,
			orderNumber, null);
	}

	public static SimulationEvent cookCompletedOrder(Cook cook, int orderNumber) {
		return new SimulationEvent(EventType.CookCompletedOrder,
			cook, null, null, null, null,
			orderNumber, null);
	}

	public static SimulationEvent cookEnding(Cook cook) {
		return new SimulationEvent(EventType.CookEnding,
			cook, null, null, null, null, 0, null);
	}

	/* Machines events */
	public static SimulationEvent machinesStarting(Machines machine,
		Food food,
		int count) {
		
		int[] params = new int[1];
		params[0] = count;
		return new SimulationEvent(EventType.MachinesStarting, null, null,
			machine,
			food, null, 0, params);
	}

	public static SimulationEvent machinesCookingFood(Machines machine, Food food) {
		return new SimulationEvent(EventType.MachinesStartingFood, null, null,
			machine,
			food, null, 0, null);
	}

	public static SimulationEvent machinesDoneFood(Machines machine, Food food) {
		return new SimulationEvent(EventType.MachinesDoneFood, null, null,
			machine,
			food, null, 0, null);
	}

	public static SimulationEvent machinesEnding(Machines machine) {
		return new SimulationEvent(EventType.MachinesEnding, null, null,
			machine, null, null, 0, null);
	}

	public String toString() {
		switch (event) {
			/* Simulation events */
			case SimulationStarting:
				int numCustomers = simParams[0];
				int numCooks = simParams[1];
				int numTables = simParams[2];
				int count = simParams[3];
				return "Starting simulation: " + 
					numCustomers + " customers; " +
					numCooks + " cooks; " + 
					numTables + " tables; " +
					"machine count per type " + count + ".";

			case SimulationEnded:
				return "Simulation ended.";

			/* Customer events */
			case CustomerStarting:
				return customer + " going to Ratsies.";

			case CustomerEnteredRatsies:
				return customer + " entered Ratsies.";

			case CustomerPlacedOrder:
				return customer + " placing order " + orderNumber + " " + orderFood;

			case CustomerReceivedOrder:
				return customer + " received order " + orderNumber + " " + orderFood;

			case CustomerLeavingRatsies:
				return customer + " leaving Ratsies.";

			/* Cook Events */
			case CookStarting:
				return cook + " reporting for work.";

			case CookReceivedOrder:
				return cook + " starting order " + orderNumber + " " + orderFood;

			case CookStartedFood:
				return cook + " preparing " + food + " for order " + orderNumber;

			case CookFinishedFood:
				return cook + " finished " + food + " for order " + orderNumber;

			case CookCompletedOrder:
				return cook + " completed order " + orderNumber;

			case CookEnding:
				return cook + " going home for the night.";

			/* Machines events */
			case MachinesStarting:
				return machines + " starting up for making " +
					food + "; count: " + simParams[0] + ".";

			case MachinesStartingFood:
				return machines + " making " + food + ".";

			case MachinesDoneFood:
				return machines + " completed " + food + ".";

			case MachinesEnding:
				return machines + " shutting down.";

			default:
				throw new Error("Illegal event; can't be stringified");
		}
	}
}
