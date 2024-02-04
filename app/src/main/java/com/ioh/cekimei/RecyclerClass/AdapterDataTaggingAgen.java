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

public class AdapterDataTaggingAgen extends RecyclerView.Adapter<AdapterDataTaggingAgen.HolderData> {
    ArrayList listData;
    LayoutInflater inflater;
    Context context;

    public AdapterDataTaggingAgen (Context context, ArrayList list){
        this.inflater = LayoutInflater.from(context);
        this.listData = list;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterDataTaggingAgen.HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_tagging_agen, parent, false);
        return new AdapterDataTaggingAgen.HolderData(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDataTaggingAgen.HolderData holder, int position) {
        String[] list_split = listData.get(position).toString().split("~");
        holder.textMSISDNAgen.setText(list_split[0]);
        holder.textNamaAgen.setText(list_split[1]);
        holder.textEmailAgen.setText(list_split[2]);
        //holder.textStatus.setText(list_split[3]);
        //holder.textTanggal.setText(list_split[4]);


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
        TextView textMSISDNAgen, textNamaAgen, textEmailAgen, textStatus, textTanggal;
        CardView CardHistory;

        public HolderData(@NonNull View itemView) {
            super(itemView);

            textMSISDNAgen = (TextView) itemView.findViewById(R.id.textMSISDNAgen);
            textNamaAgen = (TextView) itemView.findViewById(R.id.textNamaAgen);
            textEmailAgen = (TextView) itemView.findViewById(R.id.textEmailAgen);
            textStatus = (TextView) itemView.findViewById(R.id.textStatus);
            textTanggal = (TextView) itemView.findViewById(R.id.textTanggal);
            CardHistory = (CardView) itemView.findViewById(R.id.CardHistory);
        }
    }
}
