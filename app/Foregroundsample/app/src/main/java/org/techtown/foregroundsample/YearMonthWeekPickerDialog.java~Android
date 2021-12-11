package org.techtown.foregroundsample;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class YearMonthWeekPickerDialog extends DialogFragment {
    private static final int MAX_YEAR = 2099;
    private static final int MIN_YEAR = 1980;

    private DatePickerDialog.OnDateSetListener listener;
    public Calendar cal = Calendar.getInstance();
    public Calendar temp = Calendar.getInstance();

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    Button btnConfirm;
    Button btnCancel;
    public void weekset(NumberPicker yearPicker, NumberPicker monthPicker, NumberPicker weekPicker){
        temp.set(yearPicker.getValue(), monthPicker.getValue()-1,1);
        int lastday=temp.getActualMaximum(Calendar.DAY_OF_MONTH);
        int first=temp.get(Calendar.DAY_OF_WEEK);
        int numweek=0;
        temp.set(yearPicker.getValue(), monthPicker.getValue()-1,lastday);
        int last = temp.get(Calendar.DAY_OF_WEEK);
        if(first>=6){
            numweek--;
        }
        if(last<5){
            numweek--;
            numweek+=temp.get(Calendar.WEEK_OF_MONTH);
        }
        else if(last>=5){
            numweek+=temp.get(Calendar.WEEK_OF_MONTH);
        }
        Log.d("debug value change listener","numweek"+numweek);
        weekPicker.setMinValue(1);
        weekPicker.setMaxValue(numweek);
        weekPicker.setValue(1);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.yearmonthweekpicker, null);

        btnConfirm = dialog.findViewById(R.id.btn_confirm);
        btnCancel = dialog.findViewById(R.id.btn_cancel);

        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);
        yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        final NumberPicker weekPicker = (NumberPicker) dialog.findViewById(R.id.picker_week);
        weekPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        int month=cal.get(Calendar.MONTH) + 1;
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(month);

        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(year);

        weekset(yearPicker, monthPicker, weekPicker);
        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                weekset(yearPicker, monthPicker, weekPicker);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                YearMonthWeekPickerDialog.this.getDialog().cancel();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), weekPicker.getValue());
                YearMonthWeekPickerDialog.this.getDialog().cancel();
            }
        });



        builder.setView(dialog)
        // Add action buttons
        /*
        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MyYearMonthPickerDialog.this.getDialog().cancel();
            }
        })
        */
        ;

        return builder.create();
    }
}
