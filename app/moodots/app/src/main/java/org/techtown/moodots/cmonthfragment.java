package org.techtown.moodots;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Date;

public class cmonthfragment extends Fragment {

    PieChart pieChart;
    Context context;
    aMain activity;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity= (aMain) getActivity();
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
        if(context != null){
            context= null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_day,container,false);
        ArrayList<Integer> percent= bringdata();
        piechart(rootView, percent);
        return rootView;
    }

    public ArrayList<Integer> bringdata(){
        ArrayList<Integer> percent=new ArrayList<Integer>();
        String curdate=bSortfrag.getDate();
        String[] cutdate=curdate.split("-");
        String sql = "SELECT _id, MOOD, CONTENTS, HASHCONTENTS, CHECKMOD, DATE, TIME FROM " +DiaryDatabase.TABLE_DIARY +" ORDER BY DATE DESC;";
        int recordCount= -1;
        DiaryDatabase database = DiaryDatabase.getInstance(context);
        if (database != null) {
            Cursor outCursor = database.rawQuery(sql);
            recordCount = outCursor.getCount();
            zAppConstants.println("record count : " + recordCount + "\n");
            for (int i = 0; i < recordCount; i++) {
                outCursor.moveToNext();
                int _id = outCursor.getInt(0);
                int mood = outCursor.getInt(1);
                String date = outCursor.getString(5);
                String[] datecut= date.split("-");
                if(datecut[1].equals(cutdate[1])&&datecut[0].equals(cutdate[0])) {
                    if (date != null && date.length() > 6) {
                        try {
                            Date inDate = zAppConstants.dateFormat5.parse(date);
                            date = zAppConstants.dateFormat5.format(inDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        date = "";
                    }
                    percent.add(mood);
                }
            }
            outCursor.close();
        }
        return percent;
    }
    public void piechart(ViewGroup rootView, ArrayList<Integer> percent){
        TextView angry= rootView.findViewById(R.id.angry);
        TextView joy= rootView.findViewById(R.id.joy);
        TextView fear= rootView.findViewById(R.id.fear);
        TextView sad= rootView.findViewById(R.id.sad);
        TextView disgust= rootView.findViewById(R.id.disgust);
        TextView surprise= rootView.findViewById(R.id.surprise);
        TextView neutral= rootView.findViewById(R.id.neutral);
        pieChart=rootView.findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0f);
        pieChart.setDrawHoleEnabled(true); //차트 가운데 구멍을 넣을것인지
        pieChart.setHoleColor(Color.WHITE); //그 가운데 구멍의 색 결정
        pieChart.setTransparentCircleRadius(0f);
        Legend l = pieChart.getLegend();
        l.setEnabled(false);
        int[] moodlist=new int[7];
        for(int i=0;i<percent.size();i++){
            int p=percent.get(i)-1;
            moodlist[p]+=1;
        }
        angry.setText("화남:"+moodlist[0]+"회");
        joy.setText("기쁨:"+moodlist[1]+"회");
        fear.setText("두려움:"+moodlist[2]+"회");
        sad.setText("슬픔:"+moodlist[3]+"회");
        disgust.setText("혐오:"+moodlist[4]+"회");
        surprise.setText("놀람:"+moodlist[5]+"회");
        neutral.setText("중립:"+moodlist[6]+"회");

        ArrayList<Integer> colorset=new ArrayList<Integer>();
        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
        if(moodlist[0]!=0) {
            yValues.add(new PieEntry(moodlist[0], "Angry"));
            colorset.add(Color.parseColor("#EF534E"));
        }
        if(moodlist[1]!=0) {
            yValues.add(new PieEntry(moodlist[1], "Joy"));
            colorset.add(Color.parseColor("#FFEE58"));
        }
        if(moodlist[2]!=0) {
            yValues.add(new PieEntry(moodlist[2], "Fear"));
            colorset.add(Color.parseColor("#66BB6A"));
        }
        if(moodlist[3]!=0) {
            yValues.add(new PieEntry(moodlist[3], "Sad"));
            colorset.add(Color.parseColor("#2196F3"));
        }
        if(moodlist[4]!=0) {
            yValues.add(new PieEntry(moodlist[4], "Disgust"));
            colorset.add(Color.parseColor("#9C27B0"));
        }
        if(moodlist[5]!=0) {
            yValues.add(new PieEntry(moodlist[5], "Surprise"));
            colorset.add(Color.parseColor("#FFA726"));
        }
        if(moodlist[6]!=0) {
            yValues.add(new PieEntry(moodlist[6], "Neutral"));
            colorset.add(Color.parseColor("#A1A3A1"));
        }
        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(0f);
        dataSet.setColors(colorset);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
    }
}