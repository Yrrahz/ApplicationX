package models;

public class CategoryModel {
    private String name;
    private int totalAmount;
    private int date;

    public CategoryModel(String name, int totalAmount, int date){
        this.name = name;
        this.totalAmount = totalAmount;
        this.date = date;
    }

    public CategoryModel(String name, int totalAmount){
        this.name = name;
        this.totalAmount = totalAmount;
        this.date = 0;
    }

    public CategoryModel(){

    }


    public void setName(String name){
        this.name = name;
    }

    public void setTotalAmount(int totalAmount){
        this.totalAmount = totalAmount;
    }

    public void setDate(int date){
        this.date = date;
    }

    public String getName(){
        return name;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getDate(){
        return date;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof CategoryModel)
        {
            sameSame = this.name.equals(((CategoryModel) object).name);
        }

        return sameSame;
    }
}