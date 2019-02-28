package com.honeywell.honeywellproject.BleTaskModule.AddressSearch.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.honeywell.honeywellproject.BleTaskModule.AddressSearch.OnItemClickListener;
import com.honeywell.honeywellproject.R;

public  class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private OnItemClickListener mitemClickListener;
    public ImageView mImageView;

    public ImageViewHolder(View view, OnItemClickListener itemClickListener) {
        super(view);
        mitemClickListener= itemClickListener;
        view.setOnClickListener(this);
        mImageView=(ImageView) view.findViewById(R.id.iv_item);


    }

    @Override
    public void onClick(View v) {
        if (mitemClickListener != null) {
            mitemClickListener.onItemClick(v, getPosition());
        }
    }
}
