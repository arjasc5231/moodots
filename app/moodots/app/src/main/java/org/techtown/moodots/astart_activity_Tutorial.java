package org.techtown.moodots;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class astart_activity_Tutorial extends AppCompatActivity {
    public static astart_activity_Tutorial tuto = null;
    int pagecount;
    int checktutorial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checktutorial = zPreferencemanage.getInt(astart_activity_aMain.maincontext, "tutorial");
        tuto = this;
        if(checktutorial==-3){
            Log.d("debug", "debug 튜토리얼을 시작합니다.");
            zPreferencemanage.setInt(astart_activity_aMain.maincontext, "tutorial", 1);
            int[] tutorialpage = new int[]{
                    R.drawable.tutorial_1, R.drawable.tutorial_2, R.drawable.tutorial_3, R.drawable.tutorial_4, R.drawable.tutorial_5,
                    R.drawable.tutorial_6, R.drawable.tutorial_7, R.drawable.tutorial_8, R.drawable.tutorial_9, R.drawable.tutorial_10,
                    R.drawable.tutorial_11, R.drawable.tutorial_12, R.drawable.tutorial_13, R.drawable.tutorial_14, R.drawable.tutorial_15,
                    R.drawable.tutorial_16, R.drawable.tutorial_17, R.drawable.tutorial_18, R.drawable.tutorial_19, R.drawable.tutorial_20,
                    R.drawable.tutorial_21, R.drawable.tutorial_22, R.drawable.tutorial_23};
            setContentView(R.layout.tutorial);
            pagecount = 0;
            ImageButton tutorial = findViewById(R.id.tutorialimage);
            tutorial.setBackgroundResource(tutorialpage[pagecount]);
            tutorial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pagecount += 1;
                    if (pagecount == 23) {
                        Intent back = new Intent(astart_activity_Tutorial.this, astart_activity_aMain.class);
                        startActivity(back);
                        finish();
                    }
                    else {
                        tutorial.setImageResource(tutorialpage[pagecount]);
                    }
                }
            });
        }
        else {
            Intent back = new Intent(astart_activity_Tutorial.this, astart_activity_aMain.class);
            startActivity(back);
            finish();
        }
    }

}
