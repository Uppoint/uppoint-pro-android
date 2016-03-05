package com.uppoint.android.pro.login.adapter;

import com.uppoint.android.pro.R;

import android.accounts.Account;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class PickAccountAdapter extends RecyclerView.Adapter<PickAccountAdapter.AccountViewHolder> {

    private static final int VIEW_TYPE_DEFAULT = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    private final Context mContext;

    private List<Account> mData;

    private OnItemClickListener mItemClickListener;

    public PickAccountAdapter(Context context) {
        mContext = context;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER;
        }

        return VIEW_TYPE_DEFAULT;
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_account, parent, false);
        return new AccountViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, final int position) {
        if (VIEW_TYPE_DEFAULT == holder.mViewType) {
            final Account account = mData.get(position);
            holder.mEmail.setText(account.name);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(position);
                    }
                }
            });
        } else {
            holder.mEmail.setText(R.string.pick_account_add_account);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onFooterClick();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 1;
        }

        return mData.size() + 1;
    }

    public Account getItemAtPosition(int position) {
        if (mData == null) {
            return null;
        }

        return mData.get(position);
    }

    public void swapData(List<Account> accounts) {
        if (mData == accounts) {
            return;
        }

        List<Account> old = mData;
        if (old != null) {
            notifyItemRangeRemoved(0, old.size());
        }

        if (accounts == null) {
            mData = null;
        } else {
            mData = new ArrayList<>(accounts);
            notifyItemRangeInserted(0, accounts.size());
        }
    }

    protected class AccountViewHolder extends RecyclerView.ViewHolder {

        private final int mViewType;
        private final TextView mEmail;

        public AccountViewHolder(View itemView, int viewType) {
            super(itemView);

            mViewType = viewType;
            mEmail = (TextView) itemView.findViewById(R.id.list_item_account_email);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

        void onFooterClick();

    }
}
