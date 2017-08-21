package com.james.cleaner.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.james.cleaner.R;
import com.james.cleaner.utils.ImageUtils;

public class AppIconView extends View {

    private Bitmap fgBitmap;
    private Paint paint;
    private int size;
    private float rotation = 90;
    private float fgScale = 0, bgScale = 0;

    private Path path;
    private ValueAnimator animator;

    public AppIconView(Context context) {
        this(context, null);
    }

    public AppIconView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

        ValueAnimator animator = ValueAnimator.ofFloat(bgScale, 0.8f);
        animator.setInterpolator(new OvershootInterpolator());
        animator.setDuration(2000);
        animator.setStartDelay(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                bgScale = (float) animator.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();

        animator = ValueAnimator.ofFloat(rotation, 360);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(2000);
        animator.setStartDelay(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                fgScale = animator.getAnimatedFraction() * 0.8f;
                rotation = (float) animator.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (animator != null && animator.isStarted())
                    animator.cancel();

                animator = ValueAnimator.ofFloat(fgScale, 1);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        fgScale = (float) valueAnimator.getAnimatedValue();
                        bgScale = ((fgScale - 0.8f) / 2) + 0.8f;
                        invalidate();
                    }
                });
                animator.start();
                break;
            case MotionEvent.ACTION_UP:
                if (animator != null && animator.isStarted())
                    animator.cancel();

                animator = ValueAnimator.ofFloat(fgScale, 0.8f);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        fgScale = (float) valueAnimator.getAnimatedValue();
                        bgScale = ((fgScale - 0.8f) / 2) + 0.8f;
                        invalidate();
                    }
                });
                animator.start();
                break;
        }
        return true;
    }

    private Bitmap getRoundBitmap(@DrawableRes int drawable, int size) {
        Bitmap bitmap = ImageUtils.drawableToBitmap(ContextCompat.getDrawable(getContext(), drawable));
        bitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 6, bitmap.getHeight() / 6, (int) (0.666 * bitmap.getWidth()), (int) (0.666 * bitmap.getHeight()));
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, size, size);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);

        roundedBitmapDrawable.setCornerRadius(size / 2);
        roundedBitmapDrawable.setAntiAlias(true);

        return ImageUtils.drawableToBitmap(roundedBitmapDrawable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int size = Math.min(canvas.getWidth(), canvas.getHeight());
        if (this.size != size || fgBitmap == null || path == null) {
            this.size = size;
            fgBitmap = getRoundBitmap(R.mipmap.ic_launcher_foreground, size);
            path = new Path();
            path.arcTo(new RectF(0, 0, size, size), 0, 359);
            path.close();
        }

        Matrix matrix = new Matrix();
        matrix.postScale(bgScale, bgScale, size / 2, size / 2);

        Path path = new Path();
        this.path.transform(matrix, path);
        canvas.drawPath(path, paint);

        matrix = new Matrix();
        matrix.postTranslate(-fgBitmap.getWidth() / 2, -fgBitmap.getHeight() / 2);
        matrix.postScale(fgScale, fgScale);
        matrix.postRotate(rotation);
        matrix.postTranslate(fgBitmap.getWidth() / 2, fgBitmap.getHeight() / 2);
        canvas.drawBitmap(fgBitmap, matrix, paint);
    }

}
