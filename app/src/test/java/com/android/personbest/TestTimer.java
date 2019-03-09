package com.android.personbest;


import com.android.personbest.Timer.ITimer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestTimer {
    @Test
    public void testIsValidDayStr() {
        assertTrue(ITimer.isValidDayStr("11/12/1024"));
        assertTrue(ITimer.isValidDayStr("02/28/2018"));
        assertTrue(ITimer.isValidDayStr("08/30/2017"));
        assertTrue(ITimer.isValidDayStr("12/12/0001"));
        assertTrue(ITimer.isValidDayStr("01/01/1024"));
        assertTrue(ITimer.isValidDayStr("01/30/1024"));
        assertTrue(ITimer.isValidDayStr("12/30/1024"));
    }

    @Test
    public void testIsInValidDayStr() {
        assertFalse(ITimer.isValidDayStr("4/11/12"));
        assertFalse(ITimer.isValidDayStr(""));
        assertFalse(ITimer.isValidDayStr("12321321321312"));
        assertFalse(ITimer.isValidDayStr("00/00/0000"));
        assertFalse(ITimer.isValidDayStr("00/01/0000"));
        assertFalse(ITimer.isValidDayStr("00/00/0001"));
        assertFalse(ITimer.isValidDayStr("13/01/2019"));
        assertFalse(ITimer.isValidDayStr("12/32/2019"));
        assertFalse(ITimer.isValidDayStr("12/33/2019"));
    }

    @Test
    public void testGetYearFromDayStr() {
        assertEquals(1024, ITimer.getYearFromDayStr("01/02/1024"));
    }

    @Test
    public void testGetMonthFromDayStr() {
        assertEquals(1,ITimer.getMonthFromDayStr("01/02/1024"));
    }

    @Test
    public void testGetDayFromDayStr() {
        assertEquals(2, ITimer.getDayFromDayStr("01/02/1024"));
    }

    @Test
    public void testLastMonthDaysStr() {
        List<String> ls = ITimer.getLastMonthDayStrings("02/27/2018");
        assertEquals(28,ls.size());

        ArrayList<String> validLs = new ArrayList<>();
        validLs.add("02-26-2018");
        validLs.add("02-25-2018");
        validLs.add("02-24-2018");
        validLs.add("02-23-2018");
        validLs.add("02-22-2018");
        validLs.add("02-21-2018");
        validLs.add("02-20-2018");
        validLs.add("02-19-2018");
        validLs.add("02-18-2018");
        validLs.add("02-17-2018");
        validLs.add("02-16-2018");
        validLs.add("02-15-2018");
        validLs.add("02-14-2018");
        validLs.add("02-13-2018");
        validLs.add("02-12-2018");
        validLs.add("02-11-2018");
        validLs.add("02-10-2018");
        validLs.add("02-09-2018");
        validLs.add("02-08-2018");
        validLs.add("02-07-2018");
        validLs.add("02-06-2018");
        validLs.add("02-05-2018");
        validLs.add("02-04-2018");
        validLs.add("02-03-2018");
        validLs.add("02-02-2018");
        validLs.add("02-01-2018");
        validLs.add("01-31-2018");
        validLs.add("01-30-2018");

        for(int i = 0; i < validLs.size(); ++i) {
            assertEquals(validLs.get(i), ls.get(i));
        }
    }
}
