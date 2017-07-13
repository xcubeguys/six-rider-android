package com.tommy.rider.adapter;

import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.tommy.rider.R;
import com.tommy.rider.ReferralUsersActivity;

import java.util.ArrayList;
import java.util.List;

public class ReferralUsersListAdapter extends RecyclerView.Adapter {
    private List<YourTrips> usersListItems;
    ReferralUsersActivity activity;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public ReferralUsersListAdapter(ReferralUsersActivity activity, ArrayList<YourTrips> usersListItems, RecyclerView recyclerView) {
        this.activity = activity;
        this.usersListItems= usersListItems;

        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if(!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)){
                        if(onLoadMoreListener != null){
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;

        if(viewType == VIEW_ITEM){
            View convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.refusers_items, parent, false);
            FontChangeCrawler fontChanger = new FontChangeCrawler(activity.getAssets(), activity.getString(R.string.app_font));
            fontChanger.replaceFonts((ViewGroup)convertView);
            viewHolder = new MyViewHolder(convertView);
        }else{
            View convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.loadmore_progress_bar, parent, false);
            viewHolder = new ProgressViewHolder(convertView);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof MyViewHolder){
            YourTrips trips = usersListItems.get(position);

            try {

                Glide.with(activity).load(trips.getUserImage()).asBitmap().centerCrop().placeholder(R.drawable.account_circle_grey).into(new BitmapImageViewTarget( ((MyViewHolder) holder).userImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(activity.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        ((MyViewHolder) holder).userImage.setImageDrawable(circularBitmapDrawable);
                    }
                });

                ((MyViewHolder) holder).userName.setText(trips.getUserName());
                ((MyViewHolder) holder).userType.setText(trips.getUserType());

            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }else{
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName,userType;

        public MyViewHolder(View convertView) {
            super(convertView);
            userImage=(ImageView)convertView.findViewById(R.id.userpic);
            userName=(TextView)convertView.findViewById(R.id.name);
            userType=(TextView)convertView.findViewById(R.id.type);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    @Override
    public int getItemCount() {
        return usersListItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return usersListItems.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
    }

}
