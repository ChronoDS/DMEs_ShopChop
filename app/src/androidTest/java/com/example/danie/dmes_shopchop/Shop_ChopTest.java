package com.example.danie.dmes_shopchop;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.notNullValue;


import static org.junit.Assert.assertEquals;

/**
 * Created by Elad Peleg on 11/01/2018.
 */

public class Shop_ChopTest {

    private UiDevice mDevice;

    private static final String TARGET_PACKAGE =
            InstrumentationRegistry.getTargetContext().getPackageName();
    private static final int LAUNCH_TIMEOUT = 5000;
    @Before
    public void setup() throws UiObjectNotFoundException {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressHome();

        //open list of applications
        //UiObject2 allapps = mDevice.findObject(By.pkg("DMEs_ShopChop"));
        //allapps.click();

        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the blueprint app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(TARGET_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(TARGET_PACKAGE).depth(0)), LAUNCH_TIMEOUT);

    }

    @Test
    public void TestReadMe() throws UiObjectNotFoundException {
        mDevice.wait(Until.hasObject(By.text("onClickReadme")), 3000);
        //UiObject2 calculatorApp = mDevice.findObject(By.desc("onClickReadme"));
        //calculatorApp.click();

        UiObject2 result = mDevice.findObject(By.text("text_about_the_app"));

        //verify result
        //assertEquals("test", result.getText());
    }

    @After
    public void tearDown() {
        mDevice.pressBack();
    }

    private String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }
}
