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
public class RecyclerViewEnergySaverTipsAdapter extends RecyclerView.Adapter<RecyclerViewEnergySaverTipsAdapter.ViewHolder> {
    private ArrayList<ApplianceEnergySaverTips> applianceEnergySaverTips;
    private Context context;

    public RecyclerViewEnergySaverTipsAdapter(ArrayList<ApplianceEnergySaverTips> applianceEnergySaverTips, Context context) {
        this.applianceEnergySaverTips = applianceEnergySaverTips;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewEnergySaverTipsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_power_consumption, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        return new RecyclerViewEnergySaverTipsAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewEnergySaverTipsAdapter.ViewHolder viewHolder, int position) {
        final ApplianceEnergySaverTips energySaverTips = applianceEnergySaverTips.get(position);

        viewHolder.imageView.setBackgroundResource(energySaverTips.getImageViewBackground());
        viewHolder.textViewTitle.setText(energySaverTips.getApplianceTitle());
        viewHolder.textViewText.setText(energySaverTips.getApplianceText());
        viewHolder.textViewTitle.setTextColor(energySaverTips.getColor());
        viewHolder.textViewText.setTextColor(energySaverTips.getColor());
    }

    @Override
    public int getItemCount() {
        return applianceEnergySaverTips.size();
    }

//    public void removeItem(int position) {
//        appliances.remove(position);
//        notifyItemRemoved(position);
//    }

//    public void restoreItem(Appliance item, int position) {
//        appliances.add(position, item);
//        notifyItemInserted(position);
//    }

    public ArrayList<ApplianceEnergySaverTips> getData() {
        return applianceEnergySaverTips;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textViewTitle, textViewText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
            textViewTitle = itemView.findViewById(R.id.title);
            textViewText = itemView.findViewById(R.id.text);
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