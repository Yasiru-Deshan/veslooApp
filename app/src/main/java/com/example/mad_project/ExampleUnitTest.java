package com.example.vesloostore;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */


public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    private MainActivity mainActivity;


    public void  setUp(){mainActivity = new MainActivity();}


    @Test
    public void testTot(){
        double result = mainActivity.calctotal(200,2);
        assertEquals(420,result,result);
    }
}