package booken.yrrah.applicationx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import comparators.sortByAmount;
import models.CategoryModel;
import models.ExpenditureModel;

public class StatsActivity extends AppCompatActivity {

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        setUp();
        percentageStats();
        compareMonths();
    }

    // Here you can practice Algorithms. TODO : Optimize, maybe with some threads...
    private void setUp(){
        this.dbHandler = new DBHandler(this);

        int nrOfCategories = dbHandler.getCategoriesCount();
        String mostExpensiveCategory = "";
        int mostExpensiveCat = 0;
        List<CategoryModel> categoryList = dbHandler.getAllCategories();
        String catWithMostExp;
        int catWithMostExpInt;

        if(categoryList.size() == 1){
            catWithMostExp = categoryList.get(0).getName();
            catWithMostExpInt = dbHandler.getAllExpToCategory(catWithMostExp).size(); // Here you could run getAllExp.. but this seems more correct in principle.
        }else{
            catWithMostExp = "";
            catWithMostExpInt = 0;
        }

        for(CategoryModel cm : categoryList){
            if(cm.getTotalAmount() >= mostExpensiveCat){ // If two or more categories has the same maxValue. Then this returns the last one found.
                mostExpensiveCat = cm.getTotalAmount();
                mostExpensiveCategory = cm.getName();
            }
            if(cm.getDate() >= catWithMostExpInt){ // Category's Date attribute is currently a counter for the amount of expenditures and not an actual date... yet...
                catWithMostExpInt = cm.getDate();
                catWithMostExp = cm.getName();
            }
        }

        List<ExpenditureModel> expList = dbHandler.getAllExpAmounts();
        int nrOfEvents = expList.size();
        String eventWithHighestValue = "";
        int mostExpEvent = 0;
        int[] calc = {0,0,0}; // int[0] = income, int[1] = expenditure, int[2] = highest income
        String mainIncome = "";

        for(ExpenditureModel exp : expList){
            if(exp.getAmount() > mostExpEvent && !exp.getEvent().equals("Income Event")){
                mostExpEvent = exp.getAmount();
                eventWithHighestValue = exp.getRefID();
            }
            if(exp.getEvent().equals("Income Event")){
                calc[0] += exp.getAmount();
                if(exp.getAmount() >= calc[2]){
                    calc[2] = exp.getAmount();
                    mainIncome = exp.getRefID();
                }
            }else{
                calc[1] += exp.getAmount();
            }
        }

        String stats = "Nr of Categories\t\t\t"+ nrOfCategories +"\n\nNr of Events\t\t\t"+nrOfEvents+
                "\n\nMost expensive Category\t\t\t"+mostExpensiveCategory+" "+mostExpensiveCat+
                "\n\nMost expensive Event\t\t\t"+eventWithHighestValue+" "+mostExpEvent+
                "\n\nCategory with most Events\t\t\t"+catWithMostExp+" "+catWithMostExpInt+
                "\n\n\n\nMain Income\n\n"+mainIncome+"\t\t\t"+calc[2];



        TextView listOfStats = findViewById(R.id.listOfStatsView);
        listOfStats.setText(stats);

        listOfStats = findViewById(R.id.nrOfIncome);
        listOfStats.setText(String.format("%s",calc[0]));

        listOfStats = findViewById(R.id.nrOfExpenditure);
        listOfStats.setText(String.format("%s",calc[1]));

        listOfStats = findViewById(R.id.nrOfResult);
        listOfStats.setText(String.format("%s",calc[0] - calc[1]));
    }

    private void percentageStats(){
        float totalAmount = dbHandler.totalAmount();
        List<CategoryModel> categoryModelList = dbHandler.getAllCategories();
        Collections.sort(categoryModelList, new sortByAmount());

        EditText percentagePresentation = findViewById(R.id.percentageStats);
        percentagePresentation.setEnabled(false);

        String dataToPresent;
        float percentageValue;
        for(CategoryModel cm : categoryModelList){
            percentageValue = Math.round(((float)cm.getTotalAmount() / totalAmount) * 1000);
            percentageValue = percentageValue/10; // For some weird reason... you can't divide 10 from the line above.. So I do it here.
            dataToPresent = percentagePresentation.getText().toString() + "\n" + cm.getName() + "\t\t\t" + percentageValue + "%";
            percentagePresentation.setText(dataToPresent);
        }
    }

    private void compareMonths(){
        int[] last_12_months = new int[13]; // It's actually 13 months... [0] = this month, [1] = previous month...
        String[] months = populateArray();
        int mYear, mMonth;
        StringBuilder presentMonthStats = new StringBuilder();
        List<ExpenditureModel> compMonthList = dbHandler.getAllExpAmounts();
        Calendar c = Calendar.getInstance();
        int currentYear =  c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH); // zero-based

        for(ExpenditureModel expModel : compMonthList){
            c.setTimeInMillis(expModel.getDate());
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH); // zero-based

            if(currentYear - mYear == 0){
                last_12_months[currentMonth-mMonth] += expModel.getAmount();
            }else if(currentYear - mYear == 1 && mMonth > currentMonth){
                last_12_months[currentMonth+12-mMonth] += expModel.getAmount();
            }else if(currentYear - mYear == 1 && mMonth == currentMonth){
                last_12_months[12] += expModel.getAmount();
            }
        }

        presentMonthStats.append("\nCurrent and previous months\n\n");
        for(int i = 0 ; i < 12 ; i++){
            if(currentMonth-i >= 0){
                presentMonthStats.append(months[currentMonth-i]).append(" : ").append(last_12_months[i]).append("\n");
            }else if(currentMonth-i < 0){
                presentMonthStats.append(months[12+currentMonth-i]).append(" : ").append(last_12_months[i]).append("\n");
            }
        }
        presentMonthStats.append("\nSame month last year\n").append(months[currentMonth]).append(" : ").append(last_12_months[12]);

        TextView previousMonthTextView = findViewById(R.id.previousMonths);
        previousMonthTextView.setText(presentMonthStats.toString());
    }

    private String[] populateArray(){
        String[] returnArray = new String[12];

        returnArray[0] = "January";
        returnArray[1] = "February";
        returnArray[2] = "March";
        returnArray[3] = "April";
        returnArray[4] = "May";
        returnArray[5] = "June";
        returnArray[6] = "July";
        returnArray[7] = "August";
        returnArray[8] = "September";
        returnArray[9] = "October";
        returnArray[10] = "November";
        returnArray[11] = "December";

        return returnArray;
    }
}
