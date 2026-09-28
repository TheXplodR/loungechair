/*
 * Copyright (C) 2026 The Loungechair Authors
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
package com.android.launcher3.loungechair;

import static com.android.launcher3.LauncherState.ALL_APPS;
import static com.android.launcher3.LauncherState.NORMAL;

import android.view.MotionEvent;

import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.allapps.ActivityAllAppsContainerView;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.touch.AbstractStateChangeTouchController;
import com.android.launcher3.touch.AllAppsSwipeController;
import com.android.launcher3.touch.SingleAxisSwipeDetector;

/**
 * Opens and closes the app list with a sideways swipe, like the Windows Phone Start screen.
 *
 * <p>The app list sits beside the home screen, on the right (on the left in right-to-left
 * languages). Swiping toward it from the last home screen page opens it and swiping back closes
 * it, with both screens following the finger.
 */
public class HorizontalAllAppsSwipeController extends AbstractStateChangeTouchController {

    public HorizontalAllAppsSwipeController(Launcher launcher) {
        super(launcher, SingleAxisSwipeDetector.HORIZONTAL);
    }

    @Override
    protected boolean canInterceptTouch(MotionEvent ev) {
        if (!Loungechair.isHorizontalAllApps(mLauncher.getDeviceProfile())) {
            return false;
        }
        if (mCurrentAnimation != null) {
            // Let the user catch the list while it is still sliding.
            return true;
        }
        if ((ev.getEdgeFlags() & Utilities.EDGE_NAV_BAR) != 0) {
            // Swipes that start on the gesture bar belong to the system.
            return false;
        }
        if (AbstractFloatingView.getTopOpenView(mLauncher) != null) {
            return false;
        }
        if (mLauncher.isInState(NORMAL)) {
            return isOnLastHomePage();
        }
        if (mLauncher.isInState(ALL_APPS)) {
            ActivityAllAppsContainerView<Launcher> appsView = mLauncher.getAppsView();
            // Leave sideways swipes alone while searching or on the work profile tab.
            return !appsView.isSearching()
                    && appsView.getCurrentPage()
                            == ActivityAllAppsContainerView.AdapterHolder.MAIN;
        }
        return false;
    }

    /**
     * The app list comes after the last home screen page, so earlier pages keep their normal
     * sideways page switching.
     */
    private boolean isOnLastHomePage() {
        Workspace<?> workspace = mLauncher.getWorkspace();
        return !workspace.isPageInTransition()
                && workspace.getNextPage()
                        >= workspace.getPageCount() - workspace.getPanelCount();
    }

    @Override
    protected LauncherState getTargetState(LauncherState fromState, boolean isDragTowardPositive) {
        // The swipe detector mirrors x in right-to-left layouts, so a negative drag always moves
        // toward the app list and a positive drag always moves back toward the home screen.
        if (fromState == NORMAL && !isDragTowardPositive) {
            return ALL_APPS;
        } else if (fromState == ALL_APPS && isDragTowardPositive) {
            return NORMAL;
        }
        return fromState;
    }

    @Override
    protected float initCurrentAnimation() {
        float range = mLauncher.getDeviceProfile().widthPx;
        StateAnimationConfig config = getConfigForStates(mFromState, mToState);
        config.duration = (long) (2 * range);

        if (mCurrentAnimation != null) {
            mCurrentAnimation.getTarget().removeListener(mClearStateOnCancelListener);
            mCurrentAnimation.dispatchOnCancel();
        }
        mCurrentAnimation = mLauncher.getStateManager()
                .createAnimationToNewWorkspace(mToState, config);
        mCurrentAnimation.getTarget().addListener(mClearStateOnCancelListener);

        // Transition progress 1 means the home screen is showing, 0 means the app list is.
        float startShift = mFromState.getVerticalProgress(mLauncher) * range;
        float endShift = mToState.getVerticalProgress(mLauncher) * range;
        return 1 / (endShift - startShift);
    }

    @Override
    protected StateAnimationConfig getConfigForStates(
            LauncherState fromState, LauncherState toState) {
        StateAnimationConfig config = super.getConfigForStates(fromState, toState);
        config.animProps |= StateAnimationConfig.USER_CONTROLLED;
        if (fromState == NORMAL && toState == ALL_APPS) {
            AllAppsSwipeController.applyNormalToAllAppsAnimConfig(mLauncher, config);
        } else if (fromState == ALL_APPS && toState == NORMAL) {
            AllAppsSwipeController.applyAllAppsToNormalConfig(mLauncher, config);
        }
        return config;
    }

    @Override
    protected boolean useVerticalAllAppsOverscroll() {
        // The stock stretch effect pulls the list vertically, which makes no sense sideways.
        return false;
    }
}
