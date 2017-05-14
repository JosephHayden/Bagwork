package com.hayden.joseph.bagwork;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Joseph on 7/12/2016.
 */
public class ShotMachine {
    private ArrayList<Shot> shotList;
    private int shotListPosition;
    private ShotView shotViewQueue[] = new ShotView[3];
    private ShotView input = null;
    private ShotView output = null;
    private Context ctx;

    public ShotMachine(ArrayList<Shot> shots, Context context){
        this.shotList = (ArrayList<Shot>)shots.clone();
        this.ctx = context;
        this.shotListPosition = 0;
    }

    public void Enqueue(int xPos){
        ShotView shotView = null;
        if(shotListPosition < shotList.size()) {
            shotView = createElement(shotList.get(shotListPosition), xPos);
            shotListPosition++;
        }
        input = shotView;
    }

    public ShotView Step(){
        ShotView retVal = shotViewQueue[shotViewQueue.length-1]; // tail of queue always gets removed
        for(int i = shotViewQueue.length - 1; i >= 1; i--){
            shotViewQueue[i] = shotViewQueue[i-1];
        }
        shotViewQueue[0] = popInput(); // head of queue always gets input
        this.output = retVal;
        return retVal;
    }

    private ShotView popInput(){
        ShotView ret = input; // save input
        input = null; // clear input every time we get it
        return ret;
    }

    public boolean isEmpty(){
        boolean empty = true;
        for(int i = 0; i < shotViewQueue.length; i++){
            if(shotViewQueue[i] != null){
                empty = false;
            }
        }
        return empty;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private ShotView createElement(Shot shot, int xPos){
        TextView ret = new TextView(this.ctx);
        ret.setText(shot.getShortName());
        ret.setBackground(ContextCompat.getDrawable(this.ctx, R.drawable.shape_oval));
        ret.setGravity(Gravity.CENTER);
        ret.setTranslationX(xPos - ret.getBackground().getIntrinsicWidth() / 2);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){
            ret.setId(Utils.generateViewId()); // Packaged implementation of Android 17 feature.
        } else {
            ret.setId(View.generateViewId());
        }
        return new ShotView(ret, shot);
    }

    public ShotView getElement(int position){
        ShotView retShotView = null;
        try {
            retShotView = shotViewQueue[position];
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e("ShotMachine.getElement", e.toString());
        }
        return retShotView;
    }

    public void reInitialize(ArrayList<Shot> shots){
        this.shotList = (ArrayList<Shot>)shots.clone();
        this.shotListPosition = 0;
    }

    public ShotView getOutput(){
        return this.output;
    }

    public int shotListSize(){
        return this.shotList.size();
    }
}
