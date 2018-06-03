package booken.yrrah.applicationx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testDB();
    }

    public void testButtonPressed(View view){
        //Button testButton = findViewById(R.id.testButton);

        TextView testTextDB = findViewById(R.id.helloW);
        String test = Integer.toString(dbHandler.getCategoryModel("TestCat").getTotalAmount());
        testTextDB.setText(test);
    }

    private void testDB(){
        this.dbHandler = new DBHandler(this);
        dbHandler.fyllDb();

//        List<CategoryModel> listCm = dbHandler.getAllCategories();


        TextView testTextDB = findViewById(R.id.helloW);
        testTextDB.setText(dbHandler.getCategoryModel("TestCat").getName());
    }
}
