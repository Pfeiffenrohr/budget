package cbudgetbatch.forecast;

import java.util.Map;

public class ResultObjectYear {
	
	
	private int jahr;
	private String kategorie;
	private String konto;
	private double Abweichungjahr;
	private Map <Integer,ResultObjectMonth> rsom ;
	
	
	
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
