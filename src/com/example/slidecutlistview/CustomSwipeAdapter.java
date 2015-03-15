
package com.example.slidecutlistview;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slidecutlistview.CustomSwipeListView.RemoveDirection;
import com.example.slidecutlistview.CustomSwipeListView.RemoveListener;

/**
 * 实现撤销动作的Adapter
 */
public class CustomSwipeAdapter extends BaseAdapter implements CancelListener, RemoveListener {

    private static final int INVALID_POSITION = -1;

    protected Context mContext;

    private TestModel deleteModel;

    // 测试数据的实体类列表
    private List<TestModel> testModels;

    // 记录删除的item的位置
    private int deletedPosition;

    // 是否撤销删除的item
    private boolean cancelRemoveItem = false;

    // 滑动的方向
    private RemoveDirection deleteDirection;

    // 记录是否上一次弹出框还没消失
    private boolean isCountingTime;

    // 撤销弹出框的线程
    private Runnable dismissRunnable;
    private Handler handler;

    private CustomSwipeCancelDialog cancelDialog;

    public CustomSwipeAdapter(Context context, List<TestModel> Objects) {
        mContext = context;
        testModels = Objects;

        handler = new Handler();
        dismissRunnable = new DismissRunnable();

        cancelDialog = new CustomSwipeCancelDialog(context);
        cancelDialog.setcancelActionListener(this);
    }

    @Override
    public TestModel getItem(int position) {
        return testModels.get(position);
    }

    @Override
    public int getCount() {
        return testModels.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.test_listview_item_view, parent,
                    false);
            holder = new ViewHolder();
            holder.tvDate = (TextView) view.findViewById(R.id.test_date);
            holder.tvTitle = (TextView) view.findViewById(R.id.test_title);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.tvTitle.setText(getItem(position).getTestTitle());
        holder.tvDate.setText(getItem(position).getTestDate());

        if (cancelRemoveItem) {
            cancelActionAnimation(view.findViewById(R.id.ll_cotentview), position);
        }

        return view;
    }

    class ViewHolder {
        TextView tvTitle;
        TextView tvDate;
    }

    /**
     * 执行撤销动画
     */
    private void cancelActionAnimation(View contentView, int undoPosition) {
        if (undoPosition == deletedPosition) {
            switch (deleteDirection) {
                case LEFT:
                    contentView.startAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.canceldialog_push_left_in));
                    break;

                case RIGHT:
                    contentView.startAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.canceldialog_push_right_in));
                    break;

                default:
                    break;
            }

            clearDeletedObject();
        } else {
            contentView.clearAnimation();
        }
    }

    /**
     * 撤销dialog消失时调用
     */
    @Override
    public void normalAction() {
        if (!cancelRemoveItem) {
            clearDeletedObject();
        }
    }

    public void clearDeletedObject() {
        deleteModel = null;
        cancelRemoveItem = false;
        deletedPosition = INVALID_POSITION;
    }

    /**
     * 删除后点击撤销的操作
     */
    @Override
    public void executeCancelAction() {
        if (deletedPosition <= testModels.size() && deletedPosition != INVALID_POSITION) {
            testModels.add(deletedPosition, deleteModel);
            cancelRemoveItem = true;
            notifyDataSetChanged();
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 滑动删除之后的回调方法
     */
    @Override
    public void removeItem(RemoveDirection direction, int position) {
        // 上一个删除item在延迟的时间内，再删除另一个，要先终止上一个runnable
        if (isCountingTime) {
            handler.removeCallbacks(dismissRunnable);
        }

        TestModel model = removeItemByPosition(position, direction);
        cancelDialog.setMessage("Delete" + model.getTestTitle()).showCancelDialog();

        dismissDialog();

        switch (direction) {
            case RIGHT:
                Toast.makeText(mContext, "向右删除  " + position, Toast.LENGTH_SHORT).show();
                break;
            case LEFT:
                Toast.makeText(mContext, "向左删除  " + position, Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }

    }

    /**
     * 删除操作并保存被删除对象信息
     */
    public TestModel removeItemByPosition(int position, RemoveDirection direction) {
        if (position < getCount() && position != INVALID_POSITION) {
            deleteModel = testModels.remove(position);
            deletedPosition = position;
            deleteDirection = direction;
            notifyDataSetChanged();
            return deleteModel;
        } else {
            throw new IndexOutOfBoundsException("The position is invalid!");
        }
    }

    /**
     * 弹出撤销对话框后一段时间内（5秒）还没任何操作的话，对话框自动消失
     */
    private void dismissDialog() {
        isCountingTime = true;

        handler.postDelayed(dismissRunnable, 5000);
    }

    class DismissRunnable implements Runnable {

        @Override
        public void run() {
            if (cancelDialog.isShowing()) {
                cancelDialog.closeCancelDialog();
                clearDeletedObject();
                isCountingTime = false;
            }
        }

    }
}
