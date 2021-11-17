package org.techtown.moodots;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.AxisBase;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class cmonthfragment extends Fragment {

    PieChart pieChart;
    ScatterChart scatterChart;
    Context context;
    aMain activity;
    Button btnYearMonthPicker;
    final Calendar c = Calendar.getInstance();
    int chooseyear=c.get(Calendar.YEAR);
    int choosemonth=c.get(Calendar.MONTH)+1;

    public class AxisValueFormat extends ValueFormatter implements IAxisValueFormatter{
        private String[] day;

        AxisValueFormat(String[] values){
            this.day=values;
        }
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            if(value>=1&&value<=31) {
                return day[((int) value)-1];
            }
            return "";
        }
    }

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_month,container,false);
        initUI(rootView);

        return rootView;
    }
    public void initUI(ViewGroup rootView){
        ArrayList<Diary> percent= bringdata(chooseyear, choosemonth);
        scatterchart(rootView, percent);
        piechart(rootView, percent);
        btnYearMonthPicker = rootView.findViewById(R.id.datepick);
        btnYearMonthPicker.setText(chooseyear+"-"+choosemonth);
        btnYearMonthPicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                YearMonthPickerDialog pd = new YearMonthPickerDialog();
                DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                        chooseyear=year;
                        choosemonth=monthOfYear;
                        btnYearMonthPicker.setText(chooseyear+"-"+choosemonth);
                        ArrayList<Diary> temp= bringdata(year, monthOfYear);
                        piechart(rootView, temp);
                        scatterchart(rootView, temp);
                        Log.d("YearMonthPickerTest", "year = " + year + ", month = " + monthOfYear + ", day = " + dayOfMonth);
                    }
                };
                pd.setListener(d);
                pd.show(getActivity().getSupportFragmentManager(), "YearMonthPickerTest");
                Toast temp =Toast.makeText(getContext(), "누르는거 확인", Toast.LENGTH_SHORT);
                temp.show();
            }
        });
    }
    public ArrayList<Diary> bringdata( int year, int month){
        ArrayList<Diary> percent=new ArrayList<Diary>();
        String[] cutdate={Integer.toString(year),Integer.toString(month)};
        String sql = "SELECT _id, MOOD, CONTENTS, HASHCONTENTS, CHECKMOD, DATE, TIME, VOICE FROM " +DiaryDatabase.TABLE_DIARY +" ORDER BY DATE DESC, TIME DESC;";
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
                String contents = outCursor.getString(2);
                String hashcontents = outCursor.getString(3);
                int checkmod= outCursor.getInt(4);
                String date = outCursor.getString(5);
                String[] datecut= date.split("-");
                String time = outCursor.getString(6);
                String voice = outCursor.getString(7);
                if(Integer.parseInt(datecut[0],10)==year&&Integer.parseInt(datecut[1],10)==month) {
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
                    if (time != null && time.length() > 2) {
                        try {
                            Date inTime = zAppConstants.dateFormat6.parse(time);
                            time = zAppConstants.dateFormat6.format(inTime);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        time = "";
                    }
                    percent.add(new Diary(_id, mood, contents, hashcontents, checkmod, date, time, voice));
                }
            }
            outCursor.close();
        }
        return percent;
    }
    public void piechart(ViewGroup rootView, ArrayList<Diary> percent){
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
        pieChart.setRotationEnabled(false);
        pieChart.animateY(2000);
        Legend l = pieChart.getLegend();
        l.setEnabled(false);
        int[] moodlist=new int[7];
        for(int i=0;i<percent.size();i++){
            int p=percent.get(i).getMood()-1;
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
            yValues.add(new PieEntry(moodlist[0], "화남"));
            colorset.add(Color.parseColor("#EF534E"));
        }
        if(moodlist[1]!=0) {
            yValues.add(new PieEntry(moodlist[1], "기쁨"));
            colorset.add(Color.parseColor("#FFEE58"));
        }
        if(moodlist[2]!=0) {
            yValues.add(new PieEntry(moodlist[2], "두려움"));
            colorset.add(Color.parseColor("#66BB6A"));
        }
        if(moodlist[3]!=0) {
            yValues.add(new PieEntry(moodlist[3], "슬픔"));
            colorset.add(Color.parseColor("#2196F3"));
        }
        if(moodlist[4]!=0) {
            yValues.add(new PieEntry(moodlist[4], "혐오"));
            colorset.add(Color.parseColor("#9C27B0"));
        }
        if(moodlist[5]!=0) {
            yValues.add(new PieEntry(moodlist[5], "놀람"));
            colorset.add(Color.parseColor("#FFA726"));
        }
        if(moodlist[6]!=0) {
            yValues.add(new PieEntry(moodlist[6], "중립"));
            colorset.add(Color.parseColor("#A1A3A1"));
        }
        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(0f);
        dataSet.setColors(colorset);
        pieChart.setEntryLabelColor(Color.BLACK);
        PieData data = new PieData((dataSet));
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.BLACK);
        /*double temp=0;
        if((moodlist[0]+moodlist[1]+moodlist[2]+moodlist[3]+moodlist[4]+moodlist[5]+moodlist[6])==0){
            pieChart.setCenterText("작성된 일기가 없습니다.");
        }
        else{
            temp= Math.round(((float) moodlist[0]/(moodlist[0]+moodlist[1]+moodlist[2]+moodlist[3]+moodlist[4]+moodlist[5]+moodlist[6]))*100*100)/100.0;
            pieChart.setCenterText(Html.fromHtml("화남"+"<br />"+(temp)+"%"));
        }*/
        pieChart.setData(data);
    }
    public void scatterchart(ViewGroup rootView, ArrayList<Diary> percent){
        scatterChart = rootView.findViewById(R.id.scatterchart);
        scatterChart.getDescription().setEnabled(false);
        scatterChart.setDrawGridBackground(false);
        scatterChart.setTouchEnabled(true);
        scatterChart.setMaxHighlightDistance(0f);
        scatterChart.setHighlightPerTapEnabled(false);
        // enable scaling and dragging
        scatterChart.setDragEnabled(true);
        scatterChart.setScaleYEnabled(true);
        scatterChart.setScaleXEnabled(true);
        scatterChart.setDoubleTapToZoomEnabled(false);
        scatterChart.setHorizontalScrollBarEnabled(true);
        scatterChart.setMaxVisibleValueCount(999999999);
        scatterChart.setPinchZoom(false);
        Legend l = scatterChart.getLegend();
        l.setEnabled(false);
        /*l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        //l.setTypeface(tfLight);
        l.setXOffset(5f);*/
        YAxis yl = scatterChart.getAxisLeft();
        //yl.setTypeface(tfLight);

        yl.setLabelCount(25,false);
        //yl.setValueFormatter(new AxisValueFormat(day));
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f);// this replaces setStartAtZero(true)
        yl.setAxisMaximum(24f);
        yl.setGranularityEnabled(true);
        yl.setGranularity(1f);
        yl.setInverted(true);

        YAxis yr = scatterChart.getAxisRight();
        yr.setDrawGridLines(false);
        yr.setDrawLabels(false);

        String[] values=new String[31];
        for(int t=1;t<=31;t++){
            values[t-1]=Integer.toString(t);
        }
        //scatterChart.getAxisRight().setEnabled(false);
        XAxis xl = scatterChart.getXAxis();
        xl.setLabelCount(12,false);
        xl.setValueFormatter(new AxisValueFormat(values));
        xl.setAxisMinimum(0f);
        xl.setAxisMaximum(32f);
        //xl.setTypeface(tfLight);
        xl.setDrawGridLines(true);
        xl.setGranularityEnabled(true);
        xl.setGranularity(1f);

        ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
        for(int i=0;i<percent.size();i++){
            int p=percent.get(i).getMood();
            String[] date=percent.get(i).getDate().split("-");
            String[] time=percent.get(i).getTime().split(":");
            zAppConstants.println("date"+percent.get(i).getDate());
            zAppConstants.println("time"+percent.get(i).getTime());
            int dateint=0;
            int timeint1=0;
            int timeint2=0;
            try {
                dateint=Integer.parseInt(date[2],10);
            }catch(Exception e){
                e.printStackTrace();
            }
            try {
                timeint1=Integer.parseInt(time[0],10);
            }catch(Exception e){
                e.printStackTrace();
            }
            try {
                timeint2=Integer.parseInt(time[1],10);
            }catch(Exception e){
                e.printStackTrace();
            }
            switch (p){
                case 1:
                    ArrayList<Entry> angry = new ArrayList<>();
                    angry.add(new Entry(dateint,(float)(timeint1+timeint2/60.0)));
                    ScatterDataSet dataSet1 = new ScatterDataSet(angry,"angry");
                    dataSet1.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
                    dataSet1.setColor(Color.parseColor("#EF534E"));
                    dataSet1.setScatterShapeSize(50);
                    dataSet1.setDrawValues(false);
                    dataSets.add(dataSet1);
                    break;
                case 2:
                    ArrayList<Entry> joy = new ArrayList<>();
                    joy.add(new Entry(dateint,(float)(timeint1+timeint2/60.0)));
                    ScatterDataSet dataSet2 = new ScatterDataSet(joy,"joy");
                    dataSet2.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
                    dataSet2.setColor(Color.parseColor("#FFEE58"));
                    dataSet2.setScatterShapeSize(50);
                    dataSet2.setDrawValues(false);
                    dataSets.add(dataSet2);
                    break;
                case 3:
                    ArrayList<Entry> fear = new ArrayList<>();
                    fear.add(new Entry(dateint,(float)(timeint1+timeint2/60.0)));
                    ScatterDataSet dataSet3 = new ScatterDataSet(fear,"fear");
                    dataSet3.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
                    dataSet3.setColor(Color.parseColor("#66BB6A"));
                    dataSet3.setScatterShapeSize(50);
                    dataSet3.setDrawValues(false);
                    dataSets.add(dataSet3);
                    break;
                case 4:
                    ArrayList<Entry> sad = new ArrayList<>();
                    sad.add(new Entry(dateint,(float)(timeint1+timeint2/60.0)));
                    ScatterDataSet dataSet4 = new ScatterDataSet(sad,"sad");
                    dataSet4.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
                    dataSet4.setColor(Color.parseColor("#2196F3"));
                    dataSet4.setScatterShapeSize(50);
                    dataSet4.setDrawValues(false);
                    dataSets.add(dataSet4);
                    break;
                case 5:
                    ArrayList<Entry> disgust = new ArrayList<>();
                    disgust.add(new Entry(dateint,(float)(timeint1+timeint2/60.0)));
                    ScatterDataSet dataSet5 = new ScatterDataSet(disgust,"disgust");
                    dataSet5.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
                    dataSet5.setColor(Color.parseColor("#9C27B0"));
                    dataSet5.setScatterShapeSize(50);
                    dataSet5.setDrawValues(false);
                    dataSets.add(dataSet5);
                    break;
                case 6:
                    ArrayList<Entry> surprise = new ArrayList<>();
                    surprise.add(new Entry(dateint,(float)(timeint1+timeint2/60.0)));
                    ScatterDataSet dataSet6 = new ScatterDataSet(surprise,"surprise");
                    dataSet6.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
                    dataSet6.setColor(Color.parseColor("#FFA726"));
                    dataSet6.setScatterShapeSize(50);
                    dataSet6.setDrawValues(false);
                    dataSets.add(dataSet6);
                    break;
                case 7:
                    ArrayList<Entry> neutral = new ArrayList<>();
                    neutral.add(new Entry(dateint,(float)(timeint1+timeint2/60.0)));
                    ScatterDataSet dataSet7 = new ScatterDataSet(neutral,"neutral");
                    dataSet7.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
                    dataSet7.setColor(Color.parseColor("#A1A3A1"));
                    dataSet7.setScatterShapeSize(50);
                    dataSet7.setDrawValues(false); //entry위쪽 부분에 보이는 entry값 제거 코드
                    dataSets.add(dataSet7);
                    break;
            }
        }
        ScatterData data = new ScatterData(dataSets);
        //data.setValueTextSize(10f);
        //data.setValueTextColor(Color.BLACK);
        scatterChart.animateY(1500); //y축으로 내려오는 애니메이션
        scatterChart.setData(data);
        scatterChart.invalidate();
    }

}
