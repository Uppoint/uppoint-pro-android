package com.uppoint.android.pro.core.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

/**
 */
public abstract class LoaderFragment<M> extends BaseFragment<M> implements LoaderManager.LoaderCallbacks<M> {

    protected static final int NO_LOADER = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadModel();
    }

    private void loadModel() {
        final int loaderId = getLoaderId();
        if (NO_LOADER != loaderId) {
            getLoaderManager().restartLoader(loaderId, getLoaderArguments(), this);
        }
    }

    protected abstract int getLoaderId();

    @Nullable
    protected Bundle getLoaderArguments() {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<M> loader, M data) {
        updateUI(data);
    }

    @Override
    public void onLoaderReset(Loader<M> loader) {
        // do nothing
    }


}
