package com.codingwithmitch.notes.util;

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemSwiped(int position);
}