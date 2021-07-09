package cbudgetbatch.forecast;

import java.util.HashMap;
import java.util.Map;

public class ResultObjectAll {
	
	private double abweichungGesamt;
	private Map <Integer, ResultObjectYear> roa;
	private String KontoGroesteAbweichung;
	private String KategorieGroesteAbweichung;
	
	public ResultObjectAll() {
		roa = new  HashMap<Integer,ResultObjectYear>();
	}
	
	public ResultObjectAll(double abweichungGesamt, Map<Integer, ResultObjectYear> roa, String kontoGroesteAbweichung,
			String kategorieGroesteAbweichung) {
		super();
		this.abweichungGesamt = abweichungGesamt;
		this.roa = roa;
		KontoGroesteAbweichung = kontoGroesteAbweichung;
		KategorieGroesteAbweichung = kategorieGroesteAbweichung;
	}
	public String getKontoGroesteAbweichung() {
		return KontoGroesteAbweichung;
	}
	public void setKontoGroesteAbweichung(String kontoGroesteAbweichung) {
		KontoGroesteAbweichung = kontoGroesteAbweichung;
	}
	public String getKategorieGroesteAbweichung() {
		return KategorieGroesteAbweichung;
	}
	public void setKategorieGroesteAbweichung(String kategorieGroesteAbweichung) {
		KategorieGroesteAbweichung = kategorieGroesteAbweichung;
	}
	public double getAbweichungGesamt() {
		return abweichungGesamt;
	}
	public void setAbweichungGesamt(double abweichungGesamt) {
		this.abweichungGesamt = abweichungGesamt;
	}
	public Map<Integer, ResultObjectYear> getRoa() {
		return roa;
	}
	public void setRoa(Map<Integer, ResultObjectYear> roa) {
		this.roa = roa;
	}

}
