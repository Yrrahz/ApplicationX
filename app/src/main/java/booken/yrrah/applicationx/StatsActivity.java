package booken.yrrah.applicationx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

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

        String stats = "Number of Categories\t\t\t"+ nrOfCategories +"\n\nNumber of Events\t\t\t"+nrOfEvents+
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
}
