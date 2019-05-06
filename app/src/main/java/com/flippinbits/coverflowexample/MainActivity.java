package com.flippinbits.coverflowexample;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final float PREVIEW_PERCENTAGE = 0.2f;
    private static final float GAP_WIDTH_PERCENTAGE = 0.1f;
    private static final float FULL_MAGNIFICATION_FACTOR = 1.15f;
    private static final float SCROLL_WIDTH_PERCENTAGE = 0.85f;

    private ViewPager coverflow;
    private ImagesAdapter imgsAdapter;
    private ImgsPagerTransformer transformer;
    private int[] demoImages = {
            R.mipmap.ic_cover_1,
            R.mipmap.ic_cover_2,
            R.mipmap.ic_cover_3,
            R.mipmap.ic_cover_4,
            R.mipmap.ic_cover_5,
            R.mipmap.ic_cover_6,
            R.mipmap.ic_cover_7,
            R.mipmap.ic_cover_8
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coverflow = findViewById(R.id.view_pager);

        setupCoverFlow();
    }

    private void setupCoverFlow() {
        coverflow.getViewTreeObserver().addOnGlobalLayoutListener(new ImgsLayoutListener());
        imgsAdapter = new ImagesAdapter();
        transformer = new ImgsPagerTransformer(0,40,1.0f,0);
        coverflow.setAdapter(imgsAdapter);
        coverflow.setPageTransformer(false,transformer);
    }


    private class ImagesAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public float getPageWidth(int position) {
            return SCROLL_WIDTH_PERCENTAGE;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            ViewGroup layout = (ViewGroup)inflater.inflate(R.layout.imgs_view_layout, container, false);

            ImageView image = layout.findViewById(R.id.image_view);
            Drawable drawable = getResources().getDrawable(demoImages[position]);
            image.setImageDrawable(drawable);
//            image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
        }
    }

    private class ImgsPagerTransformer implements ViewPager.PageTransformer {
        private int baseElevation;
        private int raisedElevation;
        private float minscale;
        private int magOffset;

        ImgsPagerTransformer(int baseElevation, int raisedElevation, float minscale, int magOffset) {
            this.baseElevation = baseElevation;
            this.raisedElevation = raisedElevation;
            this.minscale = minscale;
            this.magOffset = magOffset;
        }

        @Override
        public void transformPage(@NonNull View view, float position) {
            float absPosition = Math.abs(position - magOffset);
            if (absPosition >= 1) {
                view.setElevation(baseElevation);
                view.setScaleX(minscale);
                view.setScaleY(minscale);
            } else {
                view.setElevation((1 - absPosition) * raisedElevation + baseElevation);
                float scalingFactor = (minscale - FULL_MAGNIFICATION_FACTOR) * absPosition + FULL_MAGNIFICATION_FACTOR;
                view.animate().scaleY(scalingFactor).scaleX(scalingFactor).setDuration(0).start();
            }
        }
    }

    private class ImgsLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            float density = getResources().getDisplayMetrics().density;
            float width = coverflow.getWidth() / density;
            int partialwidth = (int)(PREVIEW_PERCENTAGE * width * density);
            int pageMargin = (int)(GAP_WIDTH_PERCENTAGE * width);
            int viewPagerPadding = partialwidth + pageMargin;

            coverflow.setPageMargin(pageMargin);
            coverflow.setPadding(viewPagerPadding,0,viewPagerPadding,0);
        }
    }
}
