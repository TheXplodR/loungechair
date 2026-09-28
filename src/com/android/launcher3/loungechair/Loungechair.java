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

import android.content.Context;

import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Utilities;

/**
 * Central switches for Loungechair's Windows Phone-style behaviour, so every place that
 * deviates from stock Launcher3 asks the same question.
 */
public final class Loungechair {

    private Loungechair() { }

    /**
     * Whether the app list sits beside the home screen and opens with a sideways swipe,
     * instead of sliding up from the bottom. Phones only: tablets keep the stock bottom sheet.
     */
    public static boolean isHorizontalAllApps(DeviceProfile dp) {
        return !dp.isTablet;
    }

    /**
     * How far the home screen moves while the app list slides in: one screen width, toward
     * the start side (left, or right in right-to-left languages).
     */
    public static float getHomeShiftForAllApps(Context context, DeviceProfile dp) {
        return Utilities.isRtl(context.getResources()) ? dp.widthPx : -dp.widthPx;
    }
}
