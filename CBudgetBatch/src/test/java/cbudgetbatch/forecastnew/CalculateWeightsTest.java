package test.java.cbudgetbatch.forecastnew;
import cbudgetbatch.DBBatch;
import cbudgetbatch.gewichtung.CalculateWeights;
import cbudgetbatch.gewichtung.YearTable;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class CalculateWeightsTest {

    @Test
    public void calculateWeightTest() {
        DBBatch db = Mockito.mock(DBBatch.class);
        Calendar []  calendars = new Calendar[8];
        Hashtable result = new Hashtable();
        ArgumentCaptor<Hashtable> hashCaptor = ArgumentCaptor.forClass(Hashtable.class);
        for (int i =0 ; i < calendars.length; i++ ) {
            calendars [i] = Calendar.getInstance();
            calendars [i].add(Calendar.YEAR, -i);
        }
        Map<Integer, YearTable> maps = getMapsAllYear(calendars);
        when(db.insertForecastWeights(any(Hashtable.class))).thenReturn(true);
        CalculateWeights calculateWeights = new CalculateWeights();
        calculateWeights.setKonto(1);
        calculateWeights.setCategory(1);
        calculateWeights.setDb(db);
        calculateWeights.calculate(maps);
        verify(db).insertForecastWeights(hashCaptor.capture());
        result = hashCaptor.getValue();

        System.out.println("Test");

        }


    private Map<Integer, YearTable> getMapsAllYear(Calendar []  calendars ) {
        Map<Integer, YearTable > map = new HashMap<>();
        for (int i = 1;  i< calendars.length; i++) {
            map.put(calendars[i].get(Calendar.YEAR),getDummyMapsYear());
        }
        return map;
    }

    private YearTable  getDummyMapsYear () {
        int limit = 366;
        YearTable yt = new YearTable();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Map<Integer, Double> map =  new HashMap<>();
        map.put(2,2.0);
        for (int k = 0; k <= limit; k++) {
            if (map.get(k) == null) {
                map.put(k, 0.0);
            }
        }
        yt.setMapYear(map);
        yt.computeSum();
        return yt;
    }


    }
