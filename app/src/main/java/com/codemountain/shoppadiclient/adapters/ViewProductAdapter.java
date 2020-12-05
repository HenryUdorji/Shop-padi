package com.codemountain.shoppadiclient.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.codemountain.shoppadiclient.OnItemClickListener;
import com.codemountain.shoppadiclient.R;
import com.codemountain.shoppadiclient.model.ModelProducts;

import java.util.List;

public class ViewProductAdapter extends RecyclerView.Adapter<ViewProductAdapter.ViewProductHolder> {

    private Context context;
    private List<ModelProducts> productsList;
    private OnItemClickListener listener;

    public ViewProductAdapter(Context context, List<ModelProducts> productsList) {
        this.context = context;
        this.productsList = productsList;
    }

    @NonNull
    @Override
    public ViewProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_view_product, parent, false);
        return new ViewProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewProductHolder holder, int position) {
        ModelProducts modelProducts = getItem(position);
        String image = modelProducts.getImage();
        String name = modelProducts.getName();
        String price = modelProducts.getPrice();

        Glide.with(context).load(image)
                .placeholder(R.drawable.ic_launcher_background)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.productImage);

        holder.productName.setText(name);
        holder.productPrice.setText(price);

    }

    @Override
    public int getItemCount() {
            return null != productsList ? productsList.size() : 0;
    }

    public ModelProducts getItem(int position) {
        if (productsList == null || productsList.size() < 0 || productsList.size() == 0) {
            return null;
        }
        if (position < productsList.size() && position >= 0) {
            return productsList.get(position);
        } else {
            return null;
        }

    }

    class ViewProductHolder extends RecyclerView.ViewHolder{
        private ImageView productImage;
        private TextView productName, productPrice;
        private ProgressBar progressBar;

        public ViewProductHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.view_product_image);
            productName = itemView.findViewById(R.id.nameText);
            productPrice = itemView.findViewById(R.id.priceText);
            progressBar = itemView.findViewById(R.id.productProgress);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.itemClick(v, position);
                }
                else {
                    Toast.makeText(context, "Null position clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void setOnItemClickedListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
