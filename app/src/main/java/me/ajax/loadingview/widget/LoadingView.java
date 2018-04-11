package me.ajax.loadingview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by aj on 2018/4/2
 */

public class LoadingView extends View {

    Paint linePaint = new Paint();

    RectF outRectF = new RectF(-dp2Dx(60), dp2Dx(-60), dp2Dx(60), dp2Dx(60));
    RectF inRectF = new RectF(-dp2Dx(35), dp2Dx(-35), dp2Dx(35), dp2Dx(35));

    int animationRepeatCount;
    ValueAnimator animator;

    boolean isStart = false;

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速

        //画笔
        linePaint.setColor(0xFFFF00FF);
        linePaint.setStrokeWidth(dp2Dx(2));
        linePaint.setTextSize(dp2Dx(14));
        linePaint.setStyle(Paint.Style.STROKE);

        initAnimator();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animator.start();
                isStart = true;
            }
        });
    }

    void initAnimator() {

        animator = ValueAnimator.ofInt(0, 360);
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                animationRepeatCount++;
            }
        });
        animator.setRepeatCount(Integer.MAX_VALUE - 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidateView();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int mWidth = getWidth();
        int mHeight = getHeight();

        canvas.save();
        canvas.translate(mWidth / 2, mHeight / 2);


        int animationValue = !isStart ? 90 : (int) animator.getAnimatedValue();
        if (animationRepeatCount % 2 == 0) {
            animationValue *= 2;
            linePaint.setAlpha(255 - (int) (animationValue / 720F * 255F));
            canvas.drawArc(outRectF, animationValue, 360 - animationValue / 2, false, linePaint);
            linePaint.setAlpha((int) (animationValue / 720F * 255F));
            canvas.drawArc(inRectF, -animationValue, -animationValue / 2, false, linePaint);
        } else {
            linePaint.setAlpha((int) (animationValue / 360F * 255F));
            canvas.drawArc(outRectF, animationValue / 2, animationValue, false, linePaint);
            linePaint.setAlpha(255 - (int) (animationValue / 360F * 255F));
            canvas.drawArc(inRectF, -animationValue / 2, -(360 - animationValue), false, linePaint);
        }

        canvas.restore();
    }


    int dp2Dx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    void l(Object o) {
        Log.e("######", o.toString());
    }


    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            //  当前线程是主UI线程，直接刷新。
            invalidate();
        } else {
            //  当前线程是非UI线程，post刷新。
            postInvalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimAndRemoveCallbacks();
    }

    private void stopAnimAndRemoveCallbacks() {

        if (animator != null) animator.end();

        Handler handler = this.getHandler();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
