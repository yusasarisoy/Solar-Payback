package myusarisoy.solarhomesystem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class InstructionsAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public InstructionsAdapter(Context context) {
        this.context = context;
    }

    public int[] images = {
            R.drawable.solar_house,
            R.drawable.main_page,
            R.drawable.sun
    };

    public String[] texts = {
            "Solar Payback is a mobile application that provides to solar energy calculation for your billing amounts or your home appliances.",
            "You can save your monthly billing amount or select appliances in your home from the list and their amounts, and usage durations during the day.",
            "If you are ready, we can start!"
    };

    @Override
    public int getCount() {
        return texts.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.page_instructions, container, false);

        ImageView imageView = view.findViewById(R.id.image_view);
        TextView textView = view.findViewById(R.id.text_view);

        imageView.setImageResource(images[position]);
        textView.setText(texts[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
