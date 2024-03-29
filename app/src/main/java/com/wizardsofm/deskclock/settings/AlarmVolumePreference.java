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

package com.wizardsofm.deskclock.settings;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.wizardsofm.deskclock.RingtonePreviewKlaxon;
import com.wizardsofm.deskclock.data.DataModel;

public class AlarmVolumePreference extends Preference {

    private static final long ALARM_PREVIEW_DURATION_MS = 2000;

    private SeekBar mSeekbar;
    private ImageView mAlarmIcon;
    private boolean mPreviewPlaying;

    public AlarmVolumePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        // Disable click feedback for this preference.
        holder.itemView.setClickable(false);

        final Context context = getContext();
        final AudioManager audioManager =
                (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mSeekbar = (SeekBar) holder.findViewById(com.wizardsofm.deskclock.R.id.alarm_volume_slider);
        mSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));
        mSeekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
        mAlarmIcon = (ImageView) holder.findViewById(com.wizardsofm.deskclock.R.id.alarm_icon);
        updateIcon();

        final ContentObserver volumeObserver = new ContentObserver(mSeekbar.getHandler()) {
            @Override
            public void onChange(boolean selfChange) {
                // Volume was changed elsewhere, update our slider.
                mSeekbar.setProgress(audioManager.getStreamVolume(
                        AudioManager.STREAM_ALARM));
            }
        };

        mSeekbar.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                context.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI,
                        true, volumeObserver);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                context.getContentResolver().unregisterContentObserver(volumeObserver);
            }
        });


        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0);
                }
                updateIcon();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!mPreviewPlaying && seekBar.getProgress() != 0) {
                    // If we are not currently playing and progress is set to non-zero, start.
                    RingtonePreviewKlaxon.start(
                            context, DataModel.getDataModel().getDefaultAlarmRingtoneUri());
                    mPreviewPlaying = true;
                    seekBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RingtonePreviewKlaxon.stop(context);
                            mPreviewPlaying = false;
                        }
                    }, ALARM_PREVIEW_DURATION_MS);
                }
            }
        });
    }

    private void updateIcon() {
        mAlarmIcon.setImageResource(mSeekbar.getProgress() == 0 ?
                com.wizardsofm.deskclock.R.drawable.ic_alarm_off_24dp : com.wizardsofm.deskclock.R.drawable.ic_alarm_small);
    }
}