package com.peixun.mycalc;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.Thread.sleep;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CalcPadTest {
    @Rule
    public ActivityTestRule<CalcActivity> mActivityRule = new ActivityTestRule<>(
            CalcActivity.class);

    @Test
    public void wrong_expr() throws Throwable {
        EditText editText = mActivityRule.getActivity().findViewById(R.id.display);
        mActivityRule.runOnUiThread(()->editText.setText("1+?3-4"));

        onView(withId(R.id.equal)).perform(click());
        onView(withId(R.id.display)).check(matches(withText("Error")));
    }

    @Test
    public void wrong_expr_logic() throws Throwable {
        EditText editText = mActivityRule.getActivity().findViewById(R.id.display);

        //mActivityRule.runOnUiThread(()->editText.setText("1+?3-4"))
        mActivityRule.runOnUiThread(()->{
            Logic logic = new Logic(mActivityRule.getActivity(), editText);
            logic.evaluateAndShowResult("1+?3-4");
        });

        onView(withId(R.id.display)).check(matches(withText("Error")));
    }
}
