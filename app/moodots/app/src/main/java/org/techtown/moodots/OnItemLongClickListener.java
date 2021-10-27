package org.techtown.moodots;

import android.view.View;

public interface OnItemLongClickListener {
    void onItemLongClick(DiaryAdapter.ViewHolder holder , View view, int position);
}
