package cbudgetbatch.forecastnew;

import java.util.Map;

public class YearTable {
	
	
	Double sumOfYear;
	Map <Integer,Double >mapYear;
	
	
	public YearTable(Double sumOfYear, Map<Integer, Double> mapYear) {
		super();
		this.sumOfYear = sumOfYear;
		this.mapYear = mapYear;
	}
	
	public YearTable() {
	
	}
	
	public Double getSumOfYear() {
		return sumOfYear;
	}
	public void setSumOfYear(Double sumOfYear) {
		this.sumOfYear = sumOfYear;
	}
	public Map<Integer, Double> getMapYear() {
		return mapYear;
	}
	public void setMapYear(Map<Integer, Double> mapYear) {
		this.mapYear = mapYear;
	}
	
	public void computeSum() {
		sumOfYear=0.0;
		for (Map.Entry<Integer, Double> entry : mapYear.entrySet()) {
			sumOfYear+= entry.getValue();
		}
		//System.out.println("Summe = "+sumOfYear);
	}
    
}
