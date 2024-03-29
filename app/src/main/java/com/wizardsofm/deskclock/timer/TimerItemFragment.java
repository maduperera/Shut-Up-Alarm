/*
 * Copyright (C) 2014 The Android Open Source Project
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

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wizardsofm.deskclock.LabelDialogFragment;
import com.wizardsofm.deskclock.data.DataModel;
import com.wizardsofm.deskclock.data.Timer;
import com.wizardsofm.deskclock.events.Events;

public class TimerItemFragment extends Fragment {

    private static final String KEY_TIMER_ID = "KEY_TIMER_ID";
    private int mTimerId;

    /** The public no-arg constructor required by all fragments. */
    public TimerItemFragment() {}

    public static TimerItemFragment newInstance(Timer timer) {
        final TimerItemFragment fragment = new TimerItemFragment();
        final Bundle args = new Bundle();
        args.putInt(KEY_TIMER_ID, timer.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTimerId = getArguments().getInt(KEY_TIMER_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final Timer timer = getTimer();
        if (timer == null) {
            return null;
        }

        final TimerItem view = (TimerItem) inflater.inflate(com.wizardsofm.deskclock.R.layout.timer_item, container, false);
        view.findViewById(com.wizardsofm.deskclock.R.id.reset_add).setOnClickListener(new ResetAddListener());
        view.findViewById(com.wizardsofm.deskclock.R.id.timer_label).setOnClickListener(new EditLabelListener());
        view.update(timer);

        return view;
    }

    /**
     * @return {@code true} iff the timer is in a state that requires continuous updates
     */
    boolean updateTime() {
        final TimerItem view = (TimerItem) getView();
        if (view != null) {
            final Timer timer = getTimer();
            view.update(timer);
            return !timer.isReset();
        }

        return false;
    }

    int getTimerId() {
        return mTimerId;
    }

    Timer getTimer() {
        return DataModel.getDataModel().getTimer(getTimerId());
    }

    private class ResetAddListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Timer timer = getTimer();
            if (timer.isPaused()) {
                DataModel.getDataModel().resetOrDeleteTimer(timer, com.wizardsofm.deskclock.R.string.label_deskclock);
            } else if (timer.isRunning() || timer.isExpired()) {
                DataModel.getDataModel().addTimerMinute(timer);
                Events.sendTimerEvent(com.wizardsofm.deskclock.R.string.action_add_minute, com.wizardsofm.deskclock.R.string.label_deskclock);
            }
        }
    }

    private class EditLabelListener implements View.OnClickListener {

        private static final String TAG = "label_dialog";

        @Override
        public void onClick(View v) {
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            final Fragment existingFragment = getFragmentManager().findFragmentByTag(TAG);
            if (existingFragment != null) {
                ft.remove(existingFragment);
            }
            ft.addToBackStack(null);
            LabelDialogFragment.newInstance(getTimer()).show(ft, TAG);
        }
    }
}