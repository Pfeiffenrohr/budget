package test.java.cbudgetbatch.forecastnew;

import cbudgetbatch.DBBatch;
import cbudgetbatch.gewichtung.CalculateWeights;
import cbudgetbatch.gewichtung.ForecastWriteDataToFile;
import cbudgetbatch.gewichtung.YearTable;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class ForecastWriteDataToFileTest {


    @Test
    public void calculateYearTest() {
        Map<Integer, Double> map = new HashMap<>();
        Map<Integer, YearTable> resultmap;
        DBBatch db = Mockito.mock(DBBatch.class);
        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
        CalculateWeights calculateWeights = Mockito.mock(CalculateWeights.class);
        Hashtable katHash = new Hashtable();
        Hashtable kontoHash = new Hashtable();


        Vector katVector = new Vector();
        Vector konVector = new Vector();

        katHash.put("id", 1);
        katHash.put("name", "Elektronik");
        katHash.put("forecast", 1);
        kontoHash.put("id", 1);
        kontoHash.put("name", "Sparkasse Giro");
        katVector.add(katHash);
        konVector.add(kontoHash);
        System.out.println("Teste calculateYear");
        map.put(2, 2.0);

        ForecastWriteDataToFile forecastWriteDataToFile = new ForecastWriteDataToFile();
        when(db.getAllKategorien()).thenReturn(katVector);
        when(db.getAllKonto()).thenReturn(konVector);
        when(db.getAllKonto()).thenReturn(konVector);
        when(db.getKategorienAlleSummeWhereAsMapPerDay(anyString(), anyString(), anyString())).thenReturn(map);
        when(db.insertForecastWeights(any(Hashtable.class))).thenReturn(true);
        when(calculateWeights.calculate(any(Map.class))).thenReturn(true);
        Calendar[] calendars = new Calendar[8];
        for (int i = 0; i < calendars.length; i++) {
            calendars[i] = Calendar.getInstance();
            calendars[i].add(Calendar.YEAR, -i);
        }

        forecastWriteDataToFile.calculateYears(db, calendars, calculateWeights);
        verify(calculateWeights).calculate(mapCaptor.capture());
        resultmap = mapCaptor.getValue();
        assertEquals(resultmap.get(2022).getSumOfYear(), 2.0);
    }

}

