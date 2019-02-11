package com.android.personbest;

import android.widget.EditText;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class TestSetUpActivity {
    @Test
    public void testSetUpActivity(){
        SetUpActivity activity = Robolectric.setupActivity(SetUpActivity.class);
        EditText isVisible = activity.findViewById(R.id.height);
        isVisible.setText("185");
        assertEquals(isVisible.getText(), 185);
    }
}
