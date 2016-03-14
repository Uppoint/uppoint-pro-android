package com.uppoint.android.pro.login.fragment;

import com.uppoint.android.pro.R;
import com.uppoint.android.pro.core.fragment.BaseFragment;
import com.uppoint.android.pro.core.util.Preconditions;

import android.content.Context;
import android.view.View;
import android.widget.Button;

/**
 */
public class GetStartedFragment extends BaseFragment<Void> implements View.OnClickListener {

    private Callback mCallback;

    public static GetStartedFragment newInstance() {
        return new GetStartedFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Preconditions.instanceOf(context, Callback.class, "Activity doesn't implement the callback interface");

        mCallback = (Callback) context;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_splash;
    }

    @Override
    protected void initUI(View view) {
        final Button getStartedButton = (Button) view.findViewById(R.id.splash_get_started_button);
        getStartedButton.setOnClickListener(this);
    }

    @Override
    protected void updateUI(Void model) {
        // do nothing
    }

    @Override
    public void onClick(View v) {
        if (R.id.splash_get_started_button == v.getId()) {
            onGetStartedClick();
        }
    }

    private void onGetStartedClick() {
        mCallback.onPickAccountSelected();
    }

    public interface Callback {

        void onPickAccountSelected();

    }
}
