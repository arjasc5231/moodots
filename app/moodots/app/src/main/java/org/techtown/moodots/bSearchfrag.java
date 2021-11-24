package org.techtown.moodots;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import static android.os.SystemClock.sleep;


public class bSearchfrag extends Fragment implements OnBackPressedListener{
    aMain activity;
    Context context;
    TextView textsearch;
    int current =1;
    OnTabItemSelectedListener listener;


    cmoodfragment moodfragment=new cmoodfragment();
    ckeywordfragment keywordfragment= new ckeywordfragment();

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search,container,false);
        textsearch=(TextView) rootView.findViewById(R.id.searchtext);
        //TextView datesearch=(TextView) rootView.findViewById(R.id.datesearch);
        //datesearch.setText(bSortfrag.getDate());
        getChildFragmentManager().beginTransaction().add(R.id.containersort, moodfragment).commit();

        buttonUI(rootView);

        TextView searchtext= rootView.findViewById(R.id.searchtext);

        Button left= (Button) rootView.findViewById(R.id.leftbtn);
        left.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(current==1){
                    searchtext.setText("Mood");
                    current=2;
                    getChildFragmentManager().beginTransaction().replace(R.id.containersort, moodfragment).commit();
                }
                else if(current ==2){
                    searchtext.setText("KeyWord");
                    current=1;
                    getChildFragmentManager().beginTransaction().replace(R.id.containersort, keywordfragment).commit();
                }
            }
        });
        Button right= (Button) rootView.findViewById(R.id.rightbtn);
        right.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(current==1){
                    searchtext.setText("Mood");
                    current=2;
                    getChildFragmentManager().beginTransaction().replace(R.id.containersort, moodfragment).commit();
                }
                else if(current ==2){
                    searchtext.setText("KeyWord");
                    current=1;
                    getChildFragmentManager().beginTransaction().replace(R.id.containersort, keywordfragment).commit();
                }
            }
        });
        return rootView;
    }
    public class pieValueFormatter extends ValueFormatter implements IAxisValueFormatter {

        public pieValueFormatter() {

        }

        @Override
        public String getPieLabel(float value, PieEntry pieEntry) {
            return String.valueOf((int) value);
        }
    }
    private void buttonUI(ViewGroup rootView){
        Button sort=rootView.findViewById(R.id.sort);
        sort.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                //stopAudio(0);
                activity.replaceFragment(2);
            }
        });
        Button main=rootView.findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                //stopAudio(0);
                activity.replaceFragment(1);
            }
        });
        Button setting=rootView.findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                //stopAudio(0);
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
                //stopAudio(0);
                Bundle result = new Bundle();
                result.putInt("bundleKey", 1);
                result.putInt("bundleKey9", 4);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                bBlankFragment blankfragment = new bBlankFragment();//프래그먼트2 선언
                blankfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                transaction.replace(R.id.container, blankfragment);
                transaction.commit();
            }
        });
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
                        activity.finish();
                    }
                });
        builder.show();
    }
    @Override
    public void onResume() {
        super.onResume();
        activity.setOnBackPressedListener(this);
    }


}