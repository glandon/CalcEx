package com.peixun.mycalc;

import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class CalcActivity extends Activity {
    EventListener mListener = new EventListener();
    private SoundManager mSoundMgr;
    private Logic mLogic;
    private EditText mDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_calc);

        mSoundMgr = SoundManager.getInstance();
        mSoundMgr.initSounds(this);
        
        mDisplay = (EditText)findViewById(R.id.display);
        mLogic = new Logic(this, mDisplay);
        mLogic.setLineLength(getResources().getInteger(R.integer.max_digits));
        
        mListener.setHandler(mLogic);
      

        final TypedArray buttons = getResources().obtainTypedArray(
                R.array.simple_buttons);

        for (int i = 0; i < buttons.length(); i++) {
            setOnClickListener(null, buttons.getResourceId(i, 0));
        }
        
        buttons.recycle();
        
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calc, menu);
        return true;
    }

    /**
     * voice & haptic settings
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings ) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        mSoundMgr.cleanup();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        
        boolean voice = preferences.getBoolean("voice_on", true);
        boolean haptic = preferences.getBoolean("haptic_on", true);

        mListener.mbVoice = voice;
        mListener.mbHaptic = haptic;

        if (voice) {
            new SoundLoadTask(this).execute(mSoundMgr);
        }
    }

    void setOnClickListener(View root, int id) {
        final View target = root != null ? root.findViewById(id)
                : findViewById(id);
        target.setOnClickListener(mListener);
    }

    class SoundLoadTask extends AsyncTask<SoundManager, Void, Void> {
        CalcActivity mContext;
        ProgressDialog mDialog;

        SoundLoadTask(CalcActivity context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(SoundManager... arg0) {
            SoundManager mgr = (SoundManager) arg0[0];
            mgr.unloadAll();

            mgr.addSound("1", R.raw.one, 320);
            mgr.addSound("2", R.raw.two, 274);
            mgr.addSound("3", R.raw.three, 304);
            mgr.addSound("4", R.raw.four, 215);
            mgr.addSound("5", R.raw.five, 388);
            mgr.addSound("6", R.raw.six, 277);
            mgr.addSound("7", R.raw.seven, 447);
            mgr.addSound("8", R.raw.eight, 274);
            mgr.addSound("9", R.raw.nine, 451);
            mgr.addSound("0", R.raw.zero, 404);

            mgr.addSound("AC", R.raw.ac, 696);
            mgr.addSound("DEL", R.raw.del, 442);
            mgr.addSound("+", R.raw.plus, 399);
            // mgr.addSound("-", R.raw.minus, 530);
            // mgr.addSound("*", R.raw.mul, 321);
            // mgr.addSound("/", R.raw.div, 321);

            mgr.addSound(mContext.getString(R.string.minus), R.raw.minus, 530);
            mgr.addSound(mContext.getString(R.string.mul), R.raw.mul, 321);
            mgr.addSound(mContext.getString(R.string.div), R.raw.div, 321);

            mgr.addSound("=", R.raw.equal, 480);
            mgr.addSound(".", R.raw.dot, 454);
            return null;

        }

        protected void onPostExecute(Void paramVoid) {
            mDialog.dismiss();
        }

        protected void onPreExecute() {
            mDialog = ProgressDialog.show(mContext, "",
                    mContext.getString(R.string.loadingvoice), true);
            mDialog.setCancelable(false);
        }
    }
}
