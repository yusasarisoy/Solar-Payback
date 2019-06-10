package myusarisoy.solarhomesystem.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import myusarisoy.solarhomesystem.R;

public class InstructionsAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public InstructionsAdapter(Context context) {
        this.context = context;
    }

    public int[] images = {R.drawable.solar_house, R.drawable.main_page, R.drawable.sun};

    public int[] texts = {R.string.intro1, R.string.intro2, R.string.intro3};

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

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView);

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