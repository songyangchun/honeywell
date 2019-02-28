package com.honeywell.honeywellproject.BleTaskModule.AddressSearch.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.honeywell.honeywellproject.BleTaskModule.AddressSearch.OnItemClickListener;
import com.honeywell.honeywellproject.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ImageViewHolder>implements View.OnClickListener {
    private Context mContext;
    private List<Integer> mints;
    private OnItemClickListener itemClickListener;
    //可用地址

    Drawable drawableGray,drawableYellow,drawableGreen,drawableBlue;




    @Override
    public void onClick(View v) {

    }




    public ItemAdapter(List<Integer> intInfos){
        this.mints=intInfos;
    }



    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        mContext = parent.getContext();

         drawableGray = mContext.getResources().getDrawable(R.drawable.gray);
         drawableYellow = mContext.getResources().getDrawable(R.drawable.yellow);
         drawableGreen = mContext.getResources().getDrawable(R.drawable.green);
         drawableBlue = mContext.getResources().getDrawable(R.drawable.blue);
          View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
         ImageViewHolder holder = new ImageViewHolder(v,itemClickListener);
         return holder;


    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        if(mints.get(position)==0)
            ((ImageViewHolder)holder).mImageView.setImageDrawable(drawableGray);
        else if(mints.get(position)==1) {
            ((ImageViewHolder) holder).mImageView.setImageDrawable(drawableGreen);

        }

        else if(mints.get(position)==2)
            ((ImageViewHolder)holder).mImageView.setImageDrawable(drawableYellow);
        else if(mints.get(position)==3) {
            ((ImageViewHolder) holder).mImageView.setImageDrawable(drawableBlue);

        }
    }





    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){

        this.itemClickListener=onItemClickListener;

    }


    @Override
    public int getItemCount() {
        return mints.size();
    }
}




