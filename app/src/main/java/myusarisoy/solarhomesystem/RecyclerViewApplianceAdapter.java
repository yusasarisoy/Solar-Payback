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
public class RecyclerViewApplianceAdapter extends RecyclerView.Adapter<RecyclerViewApplianceAdapter.ViewHolder> {
    private ArrayList<SelectedAppliance> selectedAppliances;
    private Context context;

    public RecyclerViewApplianceAdapter(ArrayList<SelectedAppliance> selectedAppliances, Context context) {
        this.selectedAppliances = selectedAppliances;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_appliance_calculation, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final SelectedAppliance selectedAppliance = selectedAppliances.get(position);

        viewHolder.imageView.setBackgroundResource(selectedAppliance.getImageResource());
        viewHolder.textView.setText(selectedAppliance.getAppliance());
    }

    @Override
    public int getItemCount() {
        return selectedAppliances.size();
    }

//    public void removeItem(int position) {
//        appliances.remove(position);
//        notifyItemRemoved(position);
//    }

//    public void restoreItem(Appliance item, int position) {
//        appliances.add(position, item);
//        notifyItemInserted(position);
//    }

    public ArrayList<SelectedAppliance> getData() {
        return selectedAppliances;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private ImageView imageView;
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.layout_item_appliance_calculation);
            imageView = itemView.findViewById(R.id.image_view_appliance);
            textView = itemView.findViewById(R.id.item_appliance);
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
