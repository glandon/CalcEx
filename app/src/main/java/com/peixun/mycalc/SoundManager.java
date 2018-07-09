package com.peixun.mycalc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;

public class SoundManager {
    private static SoundManager mInstance;
    
    private Context mContext;
    
    private AudioManager mAudioManager;
    private int mCurStreamId;
    private boolean mPlaying;
    private SoundPool mSoundPool;
    
    private HashMap<String, Sound> mSoundPoolMap;
    private Vector<String> mSoundQueue = new Vector<String>();

    
    private Handler mHandler = new Handler();
    /**
     * Runnable to Handler
     */
    private Runnable mPlayNext = new Runnable() {

        @Override
        public void run() {
            SoundManager.this.mSoundPool.stop(SoundManager.this.mCurStreamId);
            SoundManager.this.playNextSound();
        }

    };

    /**
     * Single Instance
     * @return
     */
    public synchronized static SoundManager getInstance() {
        if ( mInstance == null ) {
            mInstance = new SoundManager();
        }

        return mInstance;
    }

    /**
     * Will play all sounds one by one
     */
    private void playNextSound() {
        
        if ( mSoundQueue.isEmpty() ) {
            return;
        }
        
    
        String name = (String) mSoundQueue.remove(0);
        Sound sound = (Sound) mSoundPoolMap.get(name);

        /* invalid sound */
        if (sound == null) {
            playNextSound();
            return;
        }

        /* get range of volume */
        float curVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        
        float range = curVolume / maxVolume;

        /* start to play */
        mCurStreamId = mSoundPool.play(sound.id, range, range, 1, 0, 1.0F);
        
        /* wait to play next */
        mPlaying = true;
        mHandler.postDelayed(mPlayNext, sound.time);

    }

    /**
     * Add sound to pool, {name, sound} with HashMap
     * @param name
     * @param resId
     * @param time
     */
    public void addSound(String name, int resId, int time) {
        Sound sound = new Sound(mSoundPool.load(mContext, resId, 1), time);
        mSoundPoolMap.put(name, sound);
    }

    /**
     * Add sound to pool, {name, sound} with HashMap
     * @param name
     * @param file
     * @param time
     */
    public void addSound(String name, AssetFileDescriptor file, int time) {
        Sound sound = new Sound(this.mSoundPool.load(file, 1), time);
        mSoundPoolMap.put(name, sound);
    }

    /**
     * Release all resource
     */
    public void cleanup() {
        unloadAll();
        
        mSoundPool.release();
        mSoundPool = null;
        mInstance = null;
    }

    /**
     * Init sounds pool
     * @param context
     */
    public void initSounds(Context context) {
        mContext = context;
        
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundPoolMap = new HashMap<String, Sound>();
        mAudioManager = ((AudioManager) mContext.getSystemService("audio"));
        
        mPlaying = false;
    }

    /**
     * Play all sounds sequence
     * @param sounds
     */
    public void playSeqSounds(String[] sounds) {
        int len = sounds.length;

        for (int i = 0; i < len; i++) {
            String str = sounds[i];
            mSoundQueue.add(str);
        }

        if (!mPlaying) {
            playNextSound();
        }
    }

    /**
     * play one sound, may need wait pre-sound done
     * @param name
     */
    public void playSound(String name) {
        stopSound();
        mSoundQueue.add(name);
        playNextSound();
    }

    /**
     * stop all sounds play
     */
    public void stopSound() {
        mHandler.removeCallbacks(this.mPlayNext);
        mSoundQueue.clear();
        mSoundPool.stop(this.mCurStreamId);
        mPlaying = false;
    }

    /**
     * unload all resource
     */
    public void unloadAll() {

        stopSound();
        
        if ( mSoundPoolMap.size() == 0 ) {
            return;
        }
        
        Iterator<String> iter = null;
        iter = mSoundPoolMap.keySet().iterator();

        while (iter.hasNext()) {
            String str = (String) iter.next();
            mSoundPool.unload(((Sound) this.mSoundPoolMap.get(str)).id);
        }

        mSoundPoolMap.clear();
       
    }

    /**
     * Internal use by SoundManager
     * @author Honey
     *
     */
    private final class Sound {
        public int id;
        public int time;

        public Sound(int id, int time) {
            this.id = id;
            this.time = time;
        }
    }

}
