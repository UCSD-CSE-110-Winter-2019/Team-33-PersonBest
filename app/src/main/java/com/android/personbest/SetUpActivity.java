package com.android.personbest;

import android.content.ContentValues;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class SetUpActivity extends AppCompatActivity {
    private EditText editText;

    public static class DataBaseEntry implements BaseColumns {
        public static final String DATABASE = "database";
        public static final String DATA_TYPE = "data type";
        public static final String VALUE = "value";
    }
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + MainActivity.DataBaseEntry.DATABASE + " (" + MainActivity.DataBaseEntry._ID + " INTEGER PRIMARY KEY," + MainActivity.DataBaseEntry.DATA_TYPE + " TEXT," + MainActivity.DataBaseEntry.VALUE + " TEXT)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + MainActivity.DataBaseEntry.DATABASE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public double calculateStrideLength(){
        editText = (EditText)findViewById(R.id.height);
        double height = Double.parseDouble(editText.getText().toString());
        ContentValues values = new ContentValues();
        values.put(MainActivity.DataBaseEntry.DATA_TYPE, "height");
        values.put(MainActivity.DataBaseEntry.VALUE, Double.parseDouble(editText.getText().toString()));
        return 0.414*height;
    }
}

