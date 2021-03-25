package com.greenmars.distribuidor.adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.greenmars.distribuidor.R;
import com.greenmars.distribuidor.model.Slider;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {
    private final Context context;
    private List<Slider> sliderItems = new ArrayList<>();

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public void renewItems(List<Slider> sliderItems) {
        this.sliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.sliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Slider sliderItem) {
        this.sliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(final SliderAdapterVH viewHolder, final int position) {
        Slider sItem = sliderItems.get(position);
        viewHolder.textDescripcion.setText(sItem.getNombre());
        viewHolder.textDescripcion.setTextSize(16);
        viewHolder.textDescripcion.setTextColor(Color.WHITE);
        Glide.with(viewHolder.itemview)
                .load(sItem.getImage())
                .fitCenter()
                .into(viewHolder.imageViewBackground);
    }

    @Override
    public int getCount() {
        return sliderItems.size();
    }

    static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        View itemview;
        ImageView imageViewBackground;
        ImageView imageGirfContainer;
        TextView textDescripcion;

        public SliderAdapterVH(View itemview) {
            super(itemview);
            this.itemview = itemview;
            imageViewBackground = itemview.findViewById(R.id.iv_auto_image_slider);
            imageGirfContainer = itemview.findViewById(R.id.iv_gif_container);
            textDescripcion = itemview.findViewById(R.id.tv_auto_image_slider);
        }
    }
}
