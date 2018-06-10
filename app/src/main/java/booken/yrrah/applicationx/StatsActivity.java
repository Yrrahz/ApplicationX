package booken.yrrah.applicationx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        setUp();
    }

    private void setUp(){
        TextView listOfStats = findViewById(R.id.listOfStatsView);

        String stats = "Number of Categories\t\t\tthis many\n\nNumber of Events\t\t\tthat many\n\n" +
                "Most expensive Catg.\t\t\thow many\n\nMost expensive Event\t\t\twhat many\n\n" +
                "Catg. with most Events\t\t\twhere many";
        listOfStats.setText(stats);
    }
}
