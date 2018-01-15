package com.example.danie.dmes_shopchop;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Created by Elad Peleg on 11/01/2018.
 */

@RunWith(AndroidJUnit4.class)
public class DMEs_ShopChop {

    private UiDevice mDevice;

    @Before
    public void setup() throws UiObjectNotFoundException{
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressHome();

        //open list of applications
        UiObject2 allapps = mDevice.findObject(By.desc("Apps"));
        allapps.click();

        //open DMEs_ShopChop application
        mDevice.wait(Until.hasObject(By.text("DMEs_ShopChop")), 3000);
        UiObject2 shop_chop_app = mDevice.findObject(By.desc("DMEs_ShopChop"));
        shop_chop_app.click();
    }


    @Test
    public void TestReadMe() throws UiObjectNotFoundException {
        mDevice.wait(Until.hasObject(By.text("Readme")), 3000);
        UiObject2 calculatorApp = mDevice.findObject(By.desc("Readme"));
        calculatorApp.click();

        UiObject2 result = mDevice.findObject(By.text("text_about_the_app"));

        //verify result
        assertEquals("test", result.getText());
    }


    @After
    public void tearDown() {
        mDevice.pressBack();
    }

}
