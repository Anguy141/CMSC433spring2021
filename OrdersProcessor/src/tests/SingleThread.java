package tests;

import java.text.*;
import java.util.*;

public class SingleThread extends Thread {
	private Map<String, Integer> orderSummary;
	private StringBuilder str;
	private String itemDataFilename;
	private String baseFilename;
	private String resultFilename;
	private double summaryCost;
	private int numOfOrders;

	public SingleThread(String itemDataFilename, int numOfOrders, String baseFilename, String resultFilename) {
		this.itemDataFilename = itemDataFilename;
		this.numOfOrders = numOfOrders;
		this.baseFilename = baseFilename;
		this.resultFilename = resultFilename;
		orderSummary = new HashMap<String, Integer>();
		str = new StringBuilder();
		summaryCost = 0;
	}

	MyFileInputReader mfir = new MyFileInputReader();
	MyFileOutputWriter mfow = new MyFileOutputWriter();

	@Override
	public void run() {
		mfir.readItemData(itemDataFilename);

		Map<String, Double> itemsPrices = mfir.getItemPrices();

		for (int j = 1; j <= numOfOrders; j++) {
			mfir.readOrder(baseFilename.concat(j + ".txt"));
		}
		
		str.append(mfir.getStringBuilder());
		
		str.append("***** Summary of all orders *****" + "\n");
		
		orderSummary = mfir.getOrderSummary();	
		ArrayList<String> orderSum = new ArrayList<String>();
		
		for (String currItem : orderSummary.keySet()) {
			Double costPerItem = itemsPrices.get(currItem);
			String costPerItemString = NumberFormat.getCurrencyInstance().format(costPerItem);
			int quantity = orderSummary.get(currItem);
			Double costDouble = (costPerItem * quantity);
			String cost = NumberFormat.getCurrencyInstance().format(costPerItem * quantity);

			orderSum.add("Summary - Item's name: " + currItem + ", Cost per item: " + costPerItemString
					+ ", Number sold: " + quantity + ", Item's Total: " + cost + "\n");
			summaryCost += costDouble;
		}
		
		Collections.sort(orderSum);

		for (String currLine : orderSum) {
			str.append(currLine);
		}
		
		str.append("Summary Grand Total: " + NumberFormat.getCurrencyInstance().format(summaryCost)+ "\n");

		mfow.writeToFile(resultFilename, str);
	}

}
