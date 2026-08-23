package com.fire.mangareader.reader.viewer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

public class ReaderColorFilterView extends View {
    private final Paint paint;

    public ReaderColorFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    // هذه الدالة لتفعيل الفلتر وتحديد لونه ونوعه (دمج، تظليل، تفتيح)
    public void setFilter(int color, int modeIndex) {
        paint.setColor(color);
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
        if (modeIndex == 1) mode = PorterDuff.Mode.DARKEN;
        else if (modeIndex == 2) mode = PorterDuff.Mode.LIGHTEN;
        else if (modeIndex == 3) mode = PorterDuff.Mode.OVERLAY;
        else if (modeIndex == 4) mode = PorterDuff.Mode.SCREEN;
        else if (modeIndex == 5) mode = PorterDuff.Mode.MULTIPLY;
        
        paint.setXfermode(new PorterDuffXfermode(mode));
        invalidate(); // إعادة رسم الشاشة
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPaint(paint);
    }
}
