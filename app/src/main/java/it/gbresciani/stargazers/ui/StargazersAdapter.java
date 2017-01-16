package it.gbresciani.stargazers.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Collections;
import java.util.List;

import it.gbresciani.stargazers.R;
import it.gbresciani.stargazers.network.Stargazer;

/**
 * Adapter used to display a list of Stargazers. It has two different types of view, one for a stargazer
 * item and one for the loading progressbar that is added on request to the bottom of the list.
 */
public class StargazersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FOOTER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Stargazer> stargazers = Collections.emptyList();
    private boolean showLoadingFooter = false;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stargazer, parent, false);
            return new StargazerViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new FooterViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ITEM) {
            Stargazer stargazer = stargazers.get(position);
            ((StargazerViewHolder) holder).userTextView.setText(stargazer.getLogin());
            ((StargazerViewHolder) holder).userImageView.setImageURI(stargazer.getAvatarUrl());
        }
    }

    @Override
    public int getItemCount() {
        int count = stargazers.size();
        if (showLoadingFooter) count++;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == stargazers.size() && showLoadingFooter) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    /**
     * Set a new list of stargazers and notify the change.
     *
     * @param newStargazers the new list.
     */
    public void setStargazers(List<Stargazer> newStargazers) {
        stargazers = newStargazers;
        notifyDataSetChanged();
    }

    /**
     * Show or hide a loading footer to the bottom of the list.
     */
    public void showLoadingFooter(boolean showLoadingMore) {
        showLoadingFooter = showLoadingMore;
    }

    private static class StargazerViewHolder extends RecyclerView.ViewHolder {
        TextView userTextView;
        SimpleDraweeView userImageView;

        StargazerViewHolder(View itemView) {
            super(itemView);
            userTextView = (TextView) itemView.findViewById(R.id.userTextView);
            userImageView = (SimpleDraweeView) itemView.findViewById(R.id.userImageView);
        }
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {
        FrameLayout base;

        FooterViewHolder(View itemView) {
            super(itemView);
            this.base = (FrameLayout) itemView;
        }
    }
}
