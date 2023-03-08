package com.example.emergencywatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class CustomImageView extends ImageView {

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas.isHardwareAccelerated()) {
            // Draw the image directly onto the canvas
            Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
            canvas.drawBitmap(bitmap, 0, 0, null);
        } else {
            // Let the ImageView handle the drawing as usual
            super.onDraw(canvas);
        }
    }
}
