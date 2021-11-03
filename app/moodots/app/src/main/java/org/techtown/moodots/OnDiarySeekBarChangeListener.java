package org.techtown.moodots;

import android.view.View;
import android.widget.SeekBar;

public interface OnDiarySeekBarChangeListener {
    public void onSeekBarChange(DiaryAdapter.ViewHolder holder , SeekBar seekBar, int position);
}
