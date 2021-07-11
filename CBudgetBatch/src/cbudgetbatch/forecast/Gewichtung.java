package cbudgetbatch.forecast;

public class Gewichtung {
	
	private int year1back;
	private int year2back;
	private int year3back;
	
	public Gewichtung(int year1back, int year2back, int year3back) {
		super();
		this.year1back = year1back;
		this.year2back = year2back;
		this.year3back = year3back;
	}
	
	
	public Gewichtung() {
		super();
		// TODO Auto-generated constructor stub
	}


	public int getyear1back() {
		return year1back;
	}
	public void setyear1back(int year1back) {
		this.year1back = year1back;
	}
	public int getyear2back() {
		return year2back;
	}
	public void setyear2back(int year2back) {
		this.year2back = year2back;
	}
	public int getyear3back() {
		return year3back;
	}
	public void setyear3back(int year3back) {
		this.year3back = year3back;
	}
	

}
