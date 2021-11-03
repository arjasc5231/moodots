package org.techtown.moodots;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.Chart;
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
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Date;


public class cweekfragment extends Fragment {
    PieChart pieChart;
    ScatterChart scatterChart;
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_week,container,false);
        ArrayList<Integer> percent= bringdata();
        scatterchart(rootView);
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
    public void scatterchart(ViewGroup rootView){
        scatterChart = rootView.findViewById(R.id.scatterchart);
        scatterChart.getDescription().setEnabled(false);
        scatterChart.setDrawGridBackground(false);
        scatterChart.setTouchEnabled(true);
        scatterChart.setMaxHighlightDistance(0f);
        scatterChart.setHighlightPerTapEnabled(false);
        // enable scaling and dragging
        scatterChart.setDragEnabled(true);
        scatterChart.setScaleYEnabled(true);
        scatterChart.setScaleXEnabled(false);
        scatterChart.setDoubleTapToZoomEnabled(false);
        scatterChart.setMaxVisibleValueCount(999999999);
        scatterChart.setPinchZoom(false);

        /*Legend l = scatterChart.getLegend();
        l.setEnabled(false);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        //l.setTypeface(tfLight);
        l.setXOffset(5f);*/

        YAxis yl = scatterChart.getAxisLeft();
        //yl.setTypeface(tfLight);
        yl.setDrawGridLines(false);
        yl.setAxisMinimum(0f);// this replaces setStartAtZero(true)

        scatterChart.getAxisRight().setEnabled(false);
        XAxis xl = scatterChart.getXAxis();
        //xl.setTypeface(tfLight);
        xl.setDrawGridLines(false);
        yl.setInverted(true);

        ArrayList<Entry> yValues = new ArrayList<>();
        yValues.add(new Entry(1,3));
        yValues.add(new Entry(1,30));
        yValues.add(new Entry(1,40));
        yValues.add(new Entry(1,300));
        yValues.add(new Entry(2,8));
        yValues.add(new Entry(3,9));
        yValues.add(new Entry(4,10));
        yValues.add(new Entry(5,4));
        yValues.add(new Entry(6,6));
        yValues.add(new Entry(7,15));
        ScatterDataSet dataSet = new ScatterDataSet(yValues,"Mood");
        dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        int[] colorset={0Xef534e,0Xffee58,0X9c27b0 };
        dataSet.setColor(ColorTemplate.COLORFUL_COLORS[0]);
        dataSet.setScatterShapeSize(20);

        ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet); // add the data sets
        //dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        ScatterData data = new ScatterData(dataSets);
        //data.setValueTextSize(10f);
        //data.setValueTextColor(Color.BLACK);
        scatterChart.animateY(3000);
        scatterChart.setData(data);

    }
}