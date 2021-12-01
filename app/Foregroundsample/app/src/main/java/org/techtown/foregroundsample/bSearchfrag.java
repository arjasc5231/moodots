package org.techtown.foregroundsample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;


public class bSearchfrag extends Fragment implements OnBackPressedListener {
    aMain activity;
    Context context;
    TextView textsearch;
    int current =1;
    OnTabItemSelectedListener listener;


     cmoodfragment moodfragment=new  cmoodfragment();
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