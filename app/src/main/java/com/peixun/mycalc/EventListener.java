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

import java.util.Arrays;

import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;

class EventListener implements View.OnClickListener {
    Logic mHandler;
    boolean mbHaptic;
    boolean mbVoice;

    void setHandler(Logic handler) {
        mHandler = handler;
    }

    @Override
    public void onClick(View view) {

        if (mbVoice) {
            SoundManager.getInstance().playSound(
                    ((Button) view).getText().toString());
        }

        if (mbHaptic) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                            | HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        }

        switch (view.getId()) {
        case R.id.del:
            mHandler.onDelete();
            break;

        case R.id.ac:
            mHandler.onClear();
            break;

        case R.id.equal:
            mHandler.onEnter();

            if (mbVoice) {
                String result = mHandler.getText().toString();
                String[] sounds = new String[result.length()];

                for (int i = 0; i < result.length(); i++) {
                    sounds[i] = String.valueOf(result.charAt(i));
                }

                SoundManager.getInstance().playSeqSounds(sounds);
            }
            break;

        default:
            if (view instanceof Button) {
                String text = ((Button) view).getText().toString();
                mHandler.insert(text);
            }
        }
    }

}
