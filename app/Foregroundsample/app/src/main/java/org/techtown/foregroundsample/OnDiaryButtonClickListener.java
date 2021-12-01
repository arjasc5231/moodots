package org.techtown.foregroundsample;

import android.view.View;
import android.widget.SeekBar;

public interface OnDiaryButtonClickListener {
    public void onButtonClick(org.techtown.foregroundsample.DiaryAdapter.ViewHolder holder , SeekBar seekBar, View view, int position);
}
