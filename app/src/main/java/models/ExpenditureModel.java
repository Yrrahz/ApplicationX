package models;

import java.util.Calendar;

public class ExpenditureModel {

    private int expenditureId; // Barely used because DB handles it itself by auto-increment
    private int amount;
    private long date;
    private String event; // Currently this is either 'Expenditure' or 'Income'
    private String refID; // Reference to Category

    public ExpenditureModel(){
    }

    public ExpenditureModel(int expenditureId, int amount, String event, String refID, long date){
        this.expenditureId = expenditureId;
        this.amount = amount;
        this.event = event;
        this.refID = refID;
        if(date == 0){
            setDate();
        }else{
            this.date = date;
        }
    }

    public ExpenditureModel(int amount, String event, String refID, long date){
        this.amount = amount;
        this.event = event;
        this.refID = refID;
        if(date == 0){
            setDate();
        }else{
            this.date = date;
        }
    }

    public void setExpenditureId(int expenditureId){
        this.expenditureId = expenditureId;
    }

    public void setAmount(int amount){
        this.amount = amount;
    }

    public void setEvent(String event){
        this.event = event;
    }

    public void setRefID(String refID) {
        this.refID = refID;
    }

    public void setDate(long date){
        this.date = date;
    }

    public void setDate(){
        this.date = Calendar.getInstance().getTime().getTime();
    }

    public int getExpenditureId() {
        return expenditureId;
    }

    public int getAmount() {
        return amount;
    }

    public String getEvent() {
        return event;
    }

    public String getRefID() {
        return refID;
    }

    public long getDate(){
        return date;
    }
}
