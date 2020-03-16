package com.slastanna.questory;

import androidx.recyclerview.widget.RecyclerView;

public class IdleRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private final IdleHandler mIdleHandler;
    private final RecyclerView mRecyclerView;

    private final Runnable mIdleRunnable = new Runnable() {

        int mRepeatCount = 0;

        @Override
        public void run() {
            if (mRecyclerView != null
                    && mIdleHandler != null
                    && mRecyclerView.getLayoutManager() != null
                    && mRecyclerView.getChildCount() > 0) {
                mRepeatCount = 0;
                mIdleHandler.onIdle(
                        mRecyclerView.getLayoutManager().getPosition(mRecyclerView.getChildAt(0)),
                        mRecyclerView.getChildCount());
            } else if (mRecyclerView != null && ++mRepeatCount < 5) {
                mRecyclerView.postDelayed(mIdleRunnable, 200);
            }
        }
    };

    private int mLastState = -1;

    public IdleRecyclerViewScrollListener(IdleHandler idleHandler, RecyclerView recyclerView) {
        mIdleHandler = idleHandler;
        mRecyclerView = recyclerView;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dx == 0 && dy == 0) {
            onScrollStateChanged(recyclerView, RecyclerView.SCROLL_STATE_IDLE);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState != mLastState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                recyclerView.postDelayed(mIdleRunnable, 200);
            } else {
                recyclerView.removeCallbacks(mIdleRunnable);
            }
        }
        mLastState = newState;
    }

    public interface IdleHandler {

        void onIdle(int firstVisibleItem, int visibleItemsCount);

    }
}
