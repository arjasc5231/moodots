package org.techtown.moodots;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class fragment_bSortfrag extends Fragment implements OnBackPressedListener{
    astart_activity_aMain activity;
    Context context;
    TextView textsort;
    OnTabItemSelectedListener listener;
    int current=1; //week=1, month =2, year=3
    fragment_sub_cweekfragment weekfragment=new fragment_sub_cweekfragment();
    fragment_sub_cmonthfragment monthfragment=new fragment_sub_cmonthfragment();
    fragment_sub_cyearfragment yearfragment=new fragment_sub_cyearfragment();
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_sort,container,false);
        textsort=(TextView) rootView.findViewById(R.id.sorttext);
        //TextView datesort=(TextView) rootView.findViewById(R.id.datesort);
        //datesort.setText(getDate());
        getChildFragmentManager().beginTransaction().add(R.id.containersort, weekfragment).commit();
        Button left= (Button) rootView.findViewById(R.id.leftbtn);
        left.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(current==1){
                    textsort.setText("Year");
                    current=3;
                    getChildFragmentManager().beginTransaction().replace(R.id.containersort, yearfragment).commit();
                }
                else if(current ==2){
                    textsort.setText("Week");
                    current=1;
                    getChildFragmentManager().beginTransaction().replace(R.id.containersort, weekfragment).commit();
                }
                else{
                    textsort.setText("Month");
                    current=2;
                    getChildFragmentManager().beginTransaction().replace(R.id.containersort, monthfragment).commit();
                }
            }
        });
        Button right= (Button) rootView.findViewById(R.id.rightbtn);
        right.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(current==1){
                    textsort.setText("Month");
                    current=2;
                    getChildFragmentManager().beginTransaction().replace(R.id.containersort, monthfragment).commit();
                }
                else if(current ==2){
                    textsort.setText("Year");
                    current=3;
                    getChildFragmentManager().beginTransaction().replace(R.id.containersort, yearfragment).commit();
                }
                else{
                    textsort.setText("Week");
                    current=1;
                    getChildFragmentManager().beginTransaction().replace(R.id.containersort, weekfragment).commit();
                }
            }
        });

        buttonUI(rootView);
        return rootView;
    }
    private void buttonUI(ViewGroup rootView){
        Button main=rootView.findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                activity.replaceFragment(1);
            }
        });
        Button search=rootView.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                activity.replaceFragment(3);
            }
        });
        Button setting=rootView.findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                activity.replaceFragment(4);
            }
        });
        Button newDiaryButton = rootView.findViewById(R.id.newDiaryButton);
        newDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                Bundle result = new Bundle();
                result.putInt("bundleKey", 1);
                result.putInt("bundleKey9", 2);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragment_BlankFragment blankfragment = new fragment_BlankFragment();//프래그먼트2 선언
                blankfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                transaction.replace(R.id.container, blankfragment);
                transaction.commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        activity.replaceFragment(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setOnBackPressedListener(this);
    }
    static String getDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        String getDate = zAppConstants.dateFormat5.format(date);
        return getDate;
    }
}