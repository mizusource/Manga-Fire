package com.fire.mangareader.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class DonutChartView extends View {

    private Paint paint;
    private RectF rectF;
    private float[] data = {0, 0, 0, 0, 0};
    private int[] colors = {
            Color.parseColor("#5A9CC4"), // Blue - Favorites
            Color.parseColor("#44A85F"), // Green - Reading
            Color.parseColor("#C33B32"), // Red - Plan
            Color.parseColor("#6A3CC4"), // Purple - Completed
            Color.parseColor("#8E24AA")  // Dark Purple - Dropped
    };

    public DonutChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(60f); // Thickness of the donut
        rectF = new RectF();
    }

    public void setData(float fav, float reading, float plan, float completed, float dropped) {
        data[0] = fav;
        data[1] = reading;
        data[2] = plan;
        data[3] = completed;
        data[4] = dropped;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height) - 80;

        int left = (width - size) / 2;
        int top = (height - size) / 2;

        rectF.set(left, top, left + size, top + size);

        float total = 0;
        for (float v : data) total += v;

        if (total == 0) {
            paint.setColor(Color.DKGRAY);
            canvas.drawArc(rectF, 0, 360, false, paint);
            return;
        }

        float startAngle = -90;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > 0) {
                float sweepAngle = (data[i] / total) * 360f;
                paint.setColor(colors[i]);
                canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);
                startAngle += sweepAngle;
            }
        }
    }
}
