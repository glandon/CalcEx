package com.peixun.mycalc;

import android.content.Context;
import android.content.res.Resources;
import android.widget.EditText;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CalcSettingsTest {
    @Test
    public void wrong_expr() {
        EditText editText = mock(EditText.class);
        Context context = mock(Context.class);
        Resources resources = mock(Resources.class);

        when(context.getResources()).thenReturn(resources);
        when(resources.getString(R.string.error)).thenReturn("error");

        Logic logic = new Logic(context, editText);
        logic.evaluateAndShowResult("1?+3-4");

        verify(resources).getString(R.string.error);
        Assert.assertEquals(logic.getResult(), "error");
    }
}