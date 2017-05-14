package com.hayden.joseph.bagwork;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Joseph on 8/1/2016.
 */
public class EditTimeText extends EditText {
    public EditTimeText(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        this.setCursorVisible(false);
    }

    public EditTimeText(Context context, AttributeSet attrs){
        super(context, attrs);
        this.setCursorVisible(false);
    }

    public EditTimeText(Context context){
        super(context);
        this.setCursorVisible(false);
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd){
        if(selStart != this.getText().length()){
            this.setSelection(this.getText().length());
        }
    }
}
