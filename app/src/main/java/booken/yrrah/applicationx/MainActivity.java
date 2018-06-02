package booken.yrrah.applicationx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testDB();
    }

    private void testDB(){
        DBHandler dbHandler = new DBHandler(this);

        TextView testTextDB = findViewById(R.id.helloW);

        testTextDB.setText("hej");
    }
}
