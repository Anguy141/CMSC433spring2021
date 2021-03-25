package tests;

import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.junit.Test;
import cmsc433.Simulation;

public class StudentTests {
	@Test
	public void test1_1_1_1() {
		ByteArrayOutputStream myOutput = new ByteArrayOutputStream();
		PrintStream errorStream = new PrintStream(myOutput);
		System.setErr(errorStream);
		int numCustomers = 1;
		int numCooks = 1;
		int numTables = 1;
		int machineCount = 1;
		boolean randomOrders = false;
		try {
			List<cmsc433.SimulationEvent> eventsResult = Simulation.runSimulation(numCustomers, numCooks, numTables,
					machineCount, randomOrders);
			boolean result = GraderValidate.validateSimulation(eventsResult);
			assertTrue(result);
		} catch (Exception e) {
			assertTrue(false);
		}
		assertFalse(errorStream.checkError());
	}
}