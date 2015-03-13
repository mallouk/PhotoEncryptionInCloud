package utc_4910.photoencryptionincloud;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.GridLayout;

/**
 * Created by Matthew Jallouk on 3/12/2015.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class PasswordGrid extends GridLayout {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PasswordGrid(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }


}
