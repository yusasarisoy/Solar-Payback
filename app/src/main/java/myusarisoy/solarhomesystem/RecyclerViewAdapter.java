package myusarisoy.solarhomesystem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<Appliance> appliances;
    private Context context;
    public int isChecked = 0;

    public RecyclerViewAdapter(ArrayList<Appliance> appliances, Context context) {
        this.appliances = appliances;
        this.context = context;
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
        final Appliance appliance = appliances.get(position);

        viewHolder.imageViewAppliance.setBackgroundResource(appliance.getImageResource());
        viewHolder.textView.setText(appliance.getAppliance());
        viewHolder.checkBox.setChecked(appliance.isCheck());
        viewHolder.checkBox.setTag(appliance);

        viewHolder.checkBox.setOnClickListener(view -> {
            CheckBox checkBox = (CheckBox) view;
            Appliance applianceCheckBox = (Appliance) checkBox.getTag();

            applianceCheckBox.setCheck(checkBox.isChecked());
            appliance.setCheck(checkBox.isChecked());

            if (checkBox.isChecked())
                showSnackbar(view, appliance.getAppliance() + " selected.");
            else
                showSnackbar(view, appliance.getAppliance() + " unselected.");
        });
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