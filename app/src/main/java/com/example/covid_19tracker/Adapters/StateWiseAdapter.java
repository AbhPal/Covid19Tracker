package com.example.covid_19tracker.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covid_19tracker.EachStateDataActivity;
import com.example.covid_19tracker.Models.StateWiseModel;
import com.example.covid_19tracker.R;

import java.util.ArrayList;

public class StateWiseAdapter extends RecyclerView.Adapter<StateWiseAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<StateWiseModel> arrayList;
    private OnItemClickListner mListner;
    private static final String STATE_NAME = "stateName";
    private static final String STATE_CONFIRMED = "stateConfirmed";
    private static final String STATE_ACTIVE = "stateActive";
    private static final String STATE_DECEASED = "stateDeaceased";
    private static final String STATE_NEW_CONFIRMED = "stateNewConfirmed";
    private static final String STATE_NEW_RECOVERED = "stateNewRecovered";
    private static final String STATE_NEW_DECEASED = "stateNewDeceased";
    private static final String STATE_LAST_UPDATE = "stateLastUpdate";
    private static final String STATE_RECOVERED = "stateRecovered";

    public interface OnItemClickListner {
        void onItemClick(int position);
    }

    public void setOnItemClickListner(OnItemClickListner listner) {
        mListner = listner;
    }

    public StateWiseAdapter(Context context, ArrayList<StateWiseModel> statewiseModelArrayList) {
        mContext = context;
        arrayList = statewiseModelArrayList;
    }

    @NonNull
    @Override
    public StateWiseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.statewise_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StateWiseAdapter.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StateWiseModel clickedItem = arrayList.get(position);
                Intent perStateIntent = new Intent(mContext, EachStateDataActivity.class);

                perStateIntent.putExtra(STATE_NAME, clickedItem.getState());
                perStateIntent.putExtra(STATE_CONFIRMED, clickedItem.getConfirmed());
                perStateIntent.putExtra(STATE_ACTIVE, clickedItem.getActive());
                perStateIntent.putExtra(STATE_DECEASED, clickedItem.getDeceased());
                perStateIntent.putExtra(STATE_NEW_CONFIRMED, clickedItem.getNewConfirmed());
                perStateIntent.putExtra(STATE_NEW_RECOVERED, clickedItem.getNewRecovered());
                perStateIntent.putExtra(STATE_NEW_DECEASED, clickedItem.getNewDeceased());
                perStateIntent.putExtra(STATE_LAST_UPDATE, clickedItem.getLastupdate());
                perStateIntent.putExtra(STATE_RECOVERED, clickedItem.getRecovered());


                mContext.startActivity(perStateIntent);
            }
        });


        StateWiseModel currentItem = arrayList.get(position);
        String stateName = currentItem.getState();
        String stateTotal = currentItem.getConfirmed();
        holder.stateTotalCases.setText(stateTotal);
        holder.stateName.setText(stateName);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void filterList(ArrayList<StateWiseModel> filteredList) {
        arrayList = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView stateName, stateTotalCases;

        public ViewHolder(View itemView) {
            super(itemView);

            stateName = itemView.findViewById(R.id.statewise_layout_name_textview);
            stateTotalCases = itemView.findViewById(R.id.statewise_layout_confirmed_textview);

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
