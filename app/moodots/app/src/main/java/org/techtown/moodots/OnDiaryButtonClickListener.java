package org.techtown.moodots;

import android.view.View;
import android.widget.SeekBar;

public interface OnDiaryButtonClickListener {
    public void onButtonClick(nadapter_DiaryAdapter.ViewHolder holder , SeekBar seekBar, View view, int position);
}
