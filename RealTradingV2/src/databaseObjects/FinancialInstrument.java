package databaseObjects;

import java.util.Date;
import java.util.Vector;

public class FinancialInstrument {
	
	private String name = "";
	private String tickerName = "";
	private Double currentPrice = null;
	private Vector<PriceHistoryUnit> priceHistory = null;
	private Double volatility = null;
//	private Vector<Purchase> purchases = null;
//	private Vector<Sale> sales = null;
	private Double totalReturnToDate = null;
	private Double totalMyTradeVolume = null;
	private Double currentVolume = null;
	private Double avgVolumePerHour = null;
	private boolean isStationary = false;
	private Double meanPrice = null;
	private boolean active = false;
	private Double costsToDate = null;
	private Double profitToDate = null;
	private Date dateCreated = null;
	private Date dateUpdated = null;

	public FinancialInstrument() {
		// TODO Auto-generated constructor stub
	}

	

	protected String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected String getTickerName() {
		return tickerName;
	}

	protected void setTickerName(String tickerName) {
		this.tickerName = tickerName;
	}

	protected Double getCurrentPrice() {
		return currentPrice;
	}

	protected void setCurrentPrice(Double currentPrice) {
		this.currentPrice = currentPrice;
	}

	protected Vector<PriceHistoryUnit> getPriceHistory() {
		return priceHistory;
	}

	protected void setPriceHistory(Vector<PriceHistoryUnit> priceHistory) {
		this.priceHistory = priceHistory;
	}

	protected Double getVolatility() {
		return volatility;
	}

	protected void setVolatility(Double volatility) {
		this.volatility = volatility;
	}

//	protected Vector<Purchase> getPurchases() {
//		return purchases;
//	}
//
//	protected void setPurchases(Vector<Purchase> purchases) {
//		this.purchases = purchases;
//	}
//
//	protected Vector<Sale> getSales() {
//		return sales;
//	}
//
//	protected void setSales(Vector<Sale> sales) {
//		this.sales = sales;
//	}

	protected Double getTotalReturnToDate() {
		return totalReturnToDate;
	}

	protected void setTotalReturnToDate(Double totalReturnToDate) {
		this.totalReturnToDate = totalReturnToDate;
	}

	protected Double getTotalMyTradeVolume() {
		return totalMyTradeVolume;
	}

	protected void setTotalMyTradeVolume(Double totalMyTradeVolume) {
		this.totalMyTradeVolume = totalMyTradeVolume;
	}

	protected Double getCurrentVolume() {
		return currentVolume;
	}

	protected void setCurrentVolume(Double currentVolume) {
		this.currentVolume = currentVolume;
	}

	protected Double getAvgVolumePerHour() {
		return avgVolumePerHour;
	}

	protected void setAvgVolumePerHour(Double avgVolumePerHour) {
		this.avgVolumePerHour = avgVolumePerHour;
	}

	protected boolean isStationary() {
		return isStationary;
	}

	protected void setStationary(boolean isStationary) {
		this.isStationary = isStationary;
	}

	protected Double getMeanPrice() {
		return meanPrice;
	}

	protected void setMeanPrice(Double meanPrice) {
		this.meanPrice = meanPrice;
	}

	protected boolean isActive() {
		return active;
	}

	protected void setActive(boolean active) {
		this.active = active;
	}

	protected Double getCostsToDate() {
		return costsToDate;
	}

	protected void setCostsToDate(Double costsToDate) {
		this.costsToDate = costsToDate;
	}

	protected Double getProfitToDate() {
		return profitToDate;
	}

	protected void setProfitToDate(Double profitToDate) {
		this.profitToDate = profitToDate;
	}

	protected Date getDateCreated() {
		return dateCreated;
	}

	protected void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	protected Date getDateUpdated() {
		return dateUpdated;
	}

	protected void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

}
