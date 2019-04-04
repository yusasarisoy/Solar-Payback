package myusarisoy.solarhomesystem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class RecyclerViewApplianceAdapter extends RecyclerView.Adapter<RecyclerViewApplianceAdapter.ViewHolder> {
    private ArrayList<SelectedAppliance> selectedAppliances;
    private ArrayList<Integer> consumptionList = new ArrayList<>();
    private ArrayList<String> applianceList = new ArrayList<>();
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
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final SelectedAppliance selectedAppliance = selectedAppliances.get(position);

        viewHolder.imageView.setBackgroundResource(selectedAppliance.getImageResource());
        viewHolder.textView.setText(selectedAppliance.getAppliance());

        viewHolder.appliance_quantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_NEXT)) {
                    if (!viewHolder.appliance_quantity.getText().toString().isEmpty() && !viewHolder.appliance_watts.getText().toString().isEmpty() && !viewHolder.appliance_hours.getText().toString().isEmpty()) {
                        int quantity = Integer.parseInt(viewHolder.appliance_quantity.getText().toString());
                        int watts = Integer.parseInt(viewHolder.appliance_watts.getText().toString());
                        int hours = Integer.parseInt(viewHolder.appliance_hours.getText().toString());
                        final int power_consumption = quantity * watts * hours;
                        applianceList.add(selectedAppliance.getAppliance());
                        consumptionList.add(power_consumption);
                    }
                }
                return false;
            }
        });

        viewHolder.appliance_watts.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_NEXT)) {
                    if (!viewHolder.appliance_quantity.getText().toString().isEmpty() && !viewHolder.appliance_watts.getText().toString().isEmpty() && !viewHolder.appliance_hours.getText().toString().isEmpty()) {
                        int quantity = Integer.parseInt(viewHolder.appliance_quantity.getText().toString());
                        int watts = Integer.parseInt(viewHolder.appliance_watts.getText().toString());
                        int hours = Integer.parseInt(viewHolder.appliance_hours.getText().toString());
                        final int power_consumption = quantity * watts * hours;
                        applianceList.add(selectedAppliance.getAppliance());
                        consumptionList.add(power_consumption);
                    }
                }
                return false;
            }
        });

        viewHolder.appliance_hours.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    if (!viewHolder.appliance_quantity.getText().toString().isEmpty() && !viewHolder.appliance_watts.getText().toString().isEmpty() && !viewHolder.appliance_hours.getText().toString().isEmpty()) {
                        int quantity = Integer.parseInt(viewHolder.appliance_quantity.getText().toString());
                        int watts = Integer.parseInt(viewHolder.appliance_watts.getText().toString());
                        int hours = Integer.parseInt(viewHolder.appliance_hours.getText().toString());
                        final int power_consumption = quantity * watts * hours;
                        applianceList.add(selectedAppliance.getAppliance());
                        consumptionList.add(power_consumption);
                    }
                }
                return false;
            }
        });
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

    public ArrayList<Integer> getData() {
        return consumptionList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private ImageView imageView;
        private TextView textView;
        private EditText appliance_watts, appliance_quantity, appliance_hours;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.layout_item_appliance_calculation);
            imageView = itemView.findViewById(R.id.image_view_appliance);
            textView = itemView.findViewById(R.id.item_appliance);
            appliance_quantity = itemView.findViewById(R.id.appliance_quantity);
            appliance_watts = itemView.findViewById(R.id.appliance_watts);
            appliance_hours = itemView.findViewById(R.id.appliance_hours);
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
