package com.hayden.joseph.bagwork;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

/**
 * Created by Joseph on 7/11/2016.
 */
public class CustomAnimator {
    static int duration = 500;

    static public void animateTranslateElement(final View view, int xPos1, int xPos2){
        final int lastXPos = xPos2;
        TranslateAnimation animation = new TranslateAnimation(0, (xPos2 - xPos1) - view.getWidth()/2, 0, 0);
        animation.setDuration(duration);
        animation.setFillAfter(false);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setTranslationX(lastXPos - view.getWidth()/2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
        view.startAnimation(animation);
    }

    static public void animateScaleElement(final View view, int initialScaleFactorX,
                                           int newScaleFactorX, int initialScaleFactorY,
                                           int newScaleFactorY){
        ScaleAnimation animation = new ScaleAnimation(initialScaleFactorX, newScaleFactorX, initialScaleFactorY, newScaleFactorY);
        animation.setDuration(duration);
        animation.setFillAfter(false);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
        view.startAnimation(animation);
    }

    static public void animateRemoveElement(final View view, final AnimationHandler animHandler){
        ScaleAnimation animation = new ScaleAnimation(1, 0, 1, 0, view.getX() + view.getWidth()/2, view.getPivotY());
        animation.setDuration(duration);
        animation.setFillAfter(false);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                Bundle data = new Bundle();
                data.putString("type", "remove");
                Message msg = new Message();
                msg.setData(data);
                animHandler.handleMessage(msg);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
        view.startAnimation(animation);    }

    static class AnimationHandler extends Handler {
        final ShotMachine machine;

        public AnimationHandler(ShotMachine sm){
            this.machine = sm;
        }

        @Override
        public void handleMessage(Message msg){
            Bundle data = msg.getData();
            if(data.containsKey("type")){
                if(data.getString("type").equals("remove") && machine.getOutput() != null) {
                    View element = machine.getOutput().getView();
                    RelativeLayout parent = (RelativeLayout) element.getParent();
                    parent.removeView(element);
                }
            }
        }
    }
}
