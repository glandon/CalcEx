package com.peixun.mycalc;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;
import android.widget.EditText;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CalcRobotiumTest {

    private Solo solo;
    @Rule
    public ActivityTestRule<CalcActivity> activityTestRule =
            new ActivityTestRule<>(CalcActivity.class);

    @Before
    public void initSolo() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),
                activityTestRule.getActivity());
    }

    @Test
    public void wrong_expr_with_robotium() throws Throwable {
        EditText display = (EditText) solo.getView(R.id.display);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(()->display.setText("1-?2+3"));

        Button equal = (Button)solo.getView(R.id.equal);
        solo.clickOnView(equal);
        solo.sleep(1000);

        assertEquals(solo.getString(R.string.error), display.getText().toString());
    }
}
