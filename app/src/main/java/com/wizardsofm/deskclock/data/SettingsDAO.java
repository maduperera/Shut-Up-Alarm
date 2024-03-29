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
import android.content.SharedPreferences;
import android.net.Uri;

import com.wizardsofm.deskclock.Utils;
import com.wizardsofm.deskclock.data.DataModel.CitySort;
import com.wizardsofm.deskclock.data.DataModel.ClockStyle;
import com.wizardsofm.deskclock.settings.ScreensaverSettingsActivity;
import com.wizardsofm.deskclock.settings.SettingsActivity;

import java.util.Locale;
import java.util.TimeZone;

/**
 * This class encapsulates the storage of application preferences in {@link SharedPreferences}.
 */
final class SettingsDAO {

    /** Key to a preference that stores the preferred sort order of world cities. */
    private static final String KEY_SORT_PREFERENCE = "sort_preference";

    /** Key to a preference that stores the default ringtone for new alarms. */
    private static final String KEY_DEFAULT_ALARM_RINGTONE_URI = "default_alarm_ringtone_uri";

    private SettingsDAO() {}

    /**
     * @return an enumerated value indicating the order in which cities are ordered
     */
    static CitySort getCitySort(Context context) {
        final int defaultSortOrdinal = CitySort.NAME.ordinal();
        final SharedPreferences prefs = Utils.getDefaultSharedPreferences(context);
        final int citySortOrdinal = prefs.getInt(KEY_SORT_PREFERENCE, defaultSortOrdinal);
        return CitySort.values()[citySortOrdinal];
    }

    /**
     * Adjust the sort order of cities.
     */
    static void toggleCitySort(Context context) {
        final CitySort oldSort = getCitySort(context);
        final CitySort newSort = oldSort == CitySort.NAME ? CitySort.UTC_OFFSET : CitySort.NAME;
        final SharedPreferences prefs = Utils.getDefaultSharedPreferences(context);
        prefs.edit().putInt(KEY_SORT_PREFERENCE, newSort.ordinal()).apply();
    }

    /**
     * @return {@code true} if a clock for the user's home timezone should be automatically
     *      displayed when it doesn't match the current timezone
     */
    static boolean getAutoShowHomeClock(Context context) {
        final SharedPreferences prefs = Utils.getDefaultSharedPreferences(context);
        return prefs.getBoolean(SettingsActivity.KEY_AUTO_HOME_CLOCK, false);
    }

    /**
     * @return the user's home timezone
     */
    static TimeZone getHomeTimeZone(Context context) {
        final SharedPreferences prefs = Utils.getDefaultSharedPreferences(context);
        final String defaultTimeZoneId = TimeZone.getDefault().getID();
        final String timeZoneId = prefs.getString(SettingsActivity.KEY_HOME_TZ, defaultTimeZoneId);
        return TimeZone.getTimeZone(timeZoneId);
    }

    /**
     * Sets the user's home timezone to the current system timezone if no home timezone is yet set.
     *
     * @param homeTimeZone the timezone to set as the user's home timezone if necessary
     */
    static void setDefaultHomeTimeZone(Context context, TimeZone homeTimeZone) {
        final SharedPreferences prefs = Utils.getDefaultSharedPreferences(context);
        final String homeTimeZoneId = prefs.getString(SettingsActivity.KEY_HOME_TZ, null);
        if (homeTimeZoneId == null) {
            prefs.edit().putString(SettingsActivity.KEY_HOME_TZ, homeTimeZone.getID()).apply();
        }
    }

    /**
     * @return a value indicating whether analog or digital clocks are displayed in the app
     */
    static ClockStyle getClockStyle(Context context) {
        return getClockStyle(context, SettingsActivity.KEY_CLOCK_STYLE);
    }

    /**
     * @return a value indicating whether analog or digital clocks are displayed on the screensaver
     */
    static ClockStyle getScreensaverClockStyle(Context context) {
        return getClockStyle(context, ScreensaverSettingsActivity.KEY_CLOCK_STYLE);
    }

    /**
     * @return {@code true} if the screen saver should be dimmed for lower contrast at night
     */
    static boolean getScreensaverNightModeOn(Context context) {
        final SharedPreferences prefs = Utils.getDefaultSharedPreferences(context);
        return prefs.getBoolean(ScreensaverSettingsActivity.KEY_NIGHT_MODE, false);
    }

    /**
     * @return the uri of the selected ringtone or the {@code defaultUri} if no explicit selection
     *      has yet been made
     */
    static Uri getTimerRingtoneUri(Context context, Uri defaultUri) {
        final SharedPreferences prefs = Utils.getDefaultSharedPreferences(context);
        final String uriString = prefs.getString(SettingsActivity.KEY_TIMER_RINGTONE, null);
        return uriString == null ? defaultUri : Uri.parse(uriString);
    }

    /**
     * @return whether timer vibration is enabled. false by default.
     */
    static boolean getTimerVibrate(Context context) {
        final SharedPreferences prefs = Utils.getDefaultSharedPreferences(context);
        return prefs.getBoolean(SettingsActivity.KEY_TIMER_VIBRATE, false);
    }

    /**
     * @param enabled whether vibration will be turned on for all timers.
     */
    static void setTimerVibrate(Context context, boolean enabled) {
        final SharedPreferences prefs = Utils.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(SettingsActivity.KEY_TIMER_VIBRATE, enabled).apply();
    }


    /**
     * @param enabled whether snooze by voice will be turned on for all alarms.
     */
    static void setSnoozeByVoice(Context context, boolean enabled) {
        final SharedPreferences prefs = Utils.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(SettingsActivity.KEY_SNOOZE_BY_VOICE, enabled).apply();
    }

    /**
     * @return whether snooze by voice is enabled. true by default.
     */
    static boolean getSnoozeByVoice(Context context) {
        final SharedPreferences prefs = Utils.getDefaultSharedPreferences(context);
        return prefs.getBoolean(SettingsActivity.KEY_SNOOZE_BY_VOICE, true);
    }


    /**
     * @param uri the uri of the ringtone to play for all timers
     */
    static void setTimerRingtoneUri(Context context, Uri uri) {
        final SharedPreferences prefs = Utils.getDefaultSharedPreferences(context);
        prefs.edit().putString(SettingsActivity.KEY_TIMER_RINGTONE, uri.toString()).apply();
    }

    /**
     * @return the uri of the selected ringtone or the {@code defaultUri} if no explicit selection
     *      has yet been made
     */
    static Uri getDefaultAlarmRingtoneUri(Context context, Uri defaultUri) {
        final SharedPreferences prefs = Utils.getDefaultSharedPreferences(context);
        final String uriString = prefs.getString(KEY_DEFAULT_ALARM_RINGTONE_URI, null);
        return uriString == null ? defaultUri : Uri.parse(uriString);
    }
    /**
     * @param uri identifies the default ringtone to play for new alarms
     */
    static void setDefaultAlarmRingtoneUri(Context context, Uri uri) {
        final SharedPreferences prefs = Utils.getDefaultSharedPreferences(context);
        prefs.edit().putString(KEY_DEFAULT_ALARM_RINGTONE_URI, uri.toString()).apply();
    }

    private static ClockStyle getClockStyle(Context context, String prefKey) {
        final String defaultStyle = context.getString(com.wizardsofm.deskclock.R.string.default_clock_style);
        final SharedPreferences prefs = Utils.getDefaultSharedPreferences(context);
        final String clockStyle = prefs.getString(prefKey, defaultStyle);
        // Use hardcoded locale to perform toUpperCase, because in some languages toUpperCase adds
        // accent to character, which breaks the enum conversion.
        return ClockStyle.valueOf(clockStyle.toUpperCase(Locale.US));
    }
}