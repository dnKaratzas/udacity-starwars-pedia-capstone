package eu.dkaratzas.starwarspedia.libs;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import eu.dkaratzas.starwarspedia.R;

import static android.support.v4.widget.DrawerLayout.STATE_SETTLING;

public class CustomDrawerButton extends AppCompatImageView implements DrawerLayout.DrawerListener {

    private DrawerLayout mDrawerLayout;
    private int side = Gravity.START;

    public CustomDrawerButton(Context context) {
        super(context);
    }

    public CustomDrawerButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDrawerButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void changeState() {
        if (mDrawerLayout.isDrawerOpen(side)) {
            mDrawerLayout.closeDrawer(side);
            setContentDescription(getContext().getString(R.string.navigation_drawer_open));
        } else {
            mDrawerLayout.openDrawer(side);
            setContentDescription(getContext().getString(R.string.navigation_drawer_close));
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {
        if (newState == STATE_SETTLING)
            ObjectAnimator.ofFloat(this, "rotation", 0, 360).start();
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public CustomDrawerButton setDrawerLayout(DrawerLayout mDrawerLayout) {
        this.mDrawerLayout = mDrawerLayout;
        return this;
    }
}