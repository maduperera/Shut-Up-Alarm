/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wizardsofm.deskclock.timer;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wizardsofm.deskclock.data.Timer;

/**
 * This view is a visual representation of a {@link Timer}.
 */
public class TimerItem extends LinearLayout {

    /** Displays the remaining time or time since expiration. */
    private CountingTimerView mTimerText;

    /** Displays timer progress as a color circle that changes from white to red. */
    private TimerCircleView mCircleView;

    /** A button that either resets the timer or adds time to it, depending on its state. */
    private ImageView mResetAddButton;

    /** Displays the label associated with the timer. Tapping it presents an edit dialog. */
    private TextView mLabelView;

    /** The last state of the timer that was rendered; used to avoid expensive operations. */
    private Timer.State mLastState;

    public TimerItem(Context context) {
        this(context, null);
    }

    public TimerItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLabelView = (TextView) findViewById(com.wizardsofm.deskclock.R.id.timer_label);
        mResetAddButton = (ImageView) findViewById(com.wizardsofm.deskclock.R.id.reset_add);
        mCircleView = (TimerCircleView) findViewById(com.wizardsofm.deskclock.R.id.timer_time);
        mTimerText = (CountingTimerView) findViewById(com.wizardsofm.deskclock.R.id.timer_time_text);
        mTimerText.setShowBoundingCircle(mCircleView != null);
    }

    /**
     * Updates this view to display the latest state of the {@code timer}.
     */
    void update(Timer timer) {
        // Update the time.
        mTimerText.setTime(timer.getRemainingTime(), false);

        // Update the label if it changed.
        final String label = timer.getLabel();
        if (!TextUtils.equals(label, mLabelView.getText())) {
            mLabelView.setText(label);
        }

        // Update visibility of things that may blink.
        final boolean blinkOff = SystemClock.elapsedRealtime() % 1000 < 500;
        if (mCircleView != null) {
            final boolean hideCircle = timer.isExpired() && blinkOff;
            mCircleView.setVisibility(hideCircle ? INVISIBLE : VISIBLE);

            if (!hideCircle) {
                // Update the progress of the circle.
                mCircleView.update(timer);
            }
        }
        mTimerText.showTime(!timer.isPaused() || !blinkOff);

        // Update some potentially expensive areas of the user interface only on state changes.
        if (timer.getState() != mLastState) {
            mLastState = timer.getState();
            switch (mLastState) {
                case RESET: {
                    final String resetDesc = getResources().getString(com.wizardsofm.deskclock.R.string.timer_reset);
                    mResetAddButton.setImageResource(com.wizardsofm.deskclock.R.drawable.ic_reset);
                    mResetAddButton.setContentDescription(resetDesc);
                    mTimerText.setTimeStrTextColor(false, true);
                    break;
                }
                case RUNNING: {
                    final String addTimeDesc = getResources().getString(com.wizardsofm.deskclock.R.string.timer_plus_one);
                    mResetAddButton.setImageResource(com.wizardsofm.deskclock.R.drawable.ic_plusone);
                    mResetAddButton.setContentDescription(addTimeDesc);
                    mTimerText.setTimeStrTextColor(false, true);
                    break;
                }
                case PAUSED: {
                    final String resetDesc = getResources().getString(com.wizardsofm.deskclock.R.string.timer_reset);
                    mResetAddButton.setImageResource(com.wizardsofm.deskclock.R.drawable.ic_reset);
                    mResetAddButton.setContentDescription(resetDesc);
                    mTimerText.setTimeStrTextColor(false, true);
                    break;
                }
                case EXPIRED: {
                    final String addTimeDesc = getResources().getString(com.wizardsofm.deskclock.R.string.timer_plus_one);
                    mResetAddButton.setImageResource(com.wizardsofm.deskclock.R.drawable.ic_plusone);
                    mResetAddButton.setContentDescription(addTimeDesc);
                    mTimerText.setTimeStrTextColor(true, true);
                    break;
                }
            }
        }
    }
}