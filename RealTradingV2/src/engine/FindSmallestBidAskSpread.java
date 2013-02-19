package engine;


import java.util.Hashtable;
import java.util.Vector;

import javafiles.ReadTextFile;

import clientConnection.Contract;

public class FindSmallestBidAskSpread {
	
	private static Vector<String> tickers = null;
	private static Hashtable<String,Double> tickersNSpreadsHashtable = new Hashtable<String,Double>();
	private static NewTradingEngine nte = null;
	public static boolean waitingForNextSpread = true;
	public static double spread = -444;
	public static Contract contract = null;
	
	public FindSmallestBidAskSpread() {
		
		tickers = ReadTextFile
				.readFileFirstToken("C:\\Users\\denman\\Desktop\\nyse_write_to1.txt");
		
		
		loopOverTickers();
	}
	
	public FindSmallestBidAskSpread(NewTradingEngine newTradingEngine) {
		
		nte = newTradingEngine;
		tickers = ReadTextFile
				.readFileFirstToken("C:\\Users\\denman\\Desktop\\nyse_write_to1.txt");
		
		
		loopOverTickers();
	}

	private void loopOverTickers() {
		
		contract = new Contract();
		contract.m_currency = "USD";
		contract.m_exchange = "NYSE";
		contract.m_symbol = "";
		contract.m_secType = "STK";
		
		int tickerId = 1;
		boolean start = true;
		
		for(int i = 0; i < tickers.size(); i++){
			
			tickerId++;
			
			
			String tickerSymbol = tickers.get(i);
			
			if(tickerSymbol.startsWith("MTDR")){
				start = true;
			}
			
			if(start == false){
				continue;
			}
			contract.m_symbol = tickerSymbol;
			
			try {
				nte.m_f.getM_client().reqMktData(tickerId, contract, "", true);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while(waitingForNextSpread){
				
				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			waitingForNextSpread= true;
			
			tickersNSpreadsHashtable.put(tickerSymbol, new Double(spread));
			System.out.println(tickerSymbol + ":  " + spread);
			
			spread = -333;
			
			
		}
		
		System.out.println("That's all folks!");
		
		System.exit(0);
	}

//	public static void main(String[] args){
//		
//		new FindSmallestBidAskSpread();
//	}

}
