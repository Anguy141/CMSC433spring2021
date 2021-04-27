package tests;

import java.util.*;

public class OrdersProcessor {

	public static void main(String[] args) {
		String itemDataFilename;
		String choice;
		String numOfOrders;
		int intOrders;
		String baseFilename;
		String resultFilename;

		Scanner scanner = new Scanner(System.in);

		for (int i = 0; i < 1; i++) {
			/*--------------------------------------------Read inputs--------------------------------------------*/
			System.out.println("Enter item's data file name: ");
			itemDataFilename = scanner.nextLine();

			System.out.println("Enter 'y' for multiple threads, any other character otherwise: ");
			choice = scanner.nextLine();

			System.out.println("Enter number of orders to process: ");
			numOfOrders = scanner.nextLine();
			intOrders = Integer.parseInt(numOfOrders);

			System.out.println("Enter order's base filename: ");
			baseFilename = scanner.nextLine();

			System.out.println("Enter result's filename: ");
			resultFilename = scanner.nextLine();

			/*--------------------------------------------Start timer--------------------------------------------*/
			long startTime = System.currentTimeMillis();

			/*-------------------------------------------Multithreaded-------------------------------------------*/
			if (choice.equals("y")) {
				Thread[] orderThreads = new Thread[intOrders];
				for (int j = 0; j < orderThreads.length; j++) {
					String orderFilename = baseFilename.concat(j + ".txt");
					orderThreads[j] = new MultiThread(itemDataFilename, orderFilename, resultFilename);
					orderThreads[j].start();
				}
				try {
					for (int j = 0; j < orderThreads.length; j++) {
						orderThreads[j].join();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			/*------------------------------------------Singlethreaded------------------------------------------*/
			} else {
				SingleThread single = new SingleThread(itemDataFilename, intOrders, baseFilename, resultFilename);
				single.start();
				try {
					single.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			/*---------------------------------------------End timer---------------------------------------------*/
			long endTime = System.currentTimeMillis();	
			System.out.println("Processing time (msec): " + (endTime - startTime));
			System.out.println("Results can be found in the file: " + resultFilename);
			scanner.close();
		}
	}
}
