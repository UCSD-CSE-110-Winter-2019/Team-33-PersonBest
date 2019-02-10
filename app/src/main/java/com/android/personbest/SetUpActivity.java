package com.android.personbest;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetupActivity extends AppCompatActivity {
    private EditText editText;
    private Button saveButton;

    public static class DataBaseEntry implements BaseColumns {
        public static final String DATABASE = "database";
        public static final String DATA_TYPE = "data type";
        public static final String VALUE = "value";
    }
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + DataBaseEntry.DATABASE + " (" + DataBaseEntry._ID + " INTEGER PRIMARY KEY," + DataBaseEntry.DATA_TYPE + " TEXT," + DataBaseEntry.VALUE + " TEXT)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DataBaseEntry.DATABASE;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_activity);

        initViews();
        initPreferences();
        if (saveButton != null)
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save();
                }
            });

    }

    private void initViews() {
        saveButton = findViewById(R.id.save_button);
        editText = (EditText)findViewById(R.id.height);
    }

    public float calculateStrideLength(float height) {
            ContentValues values = new ContentValues();
            //values.put(DataBaseEntry.DATA_TYPE, "height");
            //values.put(DataBaseEntry.VALUE, Float.parseFloat(editText.getText().toString()));
            return (float)(0.414 * height);
    }

    public void initPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);

        Float a = sharedPreferences.getFloat("height", 0);
        if (editText != null) {
            editText.setText(a.toString());
        }
    }

    public void save() {

        SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        float height = Float.parseFloat(editText.getText().toString());
        float stride = calculateStrideLength(height);

        editor.putFloat("stride", stride);
        editor.putFloat("height", height);


        editor.apply();
        Toast.makeText(SetupActivity.this, "Saved", Toast.LENGTH_SHORT).show();
    }
}
