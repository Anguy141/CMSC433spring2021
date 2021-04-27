package tests;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;

public class MyFileInputReader {
	private Map<String, Integer> orderSummary;
	private Map<String, Double> itemsPrices;
	private Map<String, HashMap<String, Integer>> clientOrders;
	private StringBuilder str;
	
	/*------------------------------------------------Constructor------------------------------------------------*/
	public MyFileInputReader() {
		itemsPrices = new HashMap<String, Double>();
		clientOrders = new HashMap<String, HashMap<String, Integer>>();
		orderSummary = new HashMap<String, Integer>();
		str = new StringBuilder();
	}
	
	MyFileOutputWriter mfow = new MyFileOutputWriter();
	
	/*----------------------------------------------Reads item data----------------------------------------------*/
	public synchronized void readItemData(String filename) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				String[] splitted = line.split("\\s+");
				String itemName = splitted[0].trim();		
                Double itemPrice = Double.parseDouble(splitted[1].trim());
                
                itemsPrices.put(itemName, itemPrice);
        		orderSummary.put(itemName, 0);    		
			}
		} catch(Exception e) {
            e.printStackTrace();
        }
	}
	
	/*--------------------------------------------Reads client's order-------------------------------------------*/
	public synchronized void readOrder(String filename) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line;
			
			line = br.readLine();
			String[] firstSplitted = line.split("\\s+");
			String clientId = firstSplitted[1].trim();
			
			clientOrders.put(clientId, null);
			System.out.println("Reading order for client with id: " + clientId);
			
			while ((line = br.readLine()) != null) {
				String[] splitted = line.split("\\s+");
				String itemName = splitted[0].trim();
				
				HashMap<String, Integer> order = clientOrders.get(clientId);
				
				if (order == null) {		
					HashMap<String, Integer> inner = new HashMap<String, Integer>();
					inner.put(itemName, 1);
					clientOrders.put(clientId, inner);	
				} else if (order.containsKey(itemName)) {
					Integer itemQuantity = clientOrders.get(clientId).get(itemName);
					clientOrders.get(clientId).put(itemName, ++itemQuantity);				
				} else {
					clientOrders.get(clientId).put(itemName, 1);
				}
			}
			/*-------------------------------------Done reading client's order-----------------------------------*/
			
			/*-----------------------------------Start of client's order summary---------------------------------*/
			Double totalOrderCost = 0.0;
			ArrayList<String> order = new ArrayList<String>();
			HashMap<String, Integer> currOrder = clientOrders.get(clientId);
			
			str.append("----- Order details for client with Id: " + clientId + " -----" + "\n");
			
			for (String currItem : currOrder.keySet()) {

				Double costPerItem = itemsPrices.get(currItem);
				String costPerItemString = NumberFormat.getCurrencyInstance().format(costPerItem);
				int quantity = currOrder.get(currItem);
				String cost = NumberFormat.getCurrencyInstance().format(costPerItem * quantity);
				Double costDouble = (costPerItem * quantity);

				order.add("Item's name: " + currItem + ", Cost per item: " + costPerItemString + ", Quantity: "
						+ quantity + ", Cost: " + cost + "\n");

				int itemCount = orderSummary.get(currItem);
				orderSummary.put(currItem, itemCount + quantity);
				totalOrderCost += costDouble;
			}

			Collections.sort(order);

			for (String currLine : order) {
				str.append(currLine);
			}

			str.append("Order Total: " + NumberFormat.getCurrencyInstance().format(totalOrderCost) + "\n");
			
		} catch(Exception e) {
            e.printStackTrace();
        }
	}
	
	/*-------------------------------------------Gets map of items prices----------------------------------------*/
	public synchronized Map<String, Double> getItemPrices(){
		return itemsPrices;
	}

	/*--------------------------------------Gets map of map of client's order------------------------------------*/
	public synchronized Map<String, HashMap<String, Integer>> getClientOrder(){
		return clientOrders;
	}
	
	/*---------------------------------------------Gets Stringbuilder--------------------------------------------*/
	public synchronized StringBuilder getStringBuilder(){
		return str;
	}
	
	/*---------------------------------------Gets map of total order summary-------------------------------------*/
	public synchronized Map<String, Integer>getOrderSummary(){
		return orderSummary;
	}
}
