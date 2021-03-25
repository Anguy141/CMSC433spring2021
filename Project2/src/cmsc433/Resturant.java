package cmsc433;

import java.util.ArrayList;
import java.util.List;

//import java.util.*;

/*This class give information about the capacity and seating availability*/
public class Resturant {

	static class Table { // how many tables the Restaurant has and the availability

		private boolean status;

		/* initialize table to NOT_TAKEN */
		public Table() {
			status = EMPTY;
		}

		/* checks to see if table is taken or not */
		public synchronized boolean getTableStatus() {
			return this.status;
		}

		public synchronized void tableForOne() {
			this.status = TAKEN; // status changes to Taken
		}

		public synchronized void leaving() {
			this.status = EMPTY; // status changes to empty
		}
	} // end of inner class

	final static boolean TAKEN = true;
	final static boolean EMPTY = false;
	private static List<Table> tableList;
	private static int numOfCustomers;
	public static Object ResturantLock; // = new Object();

	/* constructs restaurant with the max number of tables */
	public Resturant(int capacity, int numOfCustomers) {
		Resturant.numOfCustomers = numOfCustomers; // initialize Restaurant numOfCustomers
		tableList = new ArrayList<Table>();
		ResturantLock = new Object();

		for (int i = 0; i < capacity; i++) {
			tableList.add(new Table()); // creates the max cap of tables
		}
	}

	// returns Restaurant capacity
	public synchronized static List<Table> getTables() {
		return tableList;
	}

	// return status of table with param index
	public synchronized static boolean getStatus(int index) {
		return tableList.get(index).getTableStatus();
	}

	// returns true if at least one table is free, false otherwise
	public synchronized static boolean oneFreeTables() {
		for (int i = 0; i < tableList.size(); i++) {
			if (tableList.get(i).getTableStatus() == EMPTY) {
				return true;
			}
		}
		return false;
	}

	// return index of first free table, -1 if all tables are taken
	public synchronized static int firstFreeTables() {
		for (int i = 0; i < tableList.size(); i++) {
			if (tableList.get(i).getTableStatus() == EMPTY) {
				return i;
			}
		}
		return -1;
	}

	// returns number of costumers
	public synchronized static int getNumOfCustomers() {
		return numOfCustomers;
	}


}
