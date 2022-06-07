package budget.statistik;

public class TupelAmount {
    
    Double value;
    Double amount;
    Double gewicht;
    
    
    
    public TupelAmount(Double value, Double amount, Double gewicht) {
        super();
        this.value = value;
        this.amount = amount;
        this.gewicht = gewicht;
    }
    
    public TupelAmount() {
        
    }
    
    public Double getValue() {
        return value;
    }
    public void setValue(Double value) {
        this.value = value;
    }
    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    public Double getGewicht() {
        return gewicht;
    }
    public void setGewicht(Double gewicht) {
        this.gewicht = gewicht;
    }

    
}
