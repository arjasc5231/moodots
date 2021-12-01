package org.techtown.foregroundsample;

import android.widget.SeekBar;

public interface OnDiarySeekBarChangeListener {
    public void onSeekBarChange(org.techtown.foregroundsample.DiaryAdapter.ViewHolder holder , SeekBar seekBar, int position);
}
