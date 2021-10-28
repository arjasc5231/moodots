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
        int[] percent= bringdata();
        scatterchart(rootView);
        piechart(rootView, percent);
        return rootView;
    }

    public int[] bringdata(){
        int[] percent = new int[0];
        String curdate=bSortfrag.getDate();
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
                if(date.equals(curdate)) {
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
                }
            }

            outCursor.close();
        }
        return percent;
    }
    public void piechart(ViewGroup rootView, int[]percent){
        pieChart=(PieChart) rootView.findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0f);
        pieChart.setDrawHoleEnabled(true); //차트 가운데 구멍을 넣을것인지
        pieChart.setHoleColor(Color.WHITE); //그 가운데 구멍의 색 결정
        pieChart.setTransparentCircleRadius(0f);
        Legend l = pieChart.getLegend();
        l.setEnabled(false);
        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
        yValues.add(new PieEntry(34f,"Angry"));
        yValues.add(new PieEntry(23f,"Joy"));
        yValues.add(new PieEntry(14f,"Fear"));
        yValues.add(new PieEntry(35f,"Sad"));
        yValues.add(new PieEntry(40f,"Disgust"));
        yValues.add(new PieEntry(40f,"Surprise"));
        yValues.add(new PieEntry(40f,"Neutral"));
        PieDataSet dataSet = new PieDataSet(yValues,"Mood");
        int[] colorset={0Xef534e,0Xffee58,0X9c27b0 };
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColor(255);
        //dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

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