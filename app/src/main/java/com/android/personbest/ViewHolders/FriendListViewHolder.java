package com.android.personbest.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.android.personbest.R;

public class FriendListViewHolder extends RecyclerView.ViewHolder {
    private View view;

    public FriendListViewHolder(View view) {
        super(view);
        this.view = view;
    }

    public void setName(String name) {
        TextView textView = view.findViewById(R.id.friends_list_name);

        textView.setText(name);
    }
}
