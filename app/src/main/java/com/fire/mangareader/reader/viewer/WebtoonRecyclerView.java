package com.fire.mangareader.reader.viewer;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class WebtoonRecyclerView extends RecyclerView {

    private float scale = 1.0f;
    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;
    
    private float pivotX = 0f;
    private float pivotY = 0f;

    public WebtoonRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public WebtoonRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        super.onTouchEvent(e);
        scaleDetector.onTouchEvent(e);
        gestureDetector.onTouchEvent(e);
        return true;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            scale *= scaleFactor;
            
            // حدود التقريب (من 1x إلى 3x)
            scale = Math.max(1.0f, Math.min(scale, 3.0f));

            setScaleX(scale);
            setScaleY(scale);
            
            if (scale == 1.0f) {
                setTranslationX(0f);
                setTranslationY(0f);
            }
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            // إعادة الصورة لحجمها الطبيعي إذا كان التقريب صغيراً جداً
            if (scale < 1.0f) {
                animateReset();
            }
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (scale > 1.0f) {
                // تفعيل التجول داخل الصورة عند التقريب
                setTranslationX(getTranslationX() - distanceX);
                setTranslationY(getTranslationY() - distanceY);
                return true;
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (scale > 1.0f) {
                animateReset();
            } else {
                // تقريب سريع عند النقر المزدوج
                animateZoom(2.0f);
            }
            return true;
        }
    }

    private void animateReset() {
        ValueAnimator animScale = ValueAnimator.ofFloat(scale, 1.0f);
        animScale.addUpdateListener(animation -> {
            scale = (float) animation.getAnimatedValue();
            setScaleX(scale);
            setScaleY(scale);
        });

        ValueAnimator animTransX = ValueAnimator.ofFloat(getTranslationX(), 0f);
        animTransX.addUpdateListener(animation -> setTranslationX((float) animation.getAnimatedValue()));

        ValueAnimator animTransY = ValueAnimator.ofFloat(getTranslationY(), 0f);
        animTransY.addUpdateListener(animation -> setTranslationY((float) animation.getAnimatedValue()));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animScale, animTransX, animTransY);
        set.setDuration(200);
        set.setInterpolator(new DecelerateInterpolator());
        set.start();
    }

    private void animateZoom(float targetScale) {
        ValueAnimator animScale = ValueAnimator.ofFloat(scale, targetScale);
        animScale.addUpdateListener(animation -> {
            scale = (float) animation.getAnimatedValue();
            setScaleX(scale);
            setScaleY(scale);
        });
        animScale.setDuration(200);
        animScale.setInterpolator(new DecelerateInterpolator());
        animScale.start();
    }
}
