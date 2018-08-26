package com.third.zoom.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.third.zoom.common.listener.MarqueeCompletedListener;

import java.util.ArrayList;
import java.util.List;

public class MarqueeTextViewV2 extends TextView {

	public final static String TAG = MarqueeTextViewV2.class.getSimpleName();

	/**
	 * 绘图样式
	 */
	private Paint mTextPaint;
	/**
	 * 文本长度
	 */
	private float mTextLength = 0f;
	/**
	 * view宽
	 */
	private float mWidth = 0f;
	/**
	 * view高
	 */
	private float mHeigh = 0f;
	/**
	 * 滚动速度
	 */
	private float mSrollSpeed = 5f;
	/**
	 * 滚动距离
	 */
	private float mSrollLag = 0f;
	/**
	 * 是否开始滚动
	 */
	public boolean isStarting = false;
	/**
	 * 文本内容
	 */
	private String mContent = "";
	/**
	 * 字体大小
	 */
	private float mTextSize = 60;
	/**
	 * 字体颜色
	 */
	private String mTextColor = "#ffffff";
	/**
	 * 切割长度
	 */
	private float mSplitLength = 0;
	/**
	 * 分行保存显示信息
	 */
	private List<String> mContentList = new ArrayList<String>();
	/**
	 * 放置位置
	 */
	private int mLayoutWidth = 0, mLayoutHeight = 0, mLayoutX = 0,
			mLayoutY = 0;
	/**
	 * 滚动模式
	 */
	private ScrollType SCROLL_TYPE = ScrollType.RIGHT_LEFT;
	

	private float mMinTextSize = 0.1f;
	private int mShowWidth = 0;
	private int mShowHeight = 0;

	/**
	 * @描述:滚动类型
	 */
	public enum ScrollType {
		NOT_SCROLL, RIGHT_LEFT, LEFT_RIGHT, UP_DOWN, DOWN_UP,
	}

	public MarqueeTextViewV2(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
		initView();
	}

	public MarqueeTextViewV2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MarqueeTextViewV2(Context context) {
		this(context, null);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		mWidth = right - left;
		mHeigh = bottom - top;
		super.onLayout(changed, left, top, right, bottom);
	}

	private void initView() {
		mTextPaint = getPaint();
	}


	private MarqueeCompletedListener listner;

	public void setMarqueeCompletedListener(MarqueeCompletedListener listener) {
		this.listner = listener;
	}

	/**
	 * 
	 * @描述:设置字体颜色
	 * @param textColor
	 */
	public void setContentColor(String textColor) {
		this.mTextColor = textColor;
	}

	/**
	 * 
	 * @描述:设置背景
	 * @param backgroundColor
	 */
	public void setBackground(String backgroundColor) {
		this.setBackgroundColor(Color.parseColor(backgroundColor));
	}

	/**
	 * 
	 * @描述: 设置文本内容
	 * @param content
	 */
	public void setContent(final String content) {
		this.mContent = content;
		post(new Runnable() {

			@Override
			public void run() {
				init();
				isStarting = true;
				setText(content);
				invalidate();
			}
		});
	}

	/**
	 * 
	 * @描述:设置layoutOn
	 * @param width
	 * @param height
	 * @param x
	 * @param y
	 */
	public void setTextLayout(int width, int height, int x, int y) {
		this.mLayoutWidth = width;
		this.mLayoutHeight = height;
		this.mLayoutX = x;
		this.mLayoutY = y;
	}

	public void init() {
		mSrollLag = 0f;
		float resizeText = 40;
		setTextSize(resizeText);
		mTextPaint.setColor(Color.parseColor(mTextColor));
		mTextLength = mTextPaint.measureText(mContent);
//		mTextSize = mTextPaint.getTextSize();
	}

	public void play() {
		init();
		isStarting = true;
		postInvalidate();
	}

	public void release() {
		isStarting = false;
	}

	public void pause() {
		isStarting = false;
	}

	public void resume() {
		isStarting = true;
		invalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {
		right2Left(canvas);
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @描述:向左滚动
	 */
	private void right2Left(Canvas canvas) {
		canvas.drawText(mContent, mWidth - mSrollLag,
				(mHeigh + mTextSize / 2) / 2 + 5, mTextPaint);
		if (!isStarting) {
			return;
		}
		mSrollLag += mSrollSpeed;
		if (mSrollLag > mWidth + mTextLength) {
			mSrollLag = 0;
			if (null != listner) {
				System.out.println("listner.onCompleted(this)");
				listner.onCompleted(this);
			}
			return;
		}
		invalidate();
	}


	public void setShowWidth(int width) {
		mShowWidth = width;
	}

	public void setShowHeight(int height) {
		mShowHeight = height;
	}

	public float resizeText() {
		int width = mShowWidth;
		int height = mShowHeight;
		TextPaint textPaint = getPaint();
		float targetTextSize = height;
		int textHeight = getTextHeight(textPaint, targetTextSize);
		while ((textHeight >= height * 0.8) && targetTextSize > mMinTextSize) {
			targetTextSize = Math.max(targetTextSize - 1, mMinTextSize);
			textHeight = getTextHeight(textPaint, targetTextSize);
		}
		textPaint.setTextSize(targetTextSize);
		float size = textPaint.getTextSize();

		return size;
	}

	private int getTextHeight(TextPaint originalPaint, float textSize) {
		TextPaint paint = new TextPaint(originalPaint);
		paint.setTextSize(textSize);
		FontMetrics fontMetrics = paint.getFontMetrics();

		return (int) Math.abs(fontMetrics.top - fontMetrics.bottom);
	}

	// /**
	// *
	// * @描述:向右滚动
	// */
	// private void left2Right(Canvas canvas) {
	// canvas.drawText(mContent, mSrollLag - mTextLength, (mHeigh + mTextSize /
	// 2) / 2, mTextPaint);
	// if (!isStarting) { return; }
	// mSrollLag += mSrollSpeed;
	// if (mSrollLag > mTextLength + mWidth) {
	// mSrollLag = 0;
	// }
	// invalidate();
	// }

}
