package cbudgetbatch.gewichtung;

import java.util.Map;

public class YearTable {
	
	
	Double sumOfYear;
	Integer anzOfDaysNotZero;
	Map <Integer,Double >mapYear;
	
	
	public YearTable(Double sumOfYear, Integer anzOfDaysNotZero, Map<Integer, Double> mapYear) {
        super();
        this.sumOfYear = sumOfYear;
        this.anzOfDaysNotZero = anzOfDaysNotZero;
        this.mapYear = mapYear;
    }

	
	public YearTable() {
	
	}
	
	public Integer getAnzOfDaysNotZero() {
        return anzOfDaysNotZero;
    }

    public void setAnzOfDaysNotZero(Integer anzOfDaysNotZero) {
        this.anzOfDaysNotZero = anzOfDaysNotZero;
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
		anzOfDaysNotZero=0;
		int counter=0;
		for (Map.Entry<Integer, Double> entry : mapYear.entrySet()) {
			sumOfYear+= entry.getValue();
			counter++;
			if (entry.getValue()>0.01 || entry.getValue() < -0.01)
			{
			    anzOfDaysNotZero++;   
			}
		}
		//System.out.println("Summe = "+sumOfYear);
	}
	

    
}
