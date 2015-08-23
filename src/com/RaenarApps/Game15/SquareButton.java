package com.RaenarApps.Game15;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class SquareButton extends Button {

	public SquareButton(Context context) {
		super(context);
	}

	public SquareButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// This is used to make square buttons.
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec-3, widthMeasureSpec-3); //Since i've added a margin to all tiles
	}
}