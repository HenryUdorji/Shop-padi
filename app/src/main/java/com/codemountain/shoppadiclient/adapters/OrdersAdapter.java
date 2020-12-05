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
import com.codemountain.shoppadiclient.model.ModelOrder;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {
    private Context context;
    private List<ModelOrder> orderList;
    private OnItemClickListener listener;
    private int selectedPosition = -1;


    public OrdersAdapter(Context context, List<ModelOrder> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_orders_layout, parent, false);
        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        ModelOrder order = getItem(position);

        holder.orderIDText.setText("Order ID: " + order.getOrder_ID());
        holder.orderStateText.setText("State: " + order.getState());
        holder.orderDateText.setText("Date: " + order.getDate());

    }

    @Override
    public int getItemCount() {
        return null != orderList ? orderList.size() : 0;
    }

    public ModelOrder getItem(int position) {
        if (orderList == null || orderList.size() < 0 || orderList.size() == 0) {
            return null;
        }
        if (position < orderList.size() && position >= 0) {
            return orderList.get(position);
        } else {
            return null;
        }

    }

    class OrdersViewHolder extends RecyclerView.ViewHolder{
        private TextView orderIDText, orderStateText, orderDateText;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDateText = itemView.findViewById(R.id.orderDate);
            orderStateText = itemView.findViewById(R.id.orderState);
            orderIDText = itemView.findViewById(R.id.orderID);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.itemClick(v, position);
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
