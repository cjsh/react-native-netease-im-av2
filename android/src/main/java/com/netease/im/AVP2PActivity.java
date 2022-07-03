package com.netease.im;

import android.os.Handler;
import android.os.Message;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class AVP2PActivity extends P2PCallActivity {

    private static Object avChatActivity;

    @Override
    protected void onResume() {
        super.onResume();
        avChatActivity = this;
        NIMClient.getService(MsgService.class)
                .setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
    }

    public static void hangupAvChat() {
        if (AVP2PActivity.avChatActivity != null) {
            ((AVP2PActivity) (AVP2PActivity.avChatActivity)).releaseAndFinish(true);
        }
    }

    public static void showDurationDialog() {
        if (AVP2PActivity.avChatActivity != null) {
            ((AVP2PActivity) (AVP2PActivity.avChatActivity)).showDialog();
        }
    }

    CommonPriceDialog dialog;

    private void showDialog() {
        dialog = new CommonPriceDialog(this);
        dialog.setMessage("您的余额还剩余5分钟的问诊时间，请立即充值，5分钟后问诊自动结束")
                .setTitle("温馨提示").setNegtive("立即充值").setOnClickBottomListener(new CommonPriceDialog.OnClickBottomListener() {
            @Override
            public void onPositiveClick() {
                dialog.dismiss();
                releaseAndFinish(true);
                finish();
            }

            @Override
            public void onNegtiveClick() {
                dialog.dismiss();
                releaseAndFinish(true);
                finish();
            }
        });
        dialog.show();
    }

    public int mDuration;
    public static Handler mHandler;
    TimerTask task;
    private Timer timer;

    public static void setDurationTime(int duration) {
        if (AVP2PActivity.avChatActivity != null) {
            ((AVP2PActivity) (AVP2PActivity.avChatActivity)).startTime(duration);
        }
    }

    private void cleanDurationTime() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }

    public void startTime(int duration) {
        //初始化定时器
        mDuration = -1;
        mHandler = new LooperHandler(this);
        mDuration = duration;
        cleanDurationTime();
        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);
                }
            };
        }

        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(task, 1000, 1000);

    }

    private static class LooperHandler extends Handler {
        WeakReference<AVP2PActivity> mWeakReference;

        LooperHandler(AVP2PActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AVP2PActivity loopersActivity = mWeakReference.get();
            if (msg.what == 1) {
                if (loopersActivity.mDuration >= 0) {
                    if (loopersActivity.mDuration == 0) {
                        loopersActivity.releaseAndFinish(true);
                        loopersActivity.finish();
                    }
                    loopersActivity.mDuration--;
                    if (loopersActivity.mDuration == 300) {
                        if (YnEventManager.instance != null) {
                            YnEventManager.instance.onAvRechargeAmount("VIDEO_CALL_FIVE_MINUTE");
                        }
                    }
                }
            }
        }
    }

}
