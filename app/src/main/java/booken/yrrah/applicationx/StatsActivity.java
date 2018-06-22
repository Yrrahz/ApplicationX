package booken.yrrah.applicationx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.CategoryModel;
import models.ExpenditureModel;

public class StatsActivity extends AppCompatActivity {

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        setUp();
    }

    // Here you can practice Algorithms. TODO : Optimize, maybe with some threads...
    private void setUp(){
        this.dbHandler = new DBHandler(this);

        int nrOfCategories = dbHandler.getCategoriesCount();
        String mostExpensiveCategory = "";
        int mostExpensiveCat = 0;
        List<CategoryModel> categoryList = dbHandler.getAllCategories();
        for(CategoryModel cm : categoryList){
            if(cm.getTotalAmount() >= mostExpensiveCat){ // If two or more categories has the same maxValue. Then this returns the last one found.
                mostExpensiveCat = cm.getTotalAmount();
                mostExpensiveCategory = cm.getName();
            }
        }

        List<ExpenditureModel> expList = dbHandler.getAllExpAmounts();
        int nrOfEvents = expList.size();
        String eventWithHighestValue = "";
        int mostExpEvent = 0;
        String catWithMostExp;
        int catWithMostExpInt;
        if(categoryList.size() == 1){
            catWithMostExp = categoryList.get(0).getName();
            catWithMostExpInt = dbHandler.getAllExpToCategory(catWithMostExp).size(); // Here you could run getAllExp.. but this seems more correct in principle.
        }else{
            catWithMostExp = "";
            catWithMostExpInt = 0;
        }
        Map<String, Integer> testMap = new HashMap<>();
        for(ExpenditureModel exp : expList){
            String category = exp.getRefID();
            if(exp.getAmount() > mostExpEvent){
                mostExpEvent = exp.getAmount();
                eventWithHighestValue = category;
            }
            if(!testMap.containsKey(category)){
                testMap.put(category, 1);
            }else{
                if(testMap.get(category) + 1 > catWithMostExpInt){
                    catWithMostExpInt = testMap.get(category) + 1;
                    catWithMostExp = category;
                    testMap.put(category,catWithMostExpInt);
                }else{
                    testMap.put(category,testMap.get(category)+1);
                }
            }
        }

        String stats = "Number of Categories\t\t\t"+ nrOfCategories +"\n\nNumber of Events\t\t\t"+nrOfEvents+
                "\n\nMost expensive Category\t\t\t"+mostExpensiveCategory+" "+mostExpensiveCat+
                "\n\nMost expensive Event\t\t\t"+eventWithHighestValue+" "+mostExpEvent+
                "\n\nCategory with most Events\t\t\t"+catWithMostExp+" "+catWithMostExpInt;
        TextView listOfStats = findViewById(R.id.listOfStatsView);
        listOfStats.setText(stats);

        int income = 0;
        int expenditure = dbHandler.totalAmount();
        int result = income - expenditure;

        listOfStats = findViewById(R.id.nrOfIncome);
        listOfStats.setText(String.format("%s",income));

        listOfStats = findViewById(R.id.nrOfExpenditure);
        listOfStats.setText(String.format("%s",expenditure));

        listOfStats = findViewById(R.id.nrOfResult);
        listOfStats.setText(String.format("%s",result));
    }
}
