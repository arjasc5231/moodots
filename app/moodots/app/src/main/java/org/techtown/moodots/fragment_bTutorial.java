package org.techtown.moodots;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class fragment_bTutorial extends Fragment implements OnBackPressedListener {
    astart_activity_aMain activity;
    Context context;
    OnTabItemSelectedListener listener;
    int pagecount;
    int checktutorial;
    ImageButton tutorialimage;
    boolean checkservicestate=false;
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
        if(checktutorial==-3||checktutorial==-2){
            Log.d("debug", "debug 튜토리얼을 시작합니다.");
            int[] tutorialpage = new int[]{
                    R.drawable.tutorial_1, R.drawable.tutorial_2, R.drawable.tutorial_3, R.drawable.tutorial_4, R.drawable.tutorial_5,
                    R.drawable.tutorial_6, R.drawable.tutorial_7, R.drawable.tutorial_8, R.drawable.tutorial_9, R.drawable.tutorial_10,
                    R.drawable.tutorial_11, R.drawable.tutorial_12, R.drawable.tutorial_13, R.drawable.tutorial_14, R.drawable.tutorial_15,
                    R.drawable.tutorial_16, R.drawable.tutorial_17, R.drawable.tutorial_18, R.drawable.tutorial_19, R.drawable.tutorial_20,
                    R.drawable.tutorial_21, R.drawable.tutorial_22, R.drawable.tutorial_23,R.drawable.tutorial_24,R.drawable.tutorial_25,
                    R.drawable.tutorial_26,R.drawable.tutorial_27,R.drawable.tutorial_28,R.drawable.tutorial_29,R.drawable.tutorial_30,R.drawable.tutorial_31};
            pagecount = 0;
            tutorialimage = rootView.findViewById(R.id.tutorialimage);
            if(astart_activity_aMain.isServiceRunningCheck()){
                getActivity().stopService(new Intent(getActivity().getApplicationContext(), service_MyService.class));
                checkservicestate=true;
            }
            tutorialimage.setBackgroundResource(tutorialpage[pagecount]);
            tutorialimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pagecount += 1;
                    if(astart_activity_aMain.isServiceRunningCheck()){
                        getActivity().stopService(new Intent(getActivity().getApplicationContext(), service_MyService.class));
                        checkservicestate=true;
                    }
                    if (pagecount == 31) {
                        zPreferencemanage.setInt(astart_activity_aMain.maincontext, "tutorial", 1);
                        if((!astart_activity_aMain.isServiceRunningCheck())) {//&&checkservicestate
                            getActivity().startService(new Intent(getActivity().getApplicationContext(), service_MyService.class));
                        }
                        if(checktutorial==-3) {
                            activity.replaceFragment(20);
                        }
                        else if(checktutorial==-2){
                            activity.replaceFragment(25);
                        }
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

    @Override
    public void onBackPressed() {
        showdialog();
    }

    public void showdialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("종료");
        builder.setMessage("종료하시겠습니까?");
        builder.setPositiveButton("아니요",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.setNegativeButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.finishAffinity(activity);
                    }
                });
        builder.show();
    }
    @Override
    public void onResume() {
        super.onResume();
        activity.setOnBackPressedListener((OnBackPressedListener) this);
    }
}