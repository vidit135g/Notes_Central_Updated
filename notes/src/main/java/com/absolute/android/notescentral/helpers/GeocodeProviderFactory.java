/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  io.nlopez.smartlocation.location.LocationProvider
 *  io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesWithFallbackProvider
 */
package com.absolute.android.notescentral.helpers;

import android.content.Context;
import io.nlopez.smartlocation.location.LocationProvider;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesWithFallbackProvider;

public class GeocodeProviderFactory {
    private GeocodeProviderFactory() {
    }

    public static LocationProvider getProvider(Context context) {
        return new LocationGooglePlayServicesWithFallbackProvider(context);
    }
}

