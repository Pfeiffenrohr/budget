package cbudgetbatch;

public class ResultObject {
	
	private int month;
	private double yearback1;
	private double yearback2;
	private double yearback3;
    private double gesamt;
	
    private double realValue;
    private double diff;
    private double diffProzent;
    
	public double getDiffProzent() {
		return diffProzent;
	}

	public void setDiffProzent(double diffProzent) {
		this.diffProzent = diffProzent;
	}

	public double getDiff() {
		return diff;
	}

	public void setDiff(double diff) {
		this.diff = diff;
	}

	public double getRealValue() {
		return realValue;
	}

	public void setRealValue(double realValue) {
		this.realValue = realValue;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public double getYearback1() {
		return yearback1;
	}

	public void setYearback1(double yearback1) {
		this.yearback1 = yearback1;
	}

	public double getYearback2() {
		return yearback2;
	}

	public void setYearback2(double yearback2) {
		this.yearback2 = yearback2;
	}

	public double getYearback3() {
		return yearback3;
	}

	public void setYearback3(double yearback3) {
		this.yearback3 = yearback3;
	}

	public double getGesamt() {
		return gesamt;
	}

	public void setGesamt(double gesamt) {
		this.gesamt = gesamt;
	}

	
}
