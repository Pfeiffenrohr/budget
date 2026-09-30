package budget.statistik;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TupelAmountTest {

    @Test
    public void storesConstructorValues() {
        TupelAmount tupel = new TupelAmount(10.0, 20.0, 0.5);

        assertEquals(Double.valueOf(10.0), tupel.getValue());
        assertEquals(Double.valueOf(20.0), tupel.getAmount());
        assertEquals(Double.valueOf(0.5), tupel.getGewicht());
    }
}
