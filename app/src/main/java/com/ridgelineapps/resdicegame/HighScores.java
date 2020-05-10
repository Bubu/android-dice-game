/*
 * Copyright (C) 2012 Resource Dice Game (http://code.google.com/p/android-dice-game)
 * 
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *   
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ridgelineapps.resdicegame;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HighScores extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (!tabletSize) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.high_scores);
        
        ((Button)findViewById(R.id.high_scores_ok)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        ((TextView)findViewById(R.id.textView1_1)).setText(formatScore(prefs.getString("hs_score_1", "0")));
        ((TextView)findViewById(R.id.textView1_2)).setText(formatDate(prefs.getString("hs_date_1", "0")));

        ((TextView)findViewById(R.id.textView2_1)).setText(formatScore(prefs.getString("hs_score_2", "0")));
        ((TextView)findViewById(R.id.textView2_2)).setText(formatDate(prefs.getString("hs_date_2", "0")));

        ((TextView)findViewById(R.id.textView3_1)).setText(formatScore(prefs.getString("hs_score_3", "0")));
        ((TextView)findViewById(R.id.textView3_2)).setText(formatDate(prefs.getString("hs_date_3", "0")));

        ((TextView)findViewById(R.id.textView4_1)).setText(formatScore(prefs.getString("hs_score_4", "0")));
        ((TextView)findViewById(R.id.textView4_2)).setText(formatDate(prefs.getString("hs_date_4", "0")));

        ((TextView)findViewById(R.id.textView5_1)).setText(formatScore(prefs.getString("hs_score_5", "0")));
        ((TextView)findViewById(R.id.textView5_2)).setText(formatDate(prefs.getString("hs_date_5", "0")));
    }
    
    public static String formatScore(String scoreString) {
        if(scoreString == null || scoreString.equals("") || scoreString.equals("0")) {
            return "-";
        }
        
        return scoreString;
    }
    
    public static String formatDate(String dateLong) {
        if(dateLong == null || dateLong.equals("") || dateLong.equals("0")) {
            return "-";
        }
        
        long date = 0;
        try {
            date = Long.parseLong(dateLong);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        if(date == 0) {
            return "-";
        }
        
        Date d = new Date(date);
        return DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(d);
    }
    
    public static void score(Activity activity, int score) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
     
        ArrayList<ScoreAndDate> scores = new ArrayList<ScoreAndDate>();

        boolean added = false;
        boolean newHigh = false;
        boolean newTop5 = false;
        
        for(int i=1; i <= 5; i++) {
            int hs = 0;
            try {
                hs = Integer.parseInt(prefs.getString("hs_score_" + i, "0"));
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            long date = 0;
            try {
                date = Long.parseLong(prefs.getString("hs_date_" + i, "0"));
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            
            if(!added && score >= hs) {
//                if(hs > 0) {
                    newTop5 = true;
                    if(scores.size() == 0) {
                        newHigh = true;
                    }
//                }
                added = true;
                ScoreAndDate sad = new ScoreAndDate();
                sad.score = score;
                sad.date = System.currentTimeMillis();
                scores.add(sad);
            }
            
            ScoreAndDate sad = new ScoreAndDate();
            sad.score = hs;
            sad.date = date;
            scores.add(sad);
        }
        
        if(added) {
            Editor editor = prefs.edit();
            for(int i=1; i <= 5; i++) {
                ScoreAndDate sad = scores.get(i - 1);
                editor.putString("hs_score_" + i, "" + sad.score);
                editor.putString("hs_date_" + i, "" + sad.date);
            }
            editor.commit();
        }
        

        if(newHigh) {
            Toast.makeText(activity, activity.getResources().getString(R.string.high_scores_toast), Toast.LENGTH_SHORT).show();
        }
        else if(newTop5) {
            Toast.makeText(activity, activity.getResources().getString(R.string.high_scores_top_5_toast), Toast.LENGTH_SHORT).show();            
        }
    }
    
    public static class ScoreAndDate
    {
        public int score;
        public long date;
    }
}
