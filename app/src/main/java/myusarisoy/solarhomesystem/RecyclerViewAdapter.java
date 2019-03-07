package myusarisoy.solarhomesystem;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<Appliance> appliances;
    private boolean isChecked = false;

    public RecyclerViewAdapter(ArrayList<Appliance> appliances) {
        this.appliances = appliances;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_appliance, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Appliance appliance = appliances.get(position);

        viewHolder.imageViewAppliance.setBackgroundResource(appliance.getImageResource());
        viewHolder.textView.setText(appliance.getAppliance());
    }

    @Override
    public int getItemCount() {
        return appliances.size();
    }

//    public void removeItem(int position) {
//        appliances.remove(position);
//        notifyItemRemoved(position);
//    }

//    public void restoreItem(Appliance item, int position) {
//        appliances.add(position, item);
//        notifyItemInserted(position);
//    }

    public ArrayList<Appliance> getData() {
        return appliances;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout item_add_appliance;
        private CheckBox checkBox;
        private ImageView imageViewAppliance;
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item_add_appliance = itemView.findViewById(R.id.item_add_appliance);
            checkBox = itemView.findViewById(R.id.checkbox);
            imageViewAppliance = itemView.findViewById(R.id.image_view_appliance);
            textView = itemView.findViewById(R.id.item_appliance);

            item_add_appliance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isChecked) {
                        isChecked = true;
                    } else {
                        isChecked = false;
                    }
                }
            });
        }
    }
}