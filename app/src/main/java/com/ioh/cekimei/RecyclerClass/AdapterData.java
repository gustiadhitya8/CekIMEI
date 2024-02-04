package com.ioh.cekimei.RecyclerClass;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ioh.cekimei.R;

import java.util.ArrayList;

public class AdapterData extends RecyclerView.Adapter<AdapterData.HolderData> {
    ArrayList listData;
    LayoutInflater inflater;
    Context context;

    public AdapterData (Context context, ArrayList list){
        this.inflater = LayoutInflater.from(context);
        this.listData = list;
        this.context = context;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_history, parent, false);
        return new HolderData(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        String[] list_split = listData.get(position).toString().split("~");
        holder.textIMEI.setText(list_split[0]);
        holder.textDatetime.setText(list_split[1]);
        holder.textStatus.setText(list_split[2]);

        holder.textModel.setText(list_split[3]);
        holder.textMSISDN.setText(list_split[4]);

        if(list_split[5].equals("0")){
            holder.textStatus.setTextColor(Color.GREEN);
        }

        Drawable drawableDown = context.getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24);
        Drawable drawableUp = context.getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24);

        holder.imgArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.imgArrow.getDrawable() ==  drawableDown){
                    holder.imgArrow.setImageDrawable(drawableUp);
                    holder.LayoutDetail.setVisibility(View.VISIBLE);
                }else{
                    holder.imgArrow.setImageDrawable(drawableDown);
                    holder.LayoutDetail.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void filterList(ArrayList list){
        listData = list;
        notifyDataSetChanged();
    }

    public class HolderData extends RecyclerView.ViewHolder{
        TextView textIMEI, textDatetime, textStatus;
        TextView textModel, textMSISDN;
        ImageView imgArrow;
        LinearLayout LayoutDetail;
        CardView CardHistory;

        public HolderData(@NonNull View itemView) {
            super(itemView);

            textIMEI = (TextView) itemView.findViewById(R.id.textIMEI);
            textDatetime = (TextView) itemView.findViewById(R.id.textDatetime);
            textStatus = (TextView) itemView.findViewById(R.id.textStatus);

            textModel = (TextView) itemView.findViewById(R.id.textModel);
            textMSISDN = (TextView) itemView.findViewById(R.id.textMSISDN);

            imgArrow = (ImageView) itemView.findViewById(R.id.imgArrow);
            LayoutDetail = (LinearLayout) itemView.findViewById(R.id.LayoutDetail);
            CardHistory = (CardView) itemView.findViewById(R.id.CardHistory);
        }
    }
}
