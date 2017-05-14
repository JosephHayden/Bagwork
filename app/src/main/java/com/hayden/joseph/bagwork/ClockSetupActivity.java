package com.hayden.joseph.bagwork;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Joseph on 7/30/2016.
 */
public class ClockSetupActivity extends Activity {
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.ctx = this;
        setContentView(R.layout.activity_clock_setup);

        final EditText roundSeconds = (EditText) findViewById(R.id.roundLengthSeconds);
        roundSeconds.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                int max = 59;
                if (!s.equals("")) {
                    if (s.length() > 2) {
                        s.replace(0, s.length(), s.subSequence(1, 3));
                    } else if (s.length() < 2) {
                        s.replace(0, 1, "00");
                    } else {
                        int num = Integer.parseInt(s.toString());
                        if (num > max) {
                            s.replace(0, s.length(), "" + max);
                        }
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        /*roundSeconds.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.color.light_red);
                } else {
                    v.setBackgroundResource(R.color.dark_red);
                }
            }
        });*/

        final EditText roundMinutes = (EditText) findViewById(R.id.roundLengthMinutes);
        roundMinutes.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!s.equals("")) {
                    if (s.length() > 2) {
                        s.replace(0, s.length(), s.subSequence(1, 3));
                    } else if (s.length() < 2) {
                        s.replace(0, 1, "00");
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        final EditText restSeconds = (EditText) findViewById(R.id.restLengthSeconds);
        restSeconds.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                int max = 59;
                if (!s.equals("")) {
                    if(s.length() > 2){
                        s.replace(0, s.length(), s.subSequence(1, 3));
                    } else if (s.length() < 2){
                        s.replace(0, 1, "00");
                    } else {
                        int num = Integer.parseInt(s.toString());
                        if (num > max) {
                            s.replace(0, s.length(), "" + max);
                        }
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        final EditText restMinutes = (EditText) findViewById(R.id.restLengthMinutes);
        restMinutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.equals("")) {
                    if (s.length() > 2) {
                        s.replace(0, s.length(), s.subSequence(1, 3));
                    } else if (s.length() < 2) {
                        s.replace(0, 1, "00");
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    public void startClock(View v){
        long roundLength = (long)(Integer.parseInt(((EditText)findViewById(R.id.roundLengthMinutes)).getText().toString()))*1000*60 +
                (long)(Integer.parseInt(((EditText)findViewById(R.id.roundLengthSeconds)).getText().toString()))*1000;
        long restLength = (long)(Integer.parseInt(((EditText)findViewById(R.id.restLengthMinutes)).getText().toString()))*1000*60 +
                (long)(Integer.parseInt(((EditText)findViewById(R.id.restLengthSeconds)).getText().toString()))*1000;
        int numRounds = Integer.parseInt(((EditText)findViewById(R.id.numRounds)).getText().toString());
        Bundle extras = new Bundle();
        extras.putLong("roundLength", roundLength);
        extras.putLong("restLength", restLength);
        extras.putInt("maxRounds", numRounds);
        Intent intent = new Intent(this, ClockActivity.class);
        intent.putExtras(extras);
        startActivity(intent);
    }
}