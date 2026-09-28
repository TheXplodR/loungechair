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
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;

import java.util.Locale;

import app.lawnchair.theme.color.tokens.ColorTokens;

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

    /**
     * Whether the A-Z app list shows one app per row with a letter tile starting each section,
     * like the Windows Phone app list, instead of a grid of icons.
     */
    public static boolean isAllAppsList(DeviceProfile dp) {
        return isHorizontalAllApps(dp);
    }

    /** Columns in the A-Z app list and the work profile list. Search results keep the grid. */
    public static int getAllAppsColumns(DeviceProfile dp) {
        return isAllAppsList(dp) ? 1 : dp.numShownAllAppsColumns;
    }

    /** Gives an app row its list-sized name and the gap between icon and name. */
    public static void styleAllAppsRow(BubbleTextView row) {
        Resources res = row.getResources();
        row.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                res.getDimension(R.dimen.loungechair_all_apps_row_text_size));
        row.setCompoundDrawablePadding(
                res.getDimensionPixelSize(R.dimen.loungechair_all_apps_row_icon_text_gap));
    }

    /** Shows a section's letter on its tile, in lower case like on Windows Phone. */
    public static void bindSectionHeader(View header, String sectionName) {
        TextView letter = header.findViewById(R.id.loungechair_section_letter);
        letter.setText(sectionName.toLowerCase(Locale.getDefault()));
        letter.setBackgroundColor(
                ColorTokens.LoungechairSectionTile.resolveColor(header.getContext()));
    }
}
