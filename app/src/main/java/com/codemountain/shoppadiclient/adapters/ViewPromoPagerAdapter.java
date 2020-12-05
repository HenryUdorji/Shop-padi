package com.codemountain.shoppadiclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.codemountain.shoppadiclient.R;
import com.codemountain.shoppadiclient.model.ModelViewPromo;

import java.util.List;

public class ViewPromoPagerAdapter extends PagerAdapter {

    private Context context;
    private List<ModelViewPromo> viewPromoList;

    public ViewPromoPagerAdapter(Context context, List<ModelViewPromo> viewPromoList) {
        this.context = context;
        this.viewPromoList = viewPromoList;
    }

    @Override
    public int getCount() {
        return viewPromoList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.single_view_promo, null);

        String image = viewPromoList.get(position).getImage();
        ImageView imageView = view.findViewById(R.id.single_promo_image);
        Glide.with(context).load(image).placeholder(R.drawable.ic_launcher_background).into(imageView);

        view.setOnClickListener(v -> Toast.makeText(context, "Clicked!!!", Toast.LENGTH_SHORT).show());
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

}
