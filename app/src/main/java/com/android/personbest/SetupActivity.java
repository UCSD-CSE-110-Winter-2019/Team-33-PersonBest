package com.android.personbest;

import android.content.ContentValues;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SetupActivity extends AppCompatActivity {
    private EditText editText;

    public static class DataBaseEntry implements BaseColumns {
        public static final String DATABASE = "database";
        public static final String DATA_TYPE = "data type";
        public static final String VALUE = "value";
    }
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + DataBaseEntry.DATABASE + " (" + DataBaseEntry._ID + " INTEGER PRIMARY KEY," + DataBaseEntry.DATA_TYPE + " TEXT," + DataBaseEntry.VALUE + " TEXT)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DataBaseEntry.DATABASE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_activity);
    }

    public double calculateStrideLength(){
        editText = (EditText)findViewById(R.id.height);
        double height = Double.parseDouble(editText.getText().toString());
        ContentValues values = new ContentValues();
        values.put(DataBaseEntry.DATA_TYPE, "height");
        values.put(DataBaseEntry.VALUE, Double.parseDouble(editText.getText().toString()));
        return 0.414*height;
    }
}
