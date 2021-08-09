package cbudgetbatch.forecastnew;

import java.util.HashMap;
import java.util.Map;

public class OverAllTable {
	
	private Double summeUngewichtet;
	private Double summeGewichtet;
	Map <Integer,Double >prozentDayOfYear;
	Map <Integer,Double >dayGewichtet;
	
	
	public OverAllTable(Double summeUngewichtet, Double summeGewichtet, Map<Integer, Double> prozentDayOfYear) {
		super();
		this.summeUngewichtet = summeUngewichtet;
		this.summeGewichtet = summeGewichtet;
		this.prozentDayOfYear = prozentDayOfYear;
	}
	
	public OverAllTable() {
		this.prozentDayOfYear = new HashMap<Integer, Double>();
		}
	public Double getSummeUngewichtet() {
		return summeUngewichtet;
	}
	public void setSummeUngewichtet(Double summeUngewichtet) {
		this.summeUngewichtet = summeUngewichtet;
	}
	public Double getSummeGewichtet() {
		return summeGewichtet;
	}
	public void setSummeGewichtet(Double summeGewichtet) {
		this.summeGewichtet = summeGewichtet;
	}
	public Map<Integer, Double> getProzentDayOfYear() {
		return prozentDayOfYear;
	}
	public void setProzentDayOfYear(Map<Integer, Double> prozentDayOfYear) {
		this.prozentDayOfYear = prozentDayOfYear;
	}
	
	public void computeProzentDay (int dayOfYear ,Double y1, Double y2, Double y3)
	{
		Double prozentTag = (y1+y2+y3) / this.summeUngewichtet;
		prozentDayOfYear.put(dayOfYear, prozentTag);
	}
	
	public void computeDayGewichtet()
	{
		this.dayGewichtet = new HashMap<Integer, Double>();
		for (Map.Entry<Integer, Double> entry : prozentDayOfYear.entrySet()) {
			Double wertGewichtet = summeGewichtet*entry.getValue();
			dayGewichtet.put(entry.getKey() , wertGewichtet);
		}
		
	}
	
	public Double getDayGewichtet(int dayOfYear) {		
		return dayGewichtet.get(dayOfYear);		
	}
	
	public void printSumProzent()
	{
		Double sumProzent=0.0;
		for (Map.Entry<Integer, Double> entry : prozentDayOfYear.entrySet()) {
			sumProzent+= entry.getValue();
		}
		//System.out.println("Summe Prozent = "+sumProzent);
	}

}
