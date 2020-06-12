package com.codewithshubh.covid_19tracker.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codewithshubh.covid_19tracker.EachDistrictDataActivity;
import com.codewithshubh.covid_19tracker.Models.DistrictWiseModel;
import com.codewithshubh.covid_19tracker.R;

import java.text.NumberFormat;
import java.util.ArrayList;

public class DistrictWiseAdapter extends RecyclerView.Adapter<DistrictWiseAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<DistrictWiseModel> arrayList;
    private DistrictWiseAdapter.OnItemClickListner mListner;
    private static final String DISTRICT_NAME = "districtName";
    private static final String DISTRICT_CONFIRMED = "districtConfirmed";
    private static final String DISTRICT_ACTIVE = "districtActive";
    private static final String DISTRICT_DECEASED = "districtDeaceased";
    private static final String DISTRICT_NEW_CONFIRMED = "districtNewConfirmed";
    private static final String DISTRICT_NEW_RECOVERED = "districtNewRecovered";
    private static final String DISTRICT_NEW_DECEASED = "districtNewDeceased";
    private static final String DISTRICT_RECOVERED = "districtRecovered";

    public interface OnItemClickListner {
        void onItemClick(int position);
    }

    public void setOnItemClickListner(DistrictWiseAdapter.OnItemClickListner listner) {
        mListner = listner;
    }

    public DistrictWiseAdapter(Context context, ArrayList<DistrictWiseModel> districtModelArrayList) {
        mContext = context;
        arrayList = districtModelArrayList;
    }

    @NonNull
    @Override
    public DistrictWiseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.districtwise_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DistrictWiseAdapter.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistrictWiseModel clickedItem = arrayList.get(position);
                Intent perDistrictIntent = new Intent(mContext, EachDistrictDataActivity.class);

                perDistrictIntent.putExtra(DISTRICT_NAME, clickedItem.getDistrict());
                perDistrictIntent.putExtra(DISTRICT_CONFIRMED, clickedItem.getConfirmed());
                perDistrictIntent.putExtra(DISTRICT_ACTIVE, clickedItem.getActive());
                perDistrictIntent.putExtra(DISTRICT_DECEASED, clickedItem.getDeceased());
                perDistrictIntent.putExtra(DISTRICT_NEW_CONFIRMED, clickedItem.getNewConfirmed());
                perDistrictIntent.putExtra(DISTRICT_NEW_RECOVERED, clickedItem.getNewRecovered());
                perDistrictIntent.putExtra(DISTRICT_NEW_DECEASED, clickedItem.getNewDeceased());;
                perDistrictIntent.putExtra(DISTRICT_RECOVERED, clickedItem.getRecovered());


                mContext.startActivity(perDistrictIntent);
            }
        });


        DistrictWiseModel currentItem = arrayList.get(position);
        String districtName = currentItem.getDistrict();
        String districtTotal = currentItem.getConfirmed();
        int districtTotalInt = Integer.parseInt(districtTotal);
        holder.districtTotalCases.setText(NumberFormat.getInstance().format(districtTotalInt));
        holder.districtName.setText(districtName);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void filterList(ArrayList<DistrictWiseModel> filteredList) {
        arrayList = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView districtName, districtTotalCases;

        public ViewHolder(View itemView) {
            super(itemView);
            districtName = itemView.findViewById(R.id.district_layout_name_textview);
            districtTotalCases = itemView.findViewById(R.id.district_layout_confirmed_textview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListner != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListner.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}
