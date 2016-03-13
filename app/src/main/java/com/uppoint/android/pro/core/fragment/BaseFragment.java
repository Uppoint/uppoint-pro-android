package com.uppoint.android.pro.core.fragment;

import com.uppoint.android.pro.core.EndpointCommand;
import com.uppoint.android.pro.core.activity.BaseActivity;
import com.uppoint.android.pro.core.dialog.ErrorDialog;
import com.uppoint.android.pro.core.dialog.ProgressDialog;
import com.uppoint.android.pro.core.util.Preconditions;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bg.dalexiev.bender.content.ResolverCommandBuilder;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 */
public abstract class BaseFragment<M> extends Fragment implements LoaderManager.LoaderCallbacks<M> {

    protected static final int NO_LOADER = -1;

    private ResolverCommandBuilder mCommandBuilder;

    private CompositeSubscription mCompositeSubscription;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Preconditions.instanceOf(context, BaseActivity.class,
                "Trying to attach fragment to a context that is not BaseActivity");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCommandBuilder = new ResolverCommandBuilder();

        loadModel();
    }

    private void loadModel() {
        final int loaderId = getLoaderId();
        if (NO_LOADER != loaderId) {
            getLoaderManager().restartLoader(loaderId, getLoaderArguments(), this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResId(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initUI(view);
    }

    @Override
    public void onStart() {
        super.onStart();

        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (isChangingConfigurations()) {
            onRetainEndpoints();
        } else {
            mCompositeSubscription.unsubscribe();
        }
    }

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

    @LayoutRes
    protected abstract int getLayoutResId();

    protected abstract int getLoaderId();

    protected abstract void initUI(View view);

    protected abstract void updateUI(M model);

    protected ResolverCommandBuilder getContentResolverCommandBuilder() {
        return mCommandBuilder;
    }

    protected void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    protected boolean isChangingConfigurations() {
        Preconditions.stateNonNull(getActivity(), "Trying to detect configuration change from detached fragment");

        return getActivity().isChangingConfigurations();
    }

    protected void onRetainEndpoints() {
        // do nothing
    }

    protected void putEndpoint(@NonNull String key, @NonNull EndpointCommand<?, ?> endpointCommand) {
        Preconditions.stateNonNull(getActivity(), "Trying to access retained cache from detached fragment");

        final BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.putEndpoint(key, endpointCommand);
    }

    protected EndpointCommand<?, ?> getEndpoint(@NonNull String key) {
        Preconditions.stateNonNull(getActivity(), "Trying to access retained cache from detached fragment");

        final BaseActivity baseActivity = (BaseActivity) getActivity();
        return baseActivity.getEndpoint(key);
    }

    protected void showErrorDialog(@StringRes int messageResId) {
        ErrorDialog.newInstance(messageResId).show(getFragmentManager(), ErrorDialog.TAG);
    }

    protected void showLoadingDialog(@StringRes int messageResId) {
        ProgressDialog.newInstance(messageResId).show(getFragmentManager(), ProgressDialog.TAG);
    }

    protected void dismissLoadingDialog() {
        final ProgressDialog dialog = (ProgressDialog) getFragmentManager().findFragmentByTag(ProgressDialog.TAG);
        if (dialog != null) {
            dialog.dismiss();
        }

    }
}
