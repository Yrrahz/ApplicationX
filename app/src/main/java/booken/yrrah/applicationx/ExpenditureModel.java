package booken.yrrah.applicationx;

public class ExpenditureModel {

    private int expenditureId; // Barely used because DB handles it itself by auto-increment
    private int amount;
    private int date;
    private String event;
    private String refID; // Reference to Category

    public ExpenditureModel(){
        this.date = 0; // this is to ensure we always have an int value, in case some method would crash if it would getDate() and receive a null value.
    }

    public ExpenditureModel(int expenditureId, int amount, String event, String refID, int date){
        this.expenditureId = expenditureId;
        this.amount = amount;
        this.event = event;
        this.refID = refID;
        if(date == 0){
            setDate(0);
        }else{
            this.date = date;
        }
    }

    public ExpenditureModel(int amount, String event, String refID, int date){
        this.amount = amount;
        this.event = event;
        this.refID = refID;
        if(date == 0){
            setDate(0);
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

    public void setDate(int date){
        this.date = date;
    }

//    public void setDate(int date){
//        Calendar now = Calendar.getInstance();
//        if (date == 0) {
//            this.date = now.get(Calendar.YEAR)*10000 + (now.get(Calendar.MONTH)+1)*100 + now.get(Calendar.DATE);
//        } else {
//            this.date = date;
//        }
//    }

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

    public int getDate(){
        return date;
    }
}
