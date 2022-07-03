package com.netease.im;

/**
 * 雨浓事件管理器,java通知js的事件
 */
public class YnEventManager {

    public static YnEventManager instance;

//    public void onTeamAvChatUserJoin(String account) {
//    }
//
//    public void onTeamAvChatUserLeave(String account, int event) {
//    }

    /**
     * 发起群视频呼叫
     * @param teamId
     * @param account
     * @param roomId
     */
//    public void onStartTeamAvCall(String teamId, String account, String roomId) {
//    }

    /**
     * 接听多人音视频
     *
     * @param teamId
     * @param account
     */
    public void onPickUpTeamAv(String teamId, String account) {
    }

    /**
     * 挂断多人音视频
     *
     * @param teamId
     * @param account
     */
    public void onHangUpTeamAv(String teamId, String account) {
    }

    /**
     * 获取音视频的返回消息
     *
     * @param code
     * @param chatId
     */
    public void onReceiveAvCode(String eventName, String code) {
    }

    /**
     *双方音视频连接成功
     *
     */
    public void onAvConnectionSuccess(String eventName){

    }

    /**
     *音视频五分钟倒计时
     *
     */
    public void onAvLastFiveMinute(String eventName){

    }

    /**
     *音视频余额不足，去充值
     *
     */
    public void onAvRechargeAmount(String eventName){

    }


    /**
     * Activity 的onResume回调
     *
     */
    public void onAppResume(String eventName) {
    }

    /**
     * Activity 接受到第三方启动App传入的参数
     * @param params
     */
    public void onSendPageParams(String eventName, String params){

    }
}


