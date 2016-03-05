package com.uppoint.android.pro.core.fragment;

import com.uppoint.android.pro.core.EndpointCommand;
import com.uppoint.android.pro.core.util.Preconditions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class EndpointCacheFragment extends Fragment {

    public static final String TAG = "endpoint_cache";

    private Map<String, EndpointCommand<?, ?>> mCache;

    public static EndpointCacheFragment newInstance() {
        return new EndpointCacheFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mCache = new HashMap<>();
    }

    public void put(@NonNull String key, @NonNull EndpointCommand<?, ?> command) {
        Preconditions.nonNull(key, "Required parameter key is missing");
        Preconditions.nonNull(command, "Required parameter command is missing");

        mCache.put(key, command);
    }

    @Nullable
    public EndpointCommand<?, ?> get(@NonNull String key) {
        Preconditions.nonNull(key, "Required parameter key is missing");

        return mCache.remove(key);
    }
}
