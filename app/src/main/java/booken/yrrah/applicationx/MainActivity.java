package booken.yrrah.applicationx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private static final String categoryTitle = "item"; // These two variables...
    private static final String categoryData = "subItem"; // ... are final for a reason.
    List<CategoryModel> listOfCategories;
    List<Map<String, String>> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUp();
    }

    public void categoryButtonPressed(View view){
        //Button testButton = findViewById(R.id.testButton);

        TextView testTextDB = findViewById(R.id.textViewTotalAmount);
        String test = Integer.toString(dbHandler.getCategoryModel("TestCat").getTotalAmount());
        testTextDB.setText(test);
    }

    private void setUp(){
        this.dbHandler = new DBHandler(this);
        data.clear();
        dbHandler.fyllDb(); // TODO : Remove for obvious reasons.
        //this.listOfCategories = dbHandler.getAllCategories();

        List<CategoryModel> listCm = dbHandler.getAllCategories();


//        TextView testTextDB = findViewById(R.id.helloW);
//        testTextDB.setText(dbHandler.getCategoryModel("TestCat").getName());


        for(CategoryModel cm: listCm){
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
        //presentData.setOnItemClickListener(this);
        presentData.setAdapter(adapter);
    }


}
