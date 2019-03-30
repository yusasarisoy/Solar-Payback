package myusarisoy.solarhomesystem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class RecyclerViewOverviewAppliancesAdapter extends RecyclerView.Adapter<RecyclerViewOverviewAppliancesAdapter.ViewHolder> {
    private ArrayList<ApplianceOverviewItem> applianceOverviewItems;
    private Context context;

    public RecyclerViewOverviewAppliancesAdapter(ArrayList<ApplianceOverviewItem> applianceOverviewItems, Context context) {
        this.applianceOverviewItems = applianceOverviewItems;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewOverviewAppliancesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_overview_appliances, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        return new RecyclerViewOverviewAppliancesAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewOverviewAppliancesAdapter.ViewHolder viewHolder, int position) {
        final ApplianceOverviewItem applianceOverviewItem = applianceOverviewItems.get(position);

        viewHolder.image.setImageResource(applianceOverviewItem.getImage());
        viewHolder.name.setText(applianceOverviewItem.getName());
        viewHolder.powerConsumption.setText(applianceOverviewItem.getPowerConsumption() + " kWh");
    }

    @Override
    public int getItemCount() {
        return applianceOverviewItems.size();
    }

    public ArrayList<ApplianceOverviewItem> getData() {
        return applianceOverviewItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name, powerConsumption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            powerConsumption = itemView.findViewById(R.id.item_power_consumption);
        }
    }
}
