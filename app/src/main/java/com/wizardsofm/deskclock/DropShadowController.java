/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.wizardsofm.deskclock;

import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.wizardsofm.deskclock.data.DataModel;
import com.wizardsofm.deskclock.uidata.TabScrollListener;
import com.wizardsofm.deskclock.uidata.UiDataModel;

import static com.wizardsofm.deskclock.AnimatorUtils.getAlphaAnimator;

/**
 * This controller encapsulates the logic that watches a model for changes to scroll state and
 * updates the display state of an associated drop shadow. The observable model may take many forms
 * including ListViews, RecyclerViews and this application's UiDataModel. Each of these models can
 * indicate when content is scrolled to its top. When the content is scrolled to the top the drop
 * shadow is hidden and the content appears flush with the app bar. When the content is scrolled
 * up the drop shadow is displayed making the content appear to scroll below the app bar.
 */
public final class DropShadowController {

    /** Updates {@link #mDropShadowView} in response to changes in the backing scroll model. */
    private final ScrollChangeWatcher mScrollChangeWatcher = new ScrollChangeWatcher();

    /** Fades the {@link @mDropShadowView} in/out as scroll state changes. */
    private final ValueAnimator mDropShadowAnimator;

    /** The component that displays a drop shadow. */
    private final View mDropShadowView;

    // Supported sources of scroll position include: ListView, RecyclerView and UiDataModel.
    private RecyclerView mRecyclerView;
    private UiDataModel mUiDataModel;
    private ListView mListView;

    /**
     * @param dropShadowView to be hidden/shown as {@code uiDataModel} reports scrolling changes
     * @param uiDataModel models the vertical scrolling state of the application's selected tab
     */
    public DropShadowController(View dropShadowView, UiDataModel uiDataModel) {
        this(dropShadowView);
        mUiDataModel = uiDataModel;
        mUiDataModel.addTabScrollListener(mScrollChangeWatcher);
        updateDropShadow(uiDataModel.isSelectedTabScrolledToTop());
    }

    /**
     * @param dropShadowView to be hidden/shown as {@code listView} reports scrolling changes
     * @param listView a scrollable view that dictates the visibility of {@code dropShadowView}
     */
    public DropShadowController(View dropShadowView, ListView listView) {
        this(dropShadowView);
        mListView = listView;
        mListView.setOnScrollListener(mScrollChangeWatcher);
        updateDropShadow(Utils.isScrolledToTop(listView));
    }

    /**
     * @param dropShadowView to be hidden/shown as {@code recyclerView} reports scrolling changes
     * @param recyclerView a scrollable view that dictates the visibility of {@code dropShadowView}
     */
    public DropShadowController(View dropShadowView, RecyclerView recyclerView) {
        this(dropShadowView);
        mRecyclerView = recyclerView;
        mRecyclerView.addOnScrollListener(mScrollChangeWatcher);
        updateDropShadow(Utils.isScrolledToTop(recyclerView));
    }

    private DropShadowController(View dropShadowView) {
        mDropShadowView = dropShadowView;
        mDropShadowAnimator = getAlphaAnimator(mDropShadowView, 0f, 1f)
                .setDuration(UiDataModel.getUiDataModel().getShortAnimationDuration());
    }

    /**
     * Stop updating the drop shadow in response to scrolling changes. Stop listening to the backing
     * scrollable entity for changes. This is important to avoid memory leaks.
     */
    public void stop() {
        if (mRecyclerView != null) {
            mRecyclerView.removeOnScrollListener(mScrollChangeWatcher);
        } else if (mListView != null) {
            mListView.setOnScrollListener(null);
        } else if (mUiDataModel != null) {
            mUiDataModel.removeTabScrollListener(mScrollChangeWatcher);
        }
    }

    /**
     * @param scrolledToTop {@code true} indicates the drop shadow should be hidden;
     *      {@code false} indicates the drop shadow should be displayed
     */
    private void updateDropShadow(boolean scrolledToTop) {
        if (scrolledToTop && mDropShadowView.getAlpha() != 0f) {
            if (DataModel.getDataModel().isApplicationInForeground()) {
                mDropShadowAnimator.reverse();
            } else {
                mDropShadowView.setAlpha(0f);
            }
        }

        if (!scrolledToTop && mDropShadowView.getAlpha() != 1f) {
            if (DataModel.getDataModel().isApplicationInForeground()) {
                mDropShadowAnimator.start();
            } else {
                mDropShadowView.setAlpha(1f);
            }
        }
    }

    /**
     * Update the drop shadow as the scrollable entity is scrolled.
     */
    private final class ScrollChangeWatcher extends RecyclerView.OnScrollListener
            implements TabScrollListener, AbsListView.OnScrollListener {

        // RecyclerView scrolled.
        @Override
        public void onScrolled(RecyclerView view, int dx, int dy) {
            updateDropShadow(Utils.isScrolledToTop(view));
        }

        // ListView scrolled.
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {}

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                int totalItemCount) {
            updateDropShadow(Utils.isScrolledToTop(view));
        }

        // UiDataModel reports scroll change.
        public void selectedTabScrollToTopChanged(UiDataModel.Tab selectedTab, boolean scrolledToTop) {
            updateDropShadow(scrolledToTop);
        }
    }
}