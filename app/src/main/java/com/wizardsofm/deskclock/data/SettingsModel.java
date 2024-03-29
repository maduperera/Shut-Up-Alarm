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

package com.wizardsofm.deskclock.data;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;

import com.wizardsofm.deskclock.Utils;

import java.util.TimeZone;

/**
 * All settings data is accessed via this model.
 */
final class SettingsModel {

    private final Context mContext;

    /** The uri of the default ringtone to use for timers until the user explicitly chooses one. */
    private Uri mDefaultTimerRingtoneUri;

    SettingsModel(Context context) {
        mContext = context;

        // Set the user's default home timezone if one has not yet been chosen.
        SettingsDAO.setDefaultHomeTimeZone(mContext, TimeZone.getDefault());
    }

    DataModel.CitySort getCitySort() {
        return SettingsDAO.getCitySort(mContext);
    }

    void toggleCitySort() {
        SettingsDAO.toggleCitySort(mContext);
    }

    TimeZone getHomeTimeZone() {
        return SettingsDAO.getHomeTimeZone(mContext);
    }

    DataModel.ClockStyle getClockStyle() {
        return SettingsDAO.getClockStyle(mContext);
    }

    DataModel.ClockStyle getScreensaverClockStyle() {
        return SettingsDAO.getScreensaverClockStyle(mContext);
    }

    boolean getScreensaverNightModeOn() {
        return SettingsDAO.getScreensaverNightModeOn(mContext);
    }

    boolean getShowHomeClock() {
        if (!SettingsDAO.getAutoShowHomeClock(mContext)) {
            return false;
        }

        // Show the home clock if the current time and home time differ.
        // (By using UTC offset for this comparison the various DST rules are considered)
        final TimeZone homeTimeZone = SettingsDAO.getHomeTimeZone(mContext);
        final long now = System.currentTimeMillis();
        return homeTimeZone.getOffset(now) != TimeZone.getDefault().getOffset(now);
    }

    Uri getDefaultTimerRingtoneUri() {
        if (mDefaultTimerRingtoneUri == null) {
            mDefaultTimerRingtoneUri = Utils.getResourceUri(mContext, com.wizardsofm.deskclock.R.raw.timer_expire);
        }

        return mDefaultTimerRingtoneUri;
    }

    void setTimerRingtoneUri(Uri uri) {
        SettingsDAO.setTimerRingtoneUri(mContext, uri);
    }

    Uri getTimerRingtoneUri() {
        return SettingsDAO.getTimerRingtoneUri(mContext, getDefaultTimerRingtoneUri());
    }

    Uri getDefaultAlarmRingtoneUri() {
        return SettingsDAO.getDefaultAlarmRingtoneUri(mContext,
                Settings.System.DEFAULT_ALARM_ALERT_URI);
    }

    void setDefaultAlarmRingtoneUri(Uri uri) {
        SettingsDAO.setDefaultAlarmRingtoneUri(mContext, uri);
    }

    boolean getTimerVibrate() {
        return SettingsDAO.getTimerVibrate(mContext);
    }

    void setTimerVibrate(boolean enabled) {
        SettingsDAO.setTimerVibrate(mContext, enabled);
    }

    boolean getSnoozeByVoice() {
        return SettingsDAO.getSnoozeByVoice(mContext);
    }

    void setSnoozeByVoice(boolean enabled) {
        SettingsDAO.setSnoozeByVoice(mContext, enabled);
    }

}
