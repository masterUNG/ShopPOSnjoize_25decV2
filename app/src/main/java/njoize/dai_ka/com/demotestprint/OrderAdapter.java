package njoize.dai_ka.com.demotestprint;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{

    private Context context;
    private ArrayList<String> nameFoodStringArrayList, amountStringArrayList, priceStringArrayList;
    private OnClickItem onClickItem;
    private LayoutInflater layoutInflater;

    public OrderAdapter(Context context, ArrayList<String> nameFoodStringArrayList, ArrayList<String> amountStringArrayList, ArrayList<String> priceStringArrayList, OnClickItem onClickItem) {
        this.layoutInflater = LayoutInflater.from(context);
        this.nameFoodStringArrayList = nameFoodStringArrayList;
        this.amountStringArrayList = amountStringArrayList;
        this.priceStringArrayList = priceStringArrayList;
        this.onClickItem = onClickItem;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = layoutInflater.inflate(R.layout.recycler_order, viewGroup, false);
        OrderViewHolder orderViewHolder = new OrderViewHolder(view);

        return orderViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder orderViewHolder, int i) {

        String nameFood = nameFoodStringArrayList.get(i);
        String amount = amountStringArrayList.get(i);
        String price = priceStringArrayList.get(i);

        orderViewHolder.nameFoodTextView.setText(nameFood);
        orderViewHolder.amounTextView.setText(amount);
        orderViewHolder.priceTextView.setText(price);

        orderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem.onClickItem(v, orderViewHolder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return nameFoodStringArrayList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView nameFoodTextView, amounTextView, priceTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            nameFoodTextView = itemView.findViewById(R.id.txtNameFood);
            amounTextView = itemView.findViewById(R.id.txtAmount);
            priceTextView = itemView.findViewById(R.id.txtPrice);

        }
    }
}
