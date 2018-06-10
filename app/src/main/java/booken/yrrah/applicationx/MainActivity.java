package booken.yrrah.applicationx;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private static final String categoryTitle = "item"; // These two variables...
    private static final String categoryData = "subItem"; // ... are final for a reason.
    List<Map<String, String>> data = new ArrayList<>();

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
            case R.id.menu_stats:
                Intent intent = new Intent(this, StatsActivity.class);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(),"Stats pressed!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_about:
                Toast.makeText(getApplicationContext(),"About pressed!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                insertNewCategory(input.getText().toString());
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
        newCategory = newCategory.substring(0,1).toUpperCase() + newCategory.substring(1).toLowerCase();

        List<CategoryModel> categoryList = dbHandler.getAllCategories();
        CategoryModel cm = new CategoryModel(newCategory,0);

        if(!categoryList.contains(cm)){
            dbHandler.addCategory(cm);
            categoryList.add(cm);

            updateMainList(categoryList);

            Toast.makeText(getApplicationContext(),"Category Added!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Category Already Exists!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUp(){
        this.dbHandler = new DBHandler(this);
        dbHandler.fyllDb();

        List<CategoryModel> listCm = dbHandler.getAllCategories();

        updateMainList(listCm);
        updateTotalAmount();
    }

    private void updateMainList(List<CategoryModel> categoryList){
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
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){

                insertAmount(data.get(position).get(categoryTitle));

            }
        });

        presentData.setAdapter(adapter);
    }

    private void insertAmount(final String categoryName){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insert amount for "+categoryName);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER); // input type, such as normal text or pw...
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHandler.addExpenditure(new ExpenditureModel(Integer.parseInt(input.getText().toString()),"inserted event",categoryName,5));
                updateMainList(dbHandler.getAllCategories());
                updateTotalAmount();
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

    private void updateTotalAmount(){
        Integer totalAmount = dbHandler.totalAmount();
        TextView totalResult = findViewById(R.id.textViewTotalResult);
        totalResult.setText(String.format("%s",totalAmount.toString()));
    }
}
