package shbd.progressbar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import shbd.progressbar.activity.R;

/**
 * Created by yh on 2016/6/20.
 * 水平的带进度的进度条
 */
public class HorizontalProgressBarWidthProgress extends ProgressBar {
    //默认值
    private static final int DEFAULT_UNREACH_HEIGHT = 2;
    private static final int DEFAULT_UNREACH_COLOR = 0XFFFFFF;
    private static final int DEFAULT_REACH_HEIGHT = 4;
    private static final int DEFAULT_REACH_COLOR = 0XFFFFFF;
    private static final int DEFAULT_TEXT_SIZE = 2;
    private static final int DEFAULT_TEXT_COLOR = 0XFFFFFF;
    private static final int DEFAULT_TEXT_OFFSET = 2;

    private int unReachHeight = pxToDp(DEFAULT_UNREACH_HEIGHT);
    private int unReachColor = DEFAULT_UNREACH_COLOR;
    private int reachHeight = pxToDp(DEFAULT_REACH_HEIGHT);
    private int reachColor = DEFAULT_REACH_COLOR;
    private int textSize = pxToSp(DEFAULT_TEXT_SIZE);
    private int textColor = DEFAULT_TEXT_COLOR;
    private int textOffset = pxToDp(DEFAULT_TEXT_OFFSET);

    private Paint mPaint;

    //控件的真实宽度
    private int mRealWidth;

    public HorizontalProgressBarWidthProgress(Context context) {
        this(context, null);
    }

    public HorizontalProgressBarWidthProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressBarWidthProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = measureHeight(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    /***
     * 测量实际高度
     *
     * @param heightMeasureSpec
     * @return
     */
    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //若给定的模式为精确模式，则直接返回指定的值
        if (heightMode == MeasureSpec.EXACTLY) {
            result = heightSize;
        } else {
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            result = getPaddingTop() + getPaddingBottom() + Math.max(Math.abs(textHeight), Math.max(unReachHeight, reachHeight));

            if (heightMode == MeasureSpec.AT_MOST) {
                result = Math.min(heightSize, result);
            }
        }
        return result;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2);

        /**
         *绘制进度条左侧
         */
        float radio = getProgress() * 1.0f / getMax();
        int endX = (int) (radio * mRealWidth - textOffset);
        int progressX = (int) (radio * mRealWidth);

        String text = getProgress() + "%";
        int textWidth = (int) mPaint.measureText(text);

        boolean noNeedUnreach = false;
        if (progressX + textWidth > mRealWidth) {
            progressX = mRealWidth - textWidth;
            noNeedUnreach = true;
        }

        if (endX > 0) {
            mPaint.setColor(unReachColor);
            mPaint.setStrokeWidth(reachHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }
        /**
         * 绘制文本
         */
        mPaint.setColor(textColor);
        int y = (int) (-(mPaint.descent() + mPaint.descent()) / 2);
        canvas.drawText(text, progressX, y, mPaint);
        /**
         * 绘制进度条右侧
         */
        if (!noNeedUnreach) {
            mPaint.setColor(reachColor);
            mPaint.setStrokeWidth(reachHeight);
            canvas.drawLine(progressX + textWidth + textOffset, 0, mRealWidth, 0, mPaint);
        }
        canvas.restore();
    }

    /**
     * 初始化值
     */
    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBarWidthProgress);

        unReachHeight = (int) typedArray.getDimension(R.styleable.HorizontalProgressBarWidthProgress_progress_unreach_height, DEFAULT_UNREACH_HEIGHT);
        unReachColor = typedArray.getColor(R.styleable.HorizontalProgressBarWidthProgress_progress_unreach_color, DEFAULT_UNREACH_COLOR);

        reachHeight = (int) typedArray.getDimension(R.styleable.HorizontalProgressBarWidthProgress_progress_reach_height, DEFAULT_REACH_HEIGHT);
        reachColor = typedArray.getColor(R.styleable.HorizontalProgressBarWidthProgress_progress_reach_color, DEFAULT_REACH_COLOR);

        textSize = (int) typedArray.getDimension(R.styleable.HorizontalProgressBarWidthProgress_progress_text_size, DEFAULT_TEXT_SIZE);
        textColor = typedArray.getColor(R.styleable.HorizontalProgressBarWidthProgress_progress_text_color, DEFAULT_TEXT_COLOR);
        textOffset = (int) typedArray.getDimension(R.styleable.HorizontalProgressBarWidthProgress_progress_text_offset, DEFAULT_TEXT_OFFSET);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setTextSize(textSize);
    }

    /**
     * px转dp
     *
     * @param value
     * @return
     */
    public int pxToDp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    /**
     * px转sp
     *
     * @param value
     * @return
     */
    public int pxToSp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }
}
