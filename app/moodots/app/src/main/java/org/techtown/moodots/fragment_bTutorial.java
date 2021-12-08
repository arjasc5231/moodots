package org.techtown.moodots;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class fragment_bTutorial extends Fragment {
    astart_activity_aMain activity;
    Context context;
    OnTabItemSelectedListener listener;
    int pagecount;
    int checktutorial;
    ImageButton tutorialimage;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        activity= (astart_activity_aMain) getActivity();
        if(context instanceof OnTabItemSelectedListener){
            listener = (OnTabItemSelectedListener) context;
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
        if(context != null){
            context = null;
            listener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_btutorial,container,false);
        checktutorial = zPreferencemanage.getInt(astart_activity_aMain.maincontext, "tutorial");
        if(checktutorial==-3){
            Log.d("debug", "debug 튜토리얼을 시작합니다.");
            int[] tutorialpage = new int[]{
                    R.drawable.tutorial_1, R.drawable.tutorial_2, R.drawable.tutorial_3, R.drawable.tutorial_4, R.drawable.tutorial_5,
                    R.drawable.tutorial_6, R.drawable.tutorial_7, R.drawable.tutorial_8, R.drawable.tutorial_9, R.drawable.tutorial_10,
                    R.drawable.tutorial_11, R.drawable.tutorial_12, R.drawable.tutorial_13, R.drawable.tutorial_14, R.drawable.tutorial_15,
                    R.drawable.tutorial_16, R.drawable.tutorial_17, R.drawable.tutorial_18, R.drawable.tutorial_19, R.drawable.tutorial_20,
                    R.drawable.tutorial_21, R.drawable.tutorial_22, R.drawable.tutorial_23};
            pagecount = 0;
            tutorialimage = rootView.findViewById(R.id.tutorialimage);
            tutorialimage.setBackgroundResource(tutorialpage[pagecount]);
            tutorialimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pagecount += 1;
                    if (pagecount == 23) {
                        zPreferencemanage.setInt(astart_activity_aMain.maincontext, "tutorial", 1);
                        getActivity().startService(new Intent(getActivity().getApplicationContext(), service_MyService.class));
                        activity.replaceFragment(20);
                    }
                    else {
                        tutorialimage.setImageResource(tutorialpage[pagecount]);
                    }
                }
            });
        }
        else {
            activity.replaceFragment(20); //필요 없긴 하지만 그래도 혹시나 해서 이중 체크
        }
        return rootView;
    }
}