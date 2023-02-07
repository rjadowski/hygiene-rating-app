package mmu.ac.hygienerating;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.input);
        Button locationSearch = findViewById(R.id.locationSearch);
        Button postcodeSearch = findViewById(R.id.postcodeSearch);
        Button nameSearch = findViewById(R.id.nameSearch);
        Button recentSearch = findViewById(R.id.recentSearch);

        //Starts CurrentLocation activity
        locationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, CurrentLocation.class);
                startActivity(intent);
            }
        });

        //Starts NameSearch Activity, sends search data from input field
        nameSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (input.getText().toString().equals("")) {
                    input.setHint("Enter search here");
                    return;
                }
                Intent intent = new Intent(MainActivity.this, NameSearch.class);
                intent.putExtra("mmu.ac.hygienerating.SEARCH_DATA", input.getText().toString());
                startActivity(intent);
            }
        });

        //Starts PostcodeSearch Activity, sends search data from input field
        postcodeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().equals("")) {
                    input.setHint("Enter postcode here");
                    return;
                }
                Intent intent = new Intent(MainActivity.this, PostcodeSearch.class);
                intent.putExtra("mmu.ac.hygienerating.SEARCH_DATA", input.getText().toString());
                startActivity(intent);
            }
        });

        //Starts RecentSearch Activity
        recentSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, RecentSearch.class);
                startActivity(intent);
            }
        });
    }
}
