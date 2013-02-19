package engine;

import gui.MainFrame;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import trading.Purchase;
import trading.Sale;
import visual.FreeChart;
import clientConnection.Contract;
import clientConnection.Order;

public class NewTradingEngine {

	public Contract contract = null;
	public int tickerId = 1;
	public int purchaseId = 0;
	public int saleId = 0;
	public boolean buy = true;
	public boolean sell = false;
	public Vector<Double> prices = new Vector<Double>();
	public Vector<String> pricesString = null;
	public Double mostRecentPurchasePrice = null;
	public static Double movingAverage = null;
	public Vector<Purchase> purchases = new Vector<Purchase>();
	public Vector<Sale> sales = new Vector<Sale>();
	public Double currentPrice = null;
	public Double currentPriceBid = null;
	public Double currentPriceAsk = null;
	public static Vector<Double[]> currentPriceArray = new Vector<Double[]>();
	public FreeChart fc1 = null;
	public Double[] sellPrice = { .27 }; //.09
	public Double[] buyPriceUnderLow = { -.01 }; //.05
//	public Double[] buyPriceUnderHigh = { .02 };//
	public Double[] buyPriceOver = { 3.9 };
	public Double[] stopLossPrice = { .41 }; //.2
	public Integer[] movingAverageValues = {115}; //.25
	public static MainFrame m_f = null;
	public boolean keepLooping = true;
	public boolean isPaused = true;
	private JButton closeOutButton = new JButton("Close Out");
	private JButton pauseButton = new JButton("Pause");
	public boolean newMarketDataReceived = false;
	private Double ma = null;
//	private Double totalCost = 0.0;
	public Double totalTradingCost = 0.0;
	public int numberOfPurchases = 0;
	public Integer numberOfStopLoss = 0;
	public Integer numberOfProfitableSales = 0;
	public Order buyOrder = new Order();
	public Order sellOrder = new Order();
	public Order stopLossOrder = new Order();
	public boolean bothSellOrdersCompleted = false;
	private int currentOrderId = 1;
	public boolean submitStopLossOrderCompleted = false;
	public boolean submitSellOrderCompleted = false;
	public boolean buyOrderCompleted = false;

	public NewTradingEngine(MainFrame mainFrame) {

		this.m_f = mainFrame;
		contract = new Contract();
		
		contract.m_currency = "USD";
		contract.m_exchange = "SMART";
		contract.m_symbol = "GOOG";
		contract.m_secType = "STK";
		
		closeOutButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onCloseOut();

			}

			private void onCloseOut() {

				System.out.println("Close out position(s)...");
				keepLooping = false;
			}

		});

		pauseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (isPaused == false) {
					isPaused = true;
					pauseButton.setForeground(Color.red);
					pauseButton.setText("Resume");
				} else {
					isPaused = false;
					pauseButton.setForeground(Color.black);
					pauseButton.setText("Pause");
				}

			}

		});

	}

	private void createAndShowGUI() {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.add(closeOutButton);
		panel.add(pauseButton);
		closeOutButton.setEnabled(true);
		pauseButton.setEnabled(true);
		frame.pack();
		frame.setVisible(true);

	}


	public void startThisSucker() {

		isPaused = false;
		keepLooping = true;

		while (keepLooping && prices.size() < 58500) {

			
			long timeReq = System.currentTimeMillis();
			long timeForSellLimits = 0;
			long totalLoopTime = System.currentTimeMillis();
			
			while (isPaused) {
				try {
					Thread.sleep(39);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			}

			

			// m_f.m_orderDlg.m_rc = true;

			Double[] x = new Double[6];
			currentPriceArray.add(x);
			tickerId++;
			m_f.m_client.reqMktData(tickerId, contract, "", true);

			
			boolean waitformarketdata = true;
			while (!newMarketDataReceived) {

				try {
					
					if(waitformarketdata  == true){
						System.out.println("");
						System.out.println("Waiting to get back market data response...");
						System.out.println("");
					}
					waitformarketdata = false;
					
					Thread.sleep(15);
				} catch (InterruptedException e) {
			
					e.printStackTrace();
				}
			}

			newMarketDataReceived = false;

			currentPrice = prices.get(prices.size() - 1);
			currentPriceBid = currentPriceArray
					.get(currentPriceArray.size() - 1)[0];
			currentPriceAsk = currentPriceArray
					.get(currentPriceArray.size() - 1)[1];

			ma = calculateMovingAverage(currentPriceArray.size() - 1,
					movingAverageValues[0]);
			
			
			//check to see if sell limit orders have been both received and if one has been executed
			//if one has been executed, then buy = true;

			if (buy && (currentPriceArray.size() > movingAverageValues[0]-1) && (currentPriceBid < (ma - buyPriceUnderLow[0]))
					&& (currentPriceBid > (ma - buyPriceOver[0]))) {
				
				currentOrderId++;
				buy = false;
				
				buyOrder = new Order();
				buyOrder.m_orderId = currentOrderId;
				buyOrder.m_clientId = 1;
				buyOrder.m_orderType = "MKT";
				buyOrder.m_totalQuantity = 100;
				buyOrder.m_action = "BUY";
				buyOrder.m_allOrNone = false;
				
				
				try {

					m_f.m_client.placeOrder(buyOrder.m_orderId, contract,
							buyOrder);
					
					System.out.println("Buy order submitted with id = " + this.buyOrder.m_orderId + ".");
					
				} catch (Exception e1) { //problem here
					e1.printStackTrace();
				}

				boolean waitforbuy = true;
				while (!buyOrderCompleted) {
					try {
						Thread.sleep(15);
						
						if(waitforbuy){
							System.out.println("");
							System.out.println("Waiting to get back buy order response...");
							System.out.println("");
						}
						waitforbuy = false;

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				buyOrderCompleted = false;
				sell = true;
				timeForSellLimits = System.currentTimeMillis();
				
				//createLimitOrders();
				
			}
			
			if (sell
					&& currentPriceAsk > (mostRecentPurchasePrice + sellPrice[0])) {

				sell = false;
				currentOrderId++;
				
				this.sellOrder = new Order();
				
				this.sellOrder.m_orderId = currentOrderId;
				this.sellOrder.m_clientId = 1;
				this.sellOrder.m_orderType = "MKT";
				this.sellOrder.m_totalQuantity = 100;
				this.sellOrder.m_action = "SELL";
				this.sellOrder.m_allOrNone = false;
				
				m_f.m_client.placeOrder(sellOrder.m_orderId, contract,
						sellOrder);
				
				System.out.println("");
				System.out.println("Most Recent Purchase Price: " + mostRecentPurchasePrice);
				System.out.println("Sell order placed with id = " + this.sellOrder.m_orderId + " and price <> " + currentPriceAsk);
				System.out.println("");
				
//				Date date = new Date();
//				sell(currentPrice,
//						100,
//						date,
//						false);
				//buy = true;
				// fc1.getSeries3().add(i,prices.get(i));
				totalTradingCost += 1.0;
	//			numberOfProfitableSales++;
			}
			if (sell
					&& currentPriceAsk < (mostRecentPurchasePrice - stopLossPrice[0])) {

				currentOrderId++;
				sell = false;
				
				this.stopLossOrder = new Order();
				
				this.stopLossOrder.m_orderId = currentOrderId;
				this.stopLossOrder.m_clientId = 1;
				this.stopLossOrder.m_orderType = "MKT";
				this.stopLossOrder.m_totalQuantity = 100;
				this.stopLossOrder.m_action = "SELL";
				this.stopLossOrder.m_allOrNone = false;
				
				m_f.m_client.placeOrder(stopLossOrder.m_orderId, contract,
						stopLossOrder);
				
				System.out.println("");
				System.out.println("Most Recent Purchase Price: " + mostRecentPurchasePrice);
				System.out.println("Stop loss order placed with id = " + this.stopLossOrder.m_orderId + " and price <> " + currentPriceAsk);
				System.out.println("");
				
//				Date date = new Date();
//				sell(currentPrice,
//						100,
//						date,
//						true);
				
				//buy = true;
				// fc1.getSeries3().add(i,prices.get(i));
				totalTradingCost += 1.0;
//				numberOfStopLoss++;
			}
			
			
			double totalCostTemp = totalTradingCost;
			for (int i = 0; i < purchases.size(); i++) {
				totalCostTemp += purchases.get(i).getPricePurchasedAt()
						* purchases.get(i).getQuantityPurchased();
			}

			double totalRevenueTemp = 0;

			for (int i = 0; i < sales.size(); i++) {
				totalRevenueTemp += sales.get(i).getPriceSoldAt()
						* sales.get(i).getQuantitySold();
			}

			Double profitTemp = totalRevenueTemp - totalCostTemp;

			System.out.println(new Date());
			System.out.println("Number of Purchases: " + numberOfPurchases);
			System.out.println("Number of Profitable Sales: " + numberOfProfitableSales);
			System.out.println("Number of Stop Loss Sales: " + numberOfStopLoss);
			System.out.println("Buy Size: " + purchases.size());
			System.out.println("Sell size: " + sales.size());
			System.out.println("Total Revenue(Temp): " + totalRevenueTemp);
			System.out.println("Total Cost(Temp): " + totalCostTemp);
			System.out.println("Total Profit(Temp): " + profitTemp);

			timeReq = System.currentTimeMillis() - timeReq;

			try {
				Thread.sleep(Math.max(0, 4000 - timeReq));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Final Number of Purchases: " + numberOfPurchases);
		System.out.println("Final Number of Profitable Sales: "+ numberOfProfitableSales);
		System.out.println("Final Number of Stop Loss Sales: " + numberOfStopLoss);

		double totalRevenue = 0;
		double totalCost = totalTradingCost;

//		if (purchases.size() > sales.size()) {
//			purchases.remove(purchases.size() - 1);
//		}

		for (int i = 0; i < purchases.size(); i++) {
			totalCost += purchases.get(i).getPricePurchasedAt()
					* purchases.get(i).getQuantityPurchased();
		}

		for (int i = 0; i < sales.size(); i++) {
			totalRevenue += sales.get(i).getPriceSoldAt()
					* sales.get(i).getQuantitySold();
		}

		Double profit = totalRevenue - totalCost;

		System.out.println("Total Revenue: " + totalRevenue);
		System.out.println("Total Cost: " + totalCost);
		System.out.println("Total Profit: " + profit);
	}

	private void createLimitOrders() {}

	private Double returnMean(Vector<Double> diffValues) {

		Integer total = diffValues.size();
		Double sum = 0.0;

		for (int i = 0; i < diffValues.size(); i++) {
			sum += diffValues.get(i);
		}
		return sum / total;
	}

	public void buy(Double price, Integer quantity, Date date) {

		purchases.add(new Purchase(this.purchaseId, price, quantity, date));
		this.purchaseId++;
	}

	public void sell(Double price, Integer quantity, Date date,
			boolean stopLoss) {

		Purchase aPurchase = null;
		Sale aSale = new Sale(this.saleId, price, quantity, date, stopLoss);
		this.saleId++;
		sales.add(aSale);
		for (int i = 0; i < purchases.size(); i++) {
			if (!purchases.get(i).isSold()) {
				aPurchase = purchases.get(i);
				aPurchase.setSold(true);
			}
		}
	}

	private static Double calculateMovingAverage(int position, int back) {

		Integer i = null;
		Integer total = 0;
		Double sum = 0.0;

		for (i = position; i > Math.max(position - back, 0); i--) {
			sum += currentPriceArray.get(i - 1)[0];
			total++;
		}

		return movingAverage = sum / total;

	}

	public static void testThisClass(){
		
		
		int index = 0;
		double sum = 0;
		double i = 31.2;
		
			Double[] x = new Double[6];
			x[0] = i;
			currentPriceArray.add(x);
			
			
		 i = 31.45;
			
		 x = new Double[6];
			x[0] = i;
			currentPriceArray.add(x);
			
			
			 i = 31.95;
				
			 x = new Double[6];
				x[0] = i;
				currentPriceArray.add(x);
				
				
				 i = 32.15;
					
				 x = new Double[6];
					x[0] = i;
					currentPriceArray.add(x);
				
			
			for(index = 0; index < currentPriceArray.size(); index++){
			System.out.println(currentPriceArray.get(index)[0].toString());
					}
			
			
		System.out.println(calculateMovingAverage(currentPriceArray.size()-1,1));
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public int getTickerId() {
		return tickerId;
	}

	public void setTickerId(int tickerId) {
		this.tickerId = tickerId;
	}

	public int getPurchaseId() {
		return purchaseId;
	}

	public void setPurchaseId(int purchaseId) {
		this.purchaseId = purchaseId;
	}

	public int getSaleId() {
		return saleId;
	}

	public void setSaleId(int saleId) {
		this.saleId = saleId;
	}

	public boolean isBuy() {
		return buy;
	}

	public void setBuy(boolean buy) {
		this.buy = buy;
	}

	public boolean isSell() {
		return sell;
	}

	public void setSell(boolean sell) {
		this.sell = sell;
	}

	public Vector<Double> getPrices() {
		return prices;
	}

	public void setPrices(Vector<Double> prices) {
		this.prices = prices;
	}

	public Vector<String> getPricesString() {
		return pricesString;
	}

	public void setPricesString(Vector<String> pricesString) {
		this.pricesString = pricesString;
	}

	public Double getMostRecentPurchasePrice() {
		return mostRecentPurchasePrice;
	}

	public void setMostRecentPurchasePrice(Double mostRecentPurchasePrice) {
		this.mostRecentPurchasePrice = mostRecentPurchasePrice;
	}

	public Double getMovingAverage() {
		return movingAverage;
	}

	public void setMovingAverage(Double movingAverage) {
		this.movingAverage = movingAverage;
	}

	public Vector<Purchase> getPurchases() {
		return purchases;
	}

	public void setPurchases(Vector<Purchase> purchases) {
		this.purchases = purchases;
	}

	public Vector<Sale> getSales() {
		return sales;
	}

	public void setSales(Vector<Sale> sales) {
		this.sales = sales;
	}

	public Double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(Double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public FreeChart getFc1() {
		return fc1;
	}

	public void setFc1(FreeChart fc1) {
		this.fc1 = fc1;
	}

	public Double[] getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(Double[] sellPrice) {
		this.sellPrice = sellPrice;
	}

	public Double[] getBuyPriceUnderLow() {
		return buyPriceUnderLow;
	}

	public void setBuyPriceUnderLow(Double[] buyPriceUnderLow) {
		this.buyPriceUnderLow = buyPriceUnderLow;
	}

	public Double[] getBuyPriceOver() {
		return buyPriceOver;
	}

	public void setBuyPriceOver(Double[] buyPriceOver) {
		this.buyPriceOver = buyPriceOver;
	}

	public Double[] getStopLossPrice() {
		return stopLossPrice;
	}

	public void setStopLossPrice(Double[] stopLossPrice) {
		this.stopLossPrice = stopLossPrice;
	}

	public Integer[] getMovingAverageValues() {
		return movingAverageValues;
	}

	public void setMovingAverageValues(Integer[] movingAverageValues) {
		this.movingAverageValues = movingAverageValues;
	}

	public MainFrame getM_f() {
		return m_f;
	}

	public void setM_f(MainFrame m_f) {
		this.m_f = m_f;
	}

	public void printSomeShit(double price) {
		System.out.println("PRCE N SHIT" + price);

	}

	public Vector<Double[]> getCurrentPriceArray() {
		return currentPriceArray;
	}

	public void setCurrentPriceArray(Vector<Double[]> currentPriceArray) {
		this.currentPriceArray = currentPriceArray;
	}

	public boolean isBuyOrderCompleted() {
		return buyOrderCompleted;
	}

	public void setBuyOrderCompleted(boolean buyOrderCompleted) {
		this.buyOrderCompleted = buyOrderCompleted;
	}
	
	public boolean isKeepLooping() {
		return keepLooping;
	}

	public void setKeepLooping(boolean keepLooping) {
		this.keepLooping = keepLooping;
	}

	public Double getTotalTradingCost() {
		return totalTradingCost;
	}

	public void setTotalTradingCost(Double totalTradingCost) {
		this.totalTradingCost = totalTradingCost;
	}

	public int getCurrentOrderId() {
		return currentOrderId;
	}

	public void setCurrentOrderId(int currentOrderId) {
		this.currentOrderId = currentOrderId;
	}
	
	public Order getBuyOrder() {
		return buyOrder;
	}

	public void setBuyOrder(Order buyOrder) {
		this.buyOrder = buyOrder;
	}

	public Order getSellOrder() {
		return sellOrder;
	}

	public void setSellOrder(Order sellOrder) {
		this.sellOrder = sellOrder;
	}

	public Order getStopLossOrder() {
		return stopLossOrder;
	}

	public void setStopLossOrder(Order stopLossOrder) {
		this.stopLossOrder = stopLossOrder;
	}

	public void findsmallestbidask() {
		
		new FindSmallestBidAskSpread(this);
		
	}

}
