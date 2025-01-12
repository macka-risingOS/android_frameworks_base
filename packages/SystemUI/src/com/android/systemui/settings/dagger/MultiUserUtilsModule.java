/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.android.systemui.settings.dagger;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.UserManager;

import com.android.systemui.CoreStartable;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.settings.DisplayTracker;
import com.android.systemui.settings.DisplayTrackerImpl;
import com.android.systemui.settings.MaxBrightnessDialogController;
import com.android.systemui.settings.UserContentResolverProvider;
import com.android.systemui.settings.UserContextProvider;
import com.android.systemui.settings.UserFileManager;
import com.android.systemui.settings.UserFileManagerImpl;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.settings.UserTrackerImpl;
import com.android.systemui.settings.brightness.BrightnessController.OnMaxBrightnessCallback;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

/**
 * Dagger Module for classes found within the com.android.systemui.settings package.
 */
@Module
public abstract class MultiUserUtilsModule {
    @Binds
    @SysUISingleton
    abstract UserContextProvider bindUserContextProvider(UserTracker tracker);

    @Binds
    @SysUISingleton
    abstract UserContentResolverProvider bindUserContentResolverProvider(
            UserTracker tracker);

    @SysUISingleton
    @Provides
    static UserTracker provideUserTracker(
            Context context,
            UserManager userManager,
            IActivityManager iActivityManager,
            DumpManager dumpManager,
            @Background Handler handler
    ) {
        int startingUser = ActivityManager.getCurrentUser();
        UserTrackerImpl tracker = new UserTrackerImpl(context, userManager, iActivityManager,
                dumpManager, handler);
        tracker.initialize(startingUser);
        return tracker;
    }

    @SysUISingleton
    @Provides
    static DisplayTracker provideDisplayTracker(
            DisplayManager displayManager,
            @Background Handler handler
    ) {
        return new DisplayTrackerImpl(displayManager, handler);
    }

    @SysUISingleton
    @Provides
    static OnMaxBrightnessCallback provideOnMaxBrightnessCallback(
            Context context,
            ActivityStarter activityStarter
    ) {
        return new MaxBrightnessDialogController(context, activityStarter);
    }

    @Binds
    @IntoMap
    @ClassKey(UserFileManagerImpl.class)
    abstract CoreStartable bindUserFileManagerCoreStartable(UserFileManagerImpl sysui);

    @Binds
    abstract UserFileManager bindUserFileManager(UserFileManagerImpl impl);
}
