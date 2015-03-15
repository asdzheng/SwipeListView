
package com.example.slidecutlistview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * 2015-2-13 自定义ListView
 */
public class CustomSwipeListView extends ListView {
    /**
     * 当前滑动的ListView　position
     */
    private int slidePosition;

    /**
     * 手指按下X的坐标
     */
    private int downY;
    /**
     * 手指按下Y的坐标
     */
    private int downX;
    /**
     * 屏幕宽度
     */
    private int screenWidth;
    /**
     * ListView的item
     */
    private View itemView;

    /**
     * item里面的内容区域
     */
    private View contentView;

    /**
     * 滑动类
     */
    private Scroller scroller;

    /**
     * 滑动速度极限值
     */
    private final int SNAP_VELOCITY = CustomSwipeUtils.convertDptoPx(getContext(), 1000);
    /**
     * 速度追踪对象
     */
    private VelocityTracker velocityTracker;
    /**
     * 是否响应滑动，默认为不响应
     */
    private boolean isSlide = false;
    /**
     * 认为是用户滑动的最小距离
     */
    private int mTouchSlop;
    /**
     * 移除item后的回调接口
     */
    private RemoveListener mRemoveListener;
    /**
     * 用来指示item滑出屏幕的方向,向左或者向右,用一个枚举值来标记
     */
    private RemoveDirection removeDirection;

    private boolean isRemoveScroll = false;

    /**
     * 指定计算哪个点的速度
     */
    private int mPointerId;

    /**
     * 获得允许执行一个fling手势动作的最大速度值
     */
    private int mMaxVelocity;

    int velocityX = 0;

    // 滑动删除方向的枚举值
    public enum RemoveDirection {
        RIGHT, LEFT;
    }

    public CustomSwipeListView(Context context) {
        this(context, null);
    }

    public CustomSwipeListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSwipeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        screenWidth = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getWidth();
        scroller = new Scroller(context);

        // 检测用户在move前划过的距离,移动距离大于这个距离才开始算滑动
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();

    }

    /**
     * 设置滑动删除的回调接口
     * 
     * @param removeListener
     */
    public void setRemoveListener(RemoveListener removeListener) {
        this.mRemoveListener = removeListener;
    }

    /**
     * 分发事件，主要做的是判断点击的是那个item, 以及通过postDelayed来设置响应左右滑动事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        addVelocityTracker(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mPointerId = event.getPointerId(0);

                // 假如scroller滚动还没有结束，我们直接返回
                if (!scroller.isFinished()) {
                    return super.dispatchTouchEvent(event);
                }
                downX = (int) event.getX();
                downY = (int) event.getY();

                slidePosition = pointToPosition(downX, downY);

                // 无效的position, 不做任何处理
                if (slidePosition == AdapterView.INVALID_POSITION) {
                    return super.dispatchTouchEvent(event);
                }

                // 获取我们点击的item view
                itemView = getChildAt(slidePosition - getFirstVisiblePosition());
                contentView = itemView.findViewById(R.id.ll_cotentview);

                break;

            case MotionEvent.ACTION_MOVE:

                if (Math.abs(getScrollVelocity()) > SNAP_VELOCITY
                        || (Math.abs(event.getX() - downX) > mTouchSlop && Math.abs(event.getY()
                                - downY) < mTouchSlop)) {
                    isSlide = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    /**
     * 往右滑动，getScrollX()返回的是左边缘的距离，就是以View左边缘为原点到开始滑动的距离，所以向右边滑动为负值
     */
    private void scrollRight() {
        isRemoveScroll = true;
        removeDirection = RemoveDirection.RIGHT;

        final int delta = (screenWidth + itemView.getScrollX());
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        scroller.startScroll(itemView.getScrollX(), 0, -delta, 0, Math.abs(delta));
        postInvalidate(); // 刷新itemView
    }

    /**
     * 向左滑动，根据上面我们知道向左滑动为正值
     */
    private void scrollLeft() {
        isRemoveScroll = true;
        removeDirection = RemoveDirection.LEFT;

        final int delta = (screenWidth - itemView.getScrollX());
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        scroller.startScroll(itemView.getScrollX(), 0, delta, 0, Math.abs(delta));
        postInvalidate(); // 刷新itemView
    }

    /**
     * 根据手指滚动itemView的距离来判断是滚动到开始位置还是向左或者向右滚动
     */
    private void scrollByDistanceX() {
        // 如果向左滚动的距离大于屏幕的二分之一，就让其删除
        if (itemView.getScrollX() >= screenWidth / 2) {
            scrollLeft();
        } else if (itemView.getScrollX() <= -screenWidth / 2) {
            scrollRight();
        } else {
            scrollToOrigin();
        }

    }

    // 如果滑动速度不快且距离不到1/3，就原地滑动回原点
    private void scrollToOrigin() {
        isRemoveScroll = false;
        int scrollX = itemView.getScrollX();

        // 反方向滑动回去
        scroller.startScroll(scrollX, 0, -scrollX, 0, 400);
    }

    /**
     * 处理我们拖动ListView item的逻辑
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isSlide && slidePosition != AdapterView.INVALID_POSITION) {
            addVelocityTracker(ev);
            final int action = ev.getAction();
            int x = (int) ev.getX();

            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    int deltaX = downX - x;
                    downX = x;
                    // 手指拖动itemView滚动, deltaX大于0向左滚动，小于0向右滚
                    itemView.scrollBy(deltaX, 0);

                    setCotentViewAlpha(getAlphaRatio());

                    velocityX = getScrollVelocity();

                    return true;
                case MotionEvent.ACTION_UP:

                    Log.i("scrollvelocity x ========== ", velocityX + "  " + SNAP_VELOCITY);

                    if (velocityX > SNAP_VELOCITY) {
                        scrollRight();
                    } else if (velocityX < -SNAP_VELOCITY) {
                        scrollLeft();
                    } else {
                        scrollByDistanceX();
                    }

                    recycleVelocityTracker();

                    // 手指离开的时候就不响应左右滚动
                    isSlide = false;
                    break;
            }

        }

        // 否则直接交给ListView来处理onTouchEvent事件
        return super.onTouchEvent(ev);
    }

    /**
     * 获取移动距离跟透明度的比率，总距离为1/2 屏幕宽，透明度从0~255
     */
    private int getAlphaRatio() {
        int scrollX = Math.abs(itemView.getScrollX());
        int xRatio = (int) Math.round(((2 * scrollX) / (float) screenWidth) * 255);
        // 透明度最大值为255
        xRatio = 255 - (xRatio > 255 ? 255 : xRatio);
        return xRatio;
    }

    /**
     * 设置内容区域的透明度
     */
    private void setCotentViewAlpha(int xRatio) {

        contentView.getBackground().setAlpha(xRatio);

        TextView tvTitle = (TextView) contentView.findViewById(R.id.test_title);
        TextView tvDate = (TextView) contentView.findViewById(R.id.test_date);
        setTextAlpha(xRatio, tvTitle);
        setTextAlpha(xRatio, tvDate);
    }

    /**
     * 设置文字的透明色
     */
    private void setTextAlpha(int ratio, TextView textView) {
        int color = textView.getCurrentTextColor();
        textView.setTextColor(Color.argb(ratio, Color.red(color), Color.green(color),
                Color.blue(color)));
    }

    @Override
    public void computeScroll() {
        // 调用startScroll的时候scroller.computeScrollOffset()返回true，
        if (scroller.computeScrollOffset()) {
            // 让ListView item根据当前的滚动偏移量进行滚动
            itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            setCotentViewAlpha(getAlphaRatio());

            postInvalidate();

            // 滚动动画结束的时候调用回调接口
            if (scroller.isFinished() && isRemoveScroll) {
                if (mRemoveListener == null) {
                    throw new NullPointerException(
                            "RemoveListener is null, we should called setRemoveListener()");
                }
                mRemoveListener.removeItem(removeDirection, slidePosition);

                // 删除item后要把透明度和坐标恢复到初始值
                itemView.scrollTo(0, 0);
                setCotentViewAlpha(255);
            }
        }
    }

    /**
     * 添加用户的速度跟踪器
     * 
     * @param event
     */
    private void addVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }

        velocityTracker.addMovement(event);
    }

    /**
     * 移除用户速度跟踪器
     */
    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.clear();
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    /**
     * 获取X方向的滑动速度,大于0向右滑动，反之向左
     * 
     * @return
     */
    private int getScrollVelocity() {
        velocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
        int velocity = (int) velocityTracker.getXVelocity(mPointerId);

        return velocity;
    }

    /**
     * 当ListView item滑出屏幕，回调这个接口 我们需要在回调方法removeItem()中移除该Item,然后刷新ListView
     */
    public interface RemoveListener {
        public void removeItem(RemoveDirection direction, int position);
    }

}
