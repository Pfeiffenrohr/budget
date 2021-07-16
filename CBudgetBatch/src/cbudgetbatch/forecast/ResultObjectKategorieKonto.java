package cbudgetbatch.forecast;

import java.util.Map;

public class ResultObjectKategorieKonto {
	
	
	private int jahr;
	private String kategorie;
	private String konto;
	private double wertYear1;
	private double wertYear2;
	private double wertYear3;
	private double real;
	private double Abweichungjahr;
	private Map <Integer,ResultObjectMonth> rsom ;
	
	
	
	public double getReal() {
		return real;
	}
	public void setReal(double real) {
		this.real = real;
	}
	public double getWertYear1() {
		return wertYear1;
	}
	public void setWertYear1(double wertYear1) {
		this.wertYear1 = wertYear1;
	}
	public double getWertYear2() {
		return wertYear2;
	}
	public void setWertYear2(double wertYear2) {
		this.wertYear2 = wertYear2;
	}
	public double getWertYear3() {
		return wertYear3;
	}
	public void setWertYear3(double wertYear3) {
		this.wertYear3 = wertYear3;
	}
	public String getKategorie() {
		return kategorie;
	}
	public void setKategorie(String kategorie) {
		this.kategorie = kategorie;
	}
	public String getKonto() {
		return konto;
	}
	public void setKonto(String konto) {
		this.konto = konto;
	}
	public int getJahr() {
		return jahr;
	}
	public void setJahr(int jahr) {
		this.jahr = jahr;
	}
	public double getAbweichungjahr() {
		return Abweichungjahr;
	}
	public void setAbweichungjahr(double abweichungjahr) {
		Abweichungjahr = abweichungjahr;
	}
	public Map<Integer, ResultObjectMonth> getRsom() {
		return rsom;
	}
	public void setRsom(Map<Integer, ResultObjectMonth> rsom) {
		this.rsom = rsom;
	}

}
