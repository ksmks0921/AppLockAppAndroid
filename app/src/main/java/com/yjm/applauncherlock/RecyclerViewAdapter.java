package com.yjm.applauncherlock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by Aws on 28/01/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext ;
    private List<AppList> mData ;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AppList item);
    }

    public RecyclerViewAdapter(Context mContext, List<AppList> mData, OnItemClickListener listener) {
        this.mContext = mContext;
        this.mData = mData;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.app_name.setText(mData.get(position).getName());
        holder.app_icon.setImageDrawable(mData.get(position).getIcon());
        holder.bind(mData.get(position), listener);
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                Intent intent = new Intent(mContext,Book_Activity.class);
////
////                // passing data to the book activity
////                intent.putExtra("Title",mData.get(position).getTitle());
////                intent.putExtra("Description",mData.get(position).getDescription());
////                intent.putExtra("Thumbnail",mData.get(position).getThumbnail());
////                // start the activity
////                mContext.startActivity(intent);
//                Intent intent;
//                intent = getPackageManager().getLaunchIntentForPackage(mData.get(position).getPackages());
//
//            }
//        });



    }



    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView app_name;
        ImageView app_icon;
        CardView cardView ;

        public MyViewHolder(View itemView) {
            super(itemView);

            app_name = (TextView) itemView.findViewById(R.id.name_lock) ;
            app_icon = (ImageView) itemView.findViewById(R.id.icon_lock);
            cardView = (CardView) itemView.findViewById(R.id.card_view);


        }
        public void bind(final AppList item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);

                }
            });
        }
    }




}
