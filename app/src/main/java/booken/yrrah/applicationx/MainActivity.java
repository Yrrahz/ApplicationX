package booken.yrrah.applicationx;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comparators.sortAlphabetically;
import comparators.sortByAmount;
import comparators.sortLastModified;
import comparators.sortMostFreq;
import models.CategoryModel;
import models.ExpenditureModel;

public class MainActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private static final String categoryTitle = "item"; // These two variables...
    private static final String categoryData = "subItem"; // ... are final for a reason.
    List<Map<String, String>> data = new ArrayList<>();
    List<CategoryModel> categoryModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_add_income:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add Income");
                Context context = this;
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText amountBox = new EditText(context);
                amountBox.setHint("Add Amount");
                amountBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                layout.addView(amountBox);

                final TextView categoryText = new TextView(context);
                categoryText.setText(R.string.chooseCategory);
                layout.addView(categoryText);

                final Spinner popupSpinner = new Spinner(context, Spinner.MODE_DIALOG);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, getAllCategoriesList());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                popupSpinner.setAdapter(adapter);
                layout.addView(popupSpinner);

                builder.setView(layout);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String testAmount = amountBox.getText().toString();
                        if(testAmount.isEmpty() || Integer.parseInt(testAmount) == 0 || categoryModelList.size() == 0){
                            dialog.cancel();
                        }else{
                            insertAmount(popupSpinner.getSelectedItem().toString(),Integer.parseInt(testAmount), "Income Event");
                            updateMainList(categoryModelList);
                            updateTotalAmount();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            case R.id.menu_stats:
                Intent intent = new Intent(this, StatsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_how_to:
                Toast.makeText(getApplicationContext(),"Tutorial Initiated!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_settings:
                //Date
                Toast.makeText(getApplicationContext(),"Settings Initiated!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_about:
                sortingPopupMenu(findViewById(R.id.menu_about));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This is the menu for sorting the 'second' menu containing the sorting options.
     * It is to be only containing sorting options but could contain anything.
     * @param v - the view which this menu should be based from. In this case the 'About' option from the top menu.
     */
    public void sortingPopupMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_sort_alpha:
                        Collections.sort(categoryModelList, new sortAlphabetically());
                        updateMainList(categoryModelList);
                        Toast.makeText(getApplicationContext(),"Sorted Alphabetically!", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_sort_value:
                        Collections.sort(categoryModelList, new sortByAmount());
                        updateMainList(categoryModelList);
                        Toast.makeText(getApplicationContext(),"Sorted by Value!", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_sort_last_modified:
                        Collections.sort(categoryModelList, new sortLastModified(dbHandler.getAllExpAmounts()));
                        updateMainList(categoryModelList);
                        Toast.makeText(getApplicationContext(),"Last Modified Sorted!", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_sort_frequency:
                        Collections.sort(categoryModelList, new sortMostFreq());
                        updateMainList(categoryModelList);
                        Toast.makeText(getApplicationContext(),"Frequency Sorted!", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sort_menu, popup.getMenu());
        popup.show();
    }

    public void categoryButtonPressed(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new Category:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT); // input type, such as normal text or pw...
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String testNewCategoryName = input.getText().toString();
                if(testNewCategoryName.isEmpty()){
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(),"Category Name is not valid!", Toast.LENGTH_SHORT).show();
                }else{
                    insertNewCategory(testNewCategoryName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * This method takes the input from the user and first, takes away all special characters, trims
     * the input and removes excess whitespaces. Then checks if the Category already exists in the
     * database. If it does, a toast saying it does will appear, if not, create a new Category into
     * the database with totalValue of 0.
     *
     * @param newCategory - The input from the user to what the new Category name should be
     */
    private void insertNewCategory(String newCategory){
        newCategory = newCategory.replaceAll("[ ](?=[ ])|[^-_,A-Za-z0-9 ]+","").trim();
        if(newCategory.isEmpty()){
            Toast.makeText(getApplicationContext(),"Category Name is not valid!", Toast.LENGTH_SHORT).show();
            return;
        }
        newCategory = newCategory.substring(0,1).toUpperCase() + newCategory.substring(1).toLowerCase();

        CategoryModel cm = new CategoryModel(newCategory,0);

        if(!categoryModelList.contains(cm)){
            dbHandler.addCategory(cm);
            categoryModelList.add(cm);

            updateMainList(categoryModelList);

            Toast.makeText(getApplicationContext(),"Category Added!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Category Already Exists!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUp(){
        this.dbHandler = new DBHandler(this);
        dbHandler.fyllDb();
        this.categoryModelList = dbHandler.getAllCategories();

        updateMainList(categoryModelList);
        updateTotalAmount();
    }

    private void updateMainList(final List<CategoryModel> categoryList){
        data.clear();

        for(CategoryModel cm : categoryList){
            Map<String, String> listViewElement = new HashMap<>(2);
            listViewElement.put(categoryTitle, cm.getName());
            listViewElement.put(categoryData,"Total Amount: "+cm.getTotalAmount());

            data.add(listViewElement);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2, // <-- Standard lib item, contains both Item and SubItem in listView
                new String[] {categoryTitle,categoryData}, // <-- must be same as dateMap's keys
                new int[] {android.R.id.text1, android.R.id.text2});

        ListView presentData = findViewById(R.id.listCategories);

        presentData.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            // OnClick for main List
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){

                final String categoryName = data.get(position).get(categoryTitle);
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Insert amount for "+categoryName);

                final EditText input = new EditText(v.getContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER); // input type, such as normal text or pw...
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    // OnClick for the AlertBuilder handling Expenditure
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String testAmount = input.getText().toString();
                        if(testAmount.isEmpty() || Integer.parseInt(testAmount) == 0){ // If the user thought it was a good idea to add an event with the value 0... dumb ass...
                            dialog.cancel();
                        }else {
                            insertAmount(categoryName, Integer.parseInt(input.getText().toString()), "Expenditure Event");
                            updateMainList(categoryModelList);
                            updateTotalAmount();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        presentData.setAdapter(adapter);
    }

    private void insertAmount(String categoryName, int amount, String event){
        dbHandler.addExpenditure(new ExpenditureModel(amount, event, categoryName, 0));

        CategoryModel cm = new CategoryModel(categoryName,1);
        int indexOfCategory = categoryModelList.indexOf(cm);
        if(event.equals("Expenditure Event")){
            cm.setTotalAmount(categoryModelList.get(indexOfCategory).getTotalAmount() + amount);
        }else{
            cm.setTotalAmount(categoryModelList.get(indexOfCategory).getTotalAmount());
        }
        cm.setDate(categoryModelList.get(indexOfCategory).getDate() + 1);

        categoryModelList.set(indexOfCategory, cm);
    }

    private void updateTotalAmount(){
        Integer totalAmount = dbHandler.totalAmount();
        TextView totalResult = findViewById(R.id.textViewTotalResult);
        totalResult.setText(String.format("%s",totalAmount.toString()));
    }

    private List<String> getAllCategoriesList(){
        List<String> returnCategoryNames = new ArrayList<>();

        for(CategoryModel cm : dbHandler.getAllCategories()){
            returnCategoryNames.add(cm.getName());
        }

        if(returnCategoryNames.size() == 0 ){
            returnCategoryNames.add("No Category!");
            return returnCategoryNames;
        }
        return returnCategoryNames;
    }
}
