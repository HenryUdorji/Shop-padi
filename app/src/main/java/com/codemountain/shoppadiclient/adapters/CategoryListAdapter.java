package com.codemountain.shoppadiclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codemountain.shoppadiclient.OnItemClickListener;
import com.codemountain.shoppadiclient.R;
import com.codemountain.shoppadiclient.model.ModelCategoryList;

import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryListViewHolder> {
    private Context context;
    private List<ModelCategoryList> categoriesList;
    private OnItemClickListener listener;
    private int selectedPosition = -1;


    public CategoryListAdapter(Context context, List<ModelCategoryList> categoriesList) {
        this.context = context;
        this.categoriesList = categoriesList;
    }

    @NonNull
    @Override
    public CategoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_category_list_layout, parent, false);
        return new CategoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListViewHolder holder, int position) {
        ModelCategoryList categories = getItem(position);
        holder.categoryNameText.setText(categories.getName());

        //Highlight selected category
        /*if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            holder.categoryNameText.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
        else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            holder.categoryNameText.setTextColor(ContextCompat.getColor(context, R.color.black));
        }*/
    }

    @Override
    public int getItemCount() {
        return null != categoriesList ? categoriesList.size() : 0;
    }

    public ModelCategoryList getItem(int position) {
        if (categoriesList == null || categoriesList.size() < 0 || categoriesList.size() == 0) {
            return null;
        }
        if (position < categoriesList.size() && position >= 0) {
            return categoriesList.get(position);
        } else {
            return null;
        }

    }

    class CategoryListViewHolder extends RecyclerView.ViewHolder{
        private TextView categoryNameText;

        public CategoryListViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameText = itemView.findViewById(R.id.categoryName);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.itemClick(v, position);
                    notifyItemChanged(selectedPosition);
                    selectedPosition = position;
                    notifyItemChanged(selectedPosition);
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
