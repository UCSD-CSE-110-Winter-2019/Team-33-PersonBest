package com.android.personbest.Adapters;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.personbest.Models.FriendModel;
import com.android.personbest.R;
import com.android.personbest.ViewHolders.FriendListViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class FriendListAdapter extends FirestoreRecyclerAdapter<FriendModel, FriendListViewHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FriendListAdapter(FirestoreRecyclerOptions<FriendModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(FriendListViewHolder holder, int position, FriendModel model) {
        Log.d("DEBUG", model.getName());
        holder.setName(model.getName());
    }

    @NonNull
    @Override
    public FriendListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.friend_card, viewGroup, false);

        return new FriendListViewHolder(view);
    }
}
