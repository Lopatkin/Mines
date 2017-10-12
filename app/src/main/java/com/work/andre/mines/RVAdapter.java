package com.work.andre.mines;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.BuildingViewHolder> {

    public static class BuildingViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView buildingName;
        TextView buildingLVL;
        ImageView buildingPhoto;

        BuildingViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            buildingName = (TextView) itemView.findViewById(R.id.building_name);
            buildingLVL = (TextView) itemView.findViewById(R.id.building_LVL);
            buildingPhoto = (ImageView) itemView.findViewById(R.id.building_photo);
        }
    }

    List<Buildings> buildings;

    RVAdapter(List<Buildings> buildings) {
        this.buildings = buildings;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public BuildingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        BuildingViewHolder pvh = new BuildingViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(BuildingViewHolder buildingViewHolder, int i) {
        buildingViewHolder.buildingName.setText(buildings.get(i).buildingName);
        buildingViewHolder.buildingLVL.setText(buildings.get(i).buildingLVL + " уровень");
        buildingViewHolder.buildingPhoto.setImageResource(buildings.get(i).photoId);
    }

    @Override
    public int getItemCount() {
        return buildings.size();
    }
}
