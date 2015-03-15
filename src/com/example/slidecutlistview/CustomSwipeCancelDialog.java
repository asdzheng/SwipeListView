
package com.example.slidecutlistview;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * ³·Ïúµ¯³ö¿ò
 */
public class CustomSwipeCancelDialog {

    private Context mContext;

    private String cancelMessage;

    private CancelDialog cancelDialog;

    private CancelListener cancelListener;

    public CustomSwipeCancelDialog(Context context) {
        mContext = context;
    }

    public void showCancelDialog() {
        if (cancelDialog == null) {
            cancelDialog = new CancelDialog(mContext);
        }
        cancelDialog.show(cancelMessage);
    }

    protected void closeCancelDialog() {
        if (cancelDialog != null) {
            cancelDialog.dismiss();
        }
    }

    public boolean isShowing() {
        if (cancelDialog != null) {
            return cancelDialog.isShowing();
        }
        return false;
    }

    public CustomSwipeCancelDialog setMessage(int textId) {
        cancelMessage = mContext.getResources().getString(textId);
        return this;
    }

    public CustomSwipeCancelDialog setMessage(String textString) {
        cancelMessage = textString;
        return this;
    }

    public void setcancelActionListener(CancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    private class CancelDialog extends Dialog implements OnClickListener {

        private Button cancelBtn;
        private TextView cancelMsg;

        public CancelDialog(Context context) {
            super(context, R.style.CommonDialog);
            setContentView(R.layout.customswipe_canceldialog_view);
            getWindow().setGravity(Gravity.BOTTOM);
            getWindow().setWindowAnimations(R.style.dialog_inout_anim);

            WindowManager.LayoutParams p = this.getWindow().getAttributes();
            p.width = (int) (CustomSwipeUtils.getScreenWidth(context));
            getWindow().setAttributes(p);

            initView();
        }

        public void initView() {
            cancelMsg = (TextView) findViewById(R.id.cancel_dialog_message);
            cancelBtn = (Button) findViewById(R.id.cancel_dialog_btn);
            cancelBtn.setOnClickListener(this);
        }

        private void show(String title) {
            cancelMsg.setText(title);
            show();
        }

        @Override
        public void dismiss() {
            if (cancelListener != null) {
                cancelListener.normalAction();
            }
            super.dismiss();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cancel_dialog_btn:
                    if (cancelListener != null) {
                        cancelListener.executeCancelAction();
                    }
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }
}
