package player.com.roshkatian.draglist;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements Checkable {

    private static final int CHECKABLE_CHILD_INDEX = 1;
    private Checkable child;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        child = (Checkable) getChildAt(CHECKABLE_CHILD_INDEX);
    }

    @Override
    public boolean isChecked() {
        return child.isChecked(); 
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked){
            setBackgroundColor(Color.parseColor("#E3F2FD"));
        }else{
            setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        child.setChecked(checked);

    }

    @Override
    public void toggle() {
        child.toggle();
    }
    
}
