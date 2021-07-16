package cbudgetbatch.forecast;

import java.util.HashMap;
import java.util.Map;

public class ResultObjectAll {
	
	private double abweichungGesamt;
	private Map <Integer, ResultObjectKategorieKonto> roy;
	private String KontoGroesteAbweichung;
	private String KategorieGroesteAbweichung;
	private int counter;
	
	public ResultObjectAll() {
		roy = new  HashMap<Integer,ResultObjectKategorieKonto>();
		counter=0;
	}
	
	public ResultObjectAll(double abweichungGesamt, Map<Integer, ResultObjectKategorieKonto> roy, String kontoGroesteAbweichung,
			String kategorieGroesteAbweichung) {
		super();
		this.abweichungGesamt = abweichungGesamt;
		this.roy = roy;
		KontoGroesteAbweichung = kontoGroesteAbweichung;
		KategorieGroesteAbweichung = kategorieGroesteAbweichung;
	}
	
	
	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
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
	public Map<Integer, ResultObjectKategorieKonto> getRoy() {
		return roy;
	}
	public void setRoy(Map<Integer, ResultObjectKategorieKonto> roy) {
		this.roy = roy;
	}

	public void count () {
		counter++;
	}
}
