package game.a2048;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.widget.TableRow;
import android.widget.TextView;

import static game.a2048.Util.getColor;

/**
 * Created by Jeany on 2018/1/3.
 */
public class Cell extends TextView {
    private int num = 0;
    AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
    public Cell(Context context) {
        super(context);
        init();
    }
    public Cell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public Cell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        TableRow.LayoutParams layout = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        layout.setMargins(10, 10, 10, 10);
        setLayoutParams(layout);
        setGravity(Gravity.CENTER);
        setTextColor(Color.WHITE);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);
    }

    public int getNum() {
        return num;
    }

    public Cell setNum(int num) {
        this.num = num;
        if (num == 0) {
            setText("");
        } else {
            setText(num + "");
        }
        return this;
    }

    public Cell setNum(int num, boolean delay) {
        if (delay) {
            startAnimation(alphaAnimation);
        }
        return setNum(num);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setBackgroundColor(getColor(this.num));
        super.onDraw(canvas);
    }
}
