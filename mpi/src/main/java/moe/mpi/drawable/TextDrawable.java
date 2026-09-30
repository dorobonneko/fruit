package moe.mpi.drawable;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.List;
import android.graphics.PixelFormat;
import android.graphics.ColorFilter;
import android.view.View;
import android.text.*;

public class TextDrawable extends Drawable {
    private final Paint mTextPaint;
    private String mText;
    private List<String> mWrappedLines = new ArrayList<>();
    private float mLineHeight;

    public TextDrawable(String text) {
		mText=text;
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(24);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mLineHeight = mTextPaint.getFontSpacing();
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds.width() == 0 || mWrappedLines.isEmpty()) return;
        float startY = bounds.top;
        for (int i = 0; i < mWrappedLines.size(); i++) {
            String lineText = mWrappedLines.get(i);
            float baselineY = startY + mLineHeight * i + mLineHeight * 0.8f;
			if(baselineY>bounds.bottom)break;
            canvas.drawText(lineText, bounds.left, baselineY, mTextPaint);
        }
    }
	
    private void calculateWrappedLines(int maxWidth) {
		mWrappedLines.clear();
		if (maxWidth <= 0 || mText.isEmpty()) return;
		for(String str:mText.split("\n")){
			if(TextUtils.isEmpty(str))
				continue;
			int startIndex = 0,count;
			int strLength = str.length();
			do {
				try{
				count = mTextPaint.breakText(str, startIndex, strLength, true, maxWidth, null);
				}catch(Exception e){
					count=0;
				}
				if(count==0)break;
				int end = startIndex + count;
				mWrappedLines.add(str.substring(startIndex, end));
				startIndex = end;
			} while (startIndex+1 < strLength);
			
		}
	}
	
    public void setTextSize(float textSize) {
        mTextPaint.setTextSize(textSize);
        mLineHeight = mTextPaint.getFontSpacing();
        calculateWrappedLines(getBounds().width());
        invalidateSelf();
    }


    public void setText(String text) {
        this.mText = text;
        calculateWrappedLines(getBounds().width());
        invalidateSelf();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        calculateWrappedLines(bounds.width());
    }

    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        mTextPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mTextPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}

