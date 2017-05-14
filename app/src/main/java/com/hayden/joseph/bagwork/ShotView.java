package com.hayden.joseph.bagwork;

import android.view.View;

/**
 * Holds the view assigned to a given shot during SessionActivity. Seems to be unnecessary, might remove.
 */
public class ShotView {
    private View view;
    private Shot shot;
    public ShotView(View view, Shot shot){
        this.view = view;
        this.shot = shot;
    }
    public View getView(){
        return this.view;
    }

    public Shot getShot(){ return this.shot; }
}
