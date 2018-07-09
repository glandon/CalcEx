/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.peixun.mycalc;

import android.view.KeyEvent;
import android.widget.EditText;
import android.content.Context;
import java.util.Locale;
import org.javia.arity.Symbols;
import org.javia.arity.SyntaxException;

class Logic {
    private String mResult = "";
    private boolean mIsError = false;
    private int mLineLength = 0;

    private Symbols mSymbols = new Symbols();
    private EditText mDisplay;

    // the two strings below are the result of Double.toString() for Infinity &
    // NaN
    // they are not output to the user and don't require internationalization
    private static final String INFINITY_UNICODE = "\u221e";
    private static final String INFINITY = "Infinity";
    private static final String NAN = "NaN";

    static final char MINUS_UNICODE = '\u2212';

    private final String mErrorString;
    private Context mContext;

    
    /**
     * Get expression form display and calculate it.
     * @param context
     * @param display
     */
    Logic(Context context, EditText display) {
        mContext = context;
        mErrorString = mContext.getResources().getString(R.string.error);
        mDisplay = display;
    }

    void setLineLength(int nDigits) {
        mLineLength = nDigits;
    }

    public String getText() {
        return mDisplay.getText().toString();
    }

    public String getResult() {
        return mResult;
    }


    boolean acceptInsert(String delta) {
        String text = getText();
        return !mIsError &&
            (!mResult.equals(text) ||
             isOperator(delta) ||
             mDisplay.getSelectionStart() != text.length());
    }
    
    /**
     * append number/operator at the end
     * @param delta
     */
    void insert(String delta) {
        
        if (!acceptInsert(delta)) {
            clear();
        } 
        
        int cursor = mDisplay.getSelectionStart();
        mDisplay.getText().insert(cursor, delta);
      
    }

    /**
     * clear expression
     */
    private void clear() {
        mDisplay.setText("");
        mResult = "";
        mIsError = false;
    }

    /**
     * On press "DEL":
     * 1) if error, clear text
     * 2) if result, clear text
     * 3) else, just delete one by one
     */
    void onDelete() {
        if (getText().equals(mResult) || mIsError) {
            clear();
        } else {
            mDisplay.dispatchKeyEvent(new KeyEvent(0, KeyEvent.KEYCODE_DEL));
            mResult = "";
        }
    }

    /**
     * On press "AC"
     */
    void onClear() {
        clear();
    }

    /**
     * On press "="
     */
    void onEnter() {
        evaluateAndShowResult(getText());
    }

    /**
     * Get expression to evaluate expression
     * @param text
     */
    public void evaluateAndShowResult(String text) {
        try {
            String result = evaluate(text);

            if (!text.equals(result)) {
                mResult = result;
                mDisplay.setText(mResult);
                mDisplay.setSelection(mResult.length());
            }
        } catch (SyntaxException e) {
            mIsError = true;
            mResult = mErrorString;
            mDisplay.setText(mResult);
        }
    }


    /**
     * evaluate expression
     * @param input
     * @return
     * @throws SyntaxException
     */
    private String evaluate(String input) throws SyntaxException {
        if (input.trim().equals("")) {
            return "";
        }

        // drop final infix operators (they can only result in error)
        int size = input.length();
        while (size > 0 && isOperator(input.charAt(size - 1))) {
            input = input.substring(0, size - 1);
            --size;
        }

        double value = mSymbols.eval(input);

        String result = "";
        for (int precision = mLineLength; precision > 6; precision--) {
            result = tryFormattingWithPrecision(value, precision);
            if (result.length() <= mLineLength) {
                break;
            }
        }
        return result.replace('-', MINUS_UNICODE).replace(INFINITY,
                INFINITY_UNICODE);
    }

    private String tryFormattingWithPrecision(double value, int precision) {
        // The standard scientific formatter is basically what we need. We will
        // start with what it produces and then massage it a bit.
        String result = String.format(Locale.US, "%" + mLineLength + "."
                + precision + "g", value);
        
        if (result.equals(NAN)) { // treat NaN as Error
            mIsError = true;
            return mErrorString;
        }
        
        String mantissa = result;
        String exponent = null;
        
        int e = result.indexOf('e');
        
        if (e != -1) {
            mantissa = result.substring(0, e);

            // Strip "+" and unnecessary 0's from the exponent
            exponent = result.substring(e + 1);
            if (exponent.startsWith("+")) {
                exponent = exponent.substring(1);
            }
            exponent = String.valueOf(Integer.parseInt(exponent));
        } else {
            mantissa = result;
        }

        int period = mantissa.indexOf('.');
        if (period == -1) {
            period = mantissa.indexOf(',');
        }
        
        if (period != -1) {
            // Strip trailing 0's
            while (mantissa.length() > 0 && mantissa.endsWith("0")) {
                mantissa = mantissa.substring(0, mantissa.length() - 1);
            }
        
            if (mantissa.length() == period + 1) {
                mantissa = mantissa.substring(0, mantissa.length() - 1);
            }
        }

        if (exponent != null) {
            result = mantissa + 'e' + exponent;
        } else {
            result = mantissa;
        }
        
        return result;
    }

    static boolean isOperator(String text) {
        return text.length() == 1 && isOperator(text.charAt(0));
    }

    
    /**
     * plus minus mul div
     * Encode UNICODE
     * @param c
     * @return
     */
    static boolean isOperator(char c) {
        return "+\u2212\u00d7\u00f7/*".indexOf(c) != -1;
    }
}
