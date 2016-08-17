package com.czettner.bookingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_SEARCH_STRING = "com.czettner.bookingapp.SEARCHTEXT";

    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton = (Button) findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                EditText searchEditText = (EditText) findViewById(R.id.search_text);
                String searchText = searchEditText.getText().toString();
                intent.putExtra(EXTRA_SEARCH_STRING, searchText);
                startActivity(intent);
            }
        });
    }
}
