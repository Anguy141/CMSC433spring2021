package cmsc433.p1;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SingleSimulation {

	public static void main(String[] args) throws InterruptedException, InsufficientFundsException {
		AuctionServer as = AuctionServer.getInstance();
		
		Seller seller = new Seller(
				as, 
        		"Seller", 
        		100, 50, 1);
		
		Seller seller2 = new Seller(
				as, 
        		"Seller2", 
        		100, 50, 1);
		
		Bidder bidder = new ConservativeBidder(
				as, 
        		"Buyer", 
        		100, 20, 150, 1);
		
		Bidder bidder2 = new ConservativeBidder(
				as, 
        		"Buyer2", 
        		100, 20, 150, 1);
		
		as.submitItem(seller.name(), "item0", 2, 1000);
		as.submitItem(seller.name(), "item1", 4, 1000);
		as.submitItem(seller.name(), "item2", 2, 1000);
		as.submitItem(seller.name(), "item3", 2, 1000);
		
		int result = as.submitItem(seller.name(), "item4", 2, 1000);
        System.out.println(result);
        List<Item> lst = as.getItems();
        System.out.println(lst);
        
        //Boolean bidResultBelow = as.submitBid(1, bidder.name(), 3);
        //System.out.println(bidResultBelow);
        //System.out.println("item price "+as.itemPrice(1));
        //Boolean bidResult = as.submitBid(1, bidder.name(), 4);
        //System.out.println(bidResult);
        //System.out.println("item price "+as.itemPrice(1));
        //Boolean bidResult2 = as.submitBid(1, bidder2.name(), 5);
        //System.out.println("item price "+as.itemPrice(1));
        //System.out.println(bidResult2);
        
        //public Item(String seller, String name, int listingID, int lowestBiddingPrice, int biddingDurationMs)
        
        Boolean bidResult3 = as.submitBid(1, bidder.name(), 6);
        System.out.println(bidResult3);
        //System.out.println("item price "+as.itemPrice(1));
        
        //System.out.println("Bid Status");
        //System.out.println(as.checkBidStatus(1, bidder.name()));
        //System.out.println(as.checkBidStatus(1, bidder2.name()));
        //System.out.println(as.checkBidStatus(6, bidder.name()));
        
        TimeUnit.SECONDS.sleep(1);
        int bidStatus = as.checkBidStatus(1, bidder.name());
        System.out.println(bidStatus);
        System.out.println("uncolect: "+as.uncollectedRevenue());
        System.out.println("rev: "+as.revenue());
        List<Item> lst2 = as.getItems();
        System.out.println("List after: "+lst2);
        //System.out.println(as.checkBidStatus(1, bidder2.name()));
        System.out.println(as.payForItem(1, bidder.name(), 10));
        System.out.println("uncolect: "+as.uncollectedRevenue());
        System.out.println("rev: "+as.revenue());
	}

}
