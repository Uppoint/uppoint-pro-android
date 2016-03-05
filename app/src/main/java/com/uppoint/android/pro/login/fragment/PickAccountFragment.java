package com.uppoint.android.pro.login.fragment;

import com.appspot.uppoint_api.uppointApi.model.ProUserPayload;
import com.uppoint.android.pro.R;
import com.uppoint.android.pro.core.fragment.BaseFragment;
import com.uppoint.android.pro.core.util.Preconditions;
import com.uppoint.android.pro.core.util.SharedPreferenceConstants;
import com.uppoint.android.pro.login.adapter.PickAccountAdapter;
import com.uppoint.android.pro.login.endpoint.GetUser;
import com.uppoint.android.pro.login.loader.GoogleAccountLoader;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import rx.Observer;

/**
 */
public class PickAccountFragment extends BaseFragment<List<Account>> implements PickAccountAdapter.OnItemClickListener {

    private static final int LOADER_ACCOUNTS = 1;

    private static final String KEY_GET_USER = "get_user";

    private PickAccountAdapter mAdapter;

    private Callback mCallback;

    private GetUser mGetUserEndpoint;
    private GetUserObserver mGetUserObserver;

    public static PickAccountFragment newInstance() {
        return new PickAccountFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Preconditions
                .instanceOf(context, Callback.class, "Activity doesn't implement the required callback interface");
        mCallback = (Callback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new PickAccountAdapter(getContext());
        mAdapter.setItemClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        mGetUserObserver = new GetUserObserver();
        mGetUserEndpoint = (GetUser) getEndpoint(KEY_GET_USER);
        if (mGetUserEndpoint != null) {
            addSubscription(mGetUserEndpoint.toObservable().subscribe(mGetUserObserver));
        }
    }

    @Override
    protected void onRetainEndpoints() {
        super.onRetainEndpoints();

        putEndpoint(KEY_GET_USER, mGetUserEndpoint);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_pick_account;
    }

    @Override
    protected int getLoaderId() {
        return LOADER_ACCOUNTS;
    }

    @Override
    protected void initUI(View view) {
        final RecyclerView accountList = (RecyclerView) view.findViewById(R.id.account_list);
        accountList.setLayoutManager(new LinearLayoutManager(getContext()));
        accountList.setHasFixedSize(true);
        accountList.setAdapter(mAdapter);
    }

    @Override
    protected void updateUI(List<Account> model) {
        mAdapter.swapData(model);
    }

    @Override
    public Loader<List<Account>> onCreateLoader(int id, Bundle args) {
        return new GoogleAccountLoader(getContext());
    }

    @Override
    public void onItemClick(int position) {
        final Account account = mAdapter.getItemAtPosition(position);

        final SharedPreferences sharedPreferences = getContext()
                .getSharedPreferences(SharedPreferenceConstants.SETTINGS_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(SharedPreferenceConstants.KEY_USER, account.name).apply();

        if (mGetUserEndpoint == null) {
            mGetUserEndpoint = new GetUser().using(getContext()).withEmail(account.name);
        }
        addSubscription(mGetUserEndpoint.toObservable().subscribe(mGetUserObserver));
    }

    @Override
    public void onFooterClick() {
        mCallback.onAddGoogleAccount();
    }

    private class GetUserObserver implements Observer<ProUserPayload> {

        @Override
        public void onCompleted() {
            // do nothing
        }

        @Override
        public void onError(Throwable e) {
            showErrorDialog(R.string.pick_account_error_message);
        }

        @Override
        public void onNext(ProUserPayload proUserPayload) {
            if (proUserPayload != null) {
                Toast.makeText(getContext(), "User found. Proceed to main screen.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface Callback {

        void onAddGoogleAccount();

    }
}
