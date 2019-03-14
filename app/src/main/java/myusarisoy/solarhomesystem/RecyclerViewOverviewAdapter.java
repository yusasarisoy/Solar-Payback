package myusarisoy.solarhomesystem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class RecyclerViewOverviewAdapter extends RecyclerView.Adapter<RecyclerViewOverviewAdapter.ViewHolder> {
    private ArrayList<ApplianceOverview> applianceOverviews;
    private Context context;

    public RecyclerViewOverviewAdapter(ArrayList<ApplianceOverview> applianceOverviews, Context context) {
        this.applianceOverviews = applianceOverviews;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewOverviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_overview, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        return new RecyclerViewOverviewAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewOverviewAdapter.ViewHolder viewHolder, int position) {
        final ApplianceOverview applianceOverview = applianceOverviews.get(position);

        viewHolder.imageView.setBackgroundResource(applianceOverview.getImageResource());
        viewHolder.textViewAppliance.setText(applianceOverview.getAppliance());
        viewHolder.textViewPowerConsumption.setText(applianceOverview.getPowerConsumption()+" kWh");
        viewHolder.textViewPowerConsumption.setTextColor(getContext().getResources().getColor(R.color.red));
    }

    @Override
    public int getItemCount() {
        return applianceOverviews.size();
    }

//    public void removeItem(int position) {
//        appliances.remove(position);
//        notifyItemRemoved(position);
//    }

//    public void restoreItem(Appliance item, int position) {
//        appliances.add(position, item);
//        notifyItemInserted(position);
//    }

    public ArrayList<ApplianceOverview> getData() {
        return applianceOverviews;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private ImageView imageView;
        private TextView textViewAppliance, textViewPowerConsumption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.layout_item_appliance_power_consumption);
            imageView = itemView.findViewById(R.id.image_view_appliance);
            textViewAppliance = itemView.findViewById(R.id.item_appliance);
            textViewPowerConsumption = itemView.findViewById(R.id.item_power_consumption);
        }
    }

    private void showSnackbar(View view, String text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getContext().getResources().getColor(R.color.dark_slate_gray));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
}