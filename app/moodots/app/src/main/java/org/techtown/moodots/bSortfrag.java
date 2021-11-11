package org.techtown.moodots;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Date;

public class bSortfrag extends Fragment implements OnBackPressedListener{
    aMain activity;
    Context context;
    TextView textsort;
    OnTabItemSelectedListener listener;
    cmoodfragment moodfragment=new cmoodfragment();
    cweekfragment weekfragment=new cweekfragment();
    cmonthfragment monthfragment=new cmonthfragment();
    cyearfragment yearfragment=new cyearfragment();
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        activity= (aMain) getActivity();
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
        TextView datesort=(TextView) rootView.findViewById(R.id.datesort);
        datesort.setText(getDate());
        getChildFragmentManager().beginTransaction().add(R.id.containersort, moodfragment).commit();
        Button day= (Button) rootView.findViewById(R.id.day);
        day.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                textsort.setText("Mood");
                getChildFragmentManager().beginTransaction().replace(R.id.containersort, moodfragment).commit();
            }
        });
        Button week= (Button) rootView.findViewById(R.id.week);
        week.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                textsort.setText("Week");
                getChildFragmentManager().beginTransaction().replace(R.id.containersort, weekfragment).commit();
            }
        });
        Button month= (Button) rootView.findViewById(R.id.month);
        month.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                textsort.setText("Month");
                getChildFragmentManager().beginTransaction().replace(R.id.containersort, monthfragment).commit();
            }
        });
        Button year= (Button) rootView.findViewById(R.id.year);
        year.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                textsort.setText("Year");
                getChildFragmentManager().beginTransaction().replace(R.id.containersort, yearfragment).commit();
            }
        });
        return rootView;
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