package com.netease.im;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 1}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\f\b\u0016\u0018\u00002\u00020\u0001:\u0004@ABCB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u001b\u001a\u00020\u001cH\u0002J\b\u0010\u001d\u001a\u00020\u001cH\u0002J\b\u0010\u001e\u001a\u00020\u001cH\u0002J\u001e\u0010\u001f\u001a\u00020\u001c2\n\b\u0002\u0010 \u001a\u0004\u0018\u00010!2\b\b\u0002\u0010\"\u001a\u00020\nH\u0002J\b\u0010#\u001a\u00020\u001cH\u0002J\u0014\u0010$\u001a\u00020\u001c2\n\b\u0002\u0010 \u001a\u0004\u0018\u00010!H\u0002J\u0010\u0010%\u001a\u00020\u001c2\u0006\u0010 \u001a\u00020!H\u0002J\u0012\u0010&\u001a\u00020\u001c2\b\u0010\'\u001a\u0004\u0018\u00010(H\u0016J\b\u0010)\u001a\u00020\u001cH\u0002J\b\u0010*\u001a\u00020\u001cH\u0002J0\u0010+\u001a\u00020\u001c2\u0006\u0010,\u001a\u00020\u00112\n\b\u0002\u0010-\u001a\u0004\u0018\u00010\u00042\b\b\u0002\u0010.\u001a\u00020\n2\b\b\u0002\u0010/\u001a\u00020\nH\u0002J\b\u00100\u001a\u00020\u001cH\u0002J\b\u00101\u001a\u00020\u001cH\u0002J\u0014\u00102\u001a\u00020\u001c2\n\b\u0002\u00103\u001a\u0004\u0018\u00010\u0004H\u0002J\b\u00104\u001a\u00020\u001cH\u0016J\b\u00105\u001a\u00020\u001cH\u0014J\b\u00106\u001a\u00020\u0011H\u0014J\b\u00107\u001a\u000208H\u0014J\u0010\u00109\u001a\u00020\u001c2\u0006\u0010:\u001a\u00020\nH\u0014J\u0010\u0010;\u001a\u00020\u001c2\u0006\u0010<\u001a\u00020\u0011H\u0002J\u000e\u0010=\u001a\u00020\u001c2\u0006\u0010>\u001a\u00020\u0011J\b\u0010?\u001a\u00020\u001cH\u0002R\u0014\u0010\u0003\u001a\u00020\u0004X\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u0004X\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0006R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0018\u0010\u0017\u001a\u00060\u0018R\u00020\u00008BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0019\u0010\u001a\u00a8\u0006D"}, d2 = {"Lcom/netease/im/P2PCallActivity;", "Lcom/netease/yunxin/nertc/ui/base/CommonCallActivity;", "()V", "VIDEO_CALL_CODE", "", "getVIDEO_CALL_CODE", "()Ljava/lang/String;", "VIDEO_CALL_SUCCESS", "getVIDEO_CALL_SUCCESS", "callFinished", "", "delegate", "Lcom/netease/yunxin/nertc/ui/p2p/NERtcCallDelegateForP2P;", "firstLaunch", "onClickListener", "Landroid/view/View$OnClickListener;", "startPreviewCode", "", "tag", "timer", "Lcom/netease/yunxin/nertc/ui/utils/SecondsTimer;", "typeAudio", "typeVideo", "uiRender", "Lcom/netease/im/P2PCallActivity$UIRender;", "getUiRender", "()Lcom/netease/im/P2PCallActivity$UIRender;", "doAccept", "", "doCall", "doCancel", "doConfigSpeakerSwitch", "view", "Landroid/widget/ImageView;", "speakerEnable", "doHangup", "doMuteAudioSwitch", "doMuteVideo", "doOnCreate", "savedInstanceState", "Landroid/os/Bundle;", "doReject", "doSwitchCallType", "forUserInfoUI", "type", "accId", "visible", "forVideoCaller", "initForLaunchAction", "initForLaunchUI", "initForOnTheCall", "userAccId", "onBackPressed", "onPause", "provideLayoutId", "provideRtcDelegate", "Lcom/netease/yunxin/nertc/nertcvideocall/model/NERTCCallingDelegate;", "releaseAndFinish", "finishCall", "resetSwitchState", "channelType", "sendAction", "status", "showExitDialog", "AudioRender", "UIRender", "UserState", "VideoRender", "react-native-neteset-im-av2_debug"})
public class P2PCallActivity extends com.netease.yunxin.nertc.ui.base.CommonCallActivity {
    private final java.lang.String tag = "P2PCallActivity";
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String VIDEO_CALL_CODE = "VIDEO_CALL_CODE";
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String VIDEO_CALL_SUCCESS = "VIDEO_CALL_SUCCESS";
    private final int typeAudio = 0;
    private final int typeVideo = 0;
    private final com.netease.yunxin.nertc.ui.utils.SecondsTimer timer = null;
    private int startPreviewCode = -1;
    private boolean callFinished = true;
    private final com.netease.yunxin.nertc.ui.p2p.NERtcCallDelegateForP2P delegate = null;
    private final android.view.View.OnClickListener onClickListener = null;
    private boolean firstLaunch = true;
    private java.util.HashMap _$_findViewCache;
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getVIDEO_CALL_CODE() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getVIDEO_CALL_SUCCESS() {
        return null;
    }
    
    public final void sendAction(int status) {
    }
    
    private final com.netease.im.P2PCallActivity.UIRender getUiRender() {
        return null;
    }
    
    @java.lang.Override()
    public void doOnCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    protected int provideLayoutId() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    protected com.netease.yunxin.nertc.nertcvideocall.model.NERTCCallingDelegate provideRtcDelegate() {
        return null;
    }
    
    @java.lang.Override()
    protected void releaseAndFinish(boolean finishCall) {
    }
    
    @java.lang.Override()
    public void onBackPressed() {
    }
    
    @java.lang.Override()
    protected void onPause() {
    }
    
    private final void showExitDialog() {
    }
    
    private final void initForLaunchUI() {
    }
    
    private final void initForLaunchAction() {
    }
    
    /**
     * 通话中页面初始化
     */
    private final void initForOnTheCall(java.lang.String userAccId) {
    }
    
    private final void doCall() {
    }
    
    private final void doCancel() {
    }
    
    private final void doAccept() {
    }
    
    private final void doReject() {
    }
    
    private final void doHangup() {
    }
    
    private final void doMuteVideo(android.widget.ImageView view) {
    }
    
    private final void doConfigSpeakerSwitch(android.widget.ImageView view, boolean speakerEnable) {
    }
    
    private final void doMuteAudioSwitch(android.widget.ImageView view) {
    }
    
    private final void doSwitchCallType() {
    }
    
    private final void forUserInfoUI(int type, java.lang.String accId, boolean visible, boolean forVideoCaller) {
    }
    
    private final void resetSwitchState(int channelType) {
    }
    
    public P2PCallActivity() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 1}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0092\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016J\b\u0010\u0005\u001a\u00020\u0004H\u0016J\u0014\u0010\u0006\u001a\u00020\u00042\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0016J\u0010\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u000bH\u0016\u00a8\u0006\f"}, d2 = {"Lcom/netease/im/P2PCallActivity$UIRender;", "", "(Lcom/netease/im/P2PCallActivity;)V", "renderForCalled", "", "renderForCaller", "renderForOnTheCall", "userAccId", "", "updateOnTheCallState", "state", "Lcom/netease/im/P2PCallActivity$UserState;", "react-native-neteset-im-av2_debug"})
    class UIRender {
        
        public void renderForCaller() {
        }
        
        public void renderForCalled() {
        }
        
        public void renderForOnTheCall(@org.jetbrains.annotations.Nullable()
        java.lang.String userAccId) {
        }
        
        public void updateOnTheCallState(@org.jetbrains.annotations.NotNull()
        com.netease.im.P2PCallActivity.UserState state) {
        }
        
        public UIRender() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 4, 1}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\u0004\u0018\u00002\u00060\u0001R\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\b\u0010\u0006\u001a\u00020\u0005H\u0016J\u0012\u0010\u0007\u001a\u00020\u00052\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0016\u00a8\u0006\n"}, d2 = {"Lcom/netease/im/P2PCallActivity$AudioRender;", "Lcom/netease/im/P2PCallActivity$UIRender;", "Lcom/netease/im/P2PCallActivity;", "(Lcom/netease/im/P2PCallActivity;)V", "renderForCalled", "", "renderForCaller", "renderForOnTheCall", "userAccId", "", "react-native-neteset-im-av2_debug"})
    final class AudioRender extends com.netease.im.P2PCallActivity.UIRender {
        
        @java.lang.Override()
        public void renderForCaller() {
        }
        
        @java.lang.Override()
        public void renderForCalled() {
        }
        
        @java.lang.Override()
        public void renderForOnTheCall(@org.jetbrains.annotations.Nullable()
        java.lang.String userAccId) {
        }
        
        public AudioRender() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 4, 1}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0082\u0004\u0018\u00002\u00060\u0001R\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\b\u0010\u0006\u001a\u00020\u0005H\u0016J\u0012\u0010\u0007\u001a\u00020\u00052\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0016J\u0010\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\fH\u0016\u00a8\u0006\r"}, d2 = {"Lcom/netease/im/P2PCallActivity$VideoRender;", "Lcom/netease/im/P2PCallActivity$UIRender;", "Lcom/netease/im/P2PCallActivity;", "(Lcom/netease/im/P2PCallActivity;)V", "renderForCalled", "", "renderForCaller", "renderForOnTheCall", "userAccId", "", "updateOnTheCallState", "state", "Lcom/netease/im/P2PCallActivity$UserState;", "react-native-neteset-im-av2_debug"})
    final class VideoRender extends com.netease.im.P2PCallActivity.UIRender {
        
        @java.lang.Override()
        public void renderForCaller() {
        }
        
        @java.lang.Override()
        public void renderForCalled() {
        }
        
        @java.lang.Override()
        public void renderForOnTheCall(@org.jetbrains.annotations.Nullable()
        java.lang.String userAccId) {
        }
        
        @java.lang.Override()
        public void updateOnTheCallState(@org.jetbrains.annotations.NotNull()
        com.netease.im.P2PCallActivity.UserState state) {
        }
        
        public VideoRender() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 4, 1}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u001b\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0006R\u0015\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\n\n\u0002\u0010\t\u001a\u0004\b\u0007\u0010\bR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\f"}, d2 = {"Lcom/netease/im/P2PCallActivity$UserState;", "", "userAccId", "", "muteVideo", "", "(Ljava/lang/String;Ljava/lang/Boolean;)V", "getMuteVideo", "()Ljava/lang/Boolean;", "Ljava/lang/Boolean;", "getUserAccId", "()Ljava/lang/String;", "react-native-neteset-im-av2_debug"})
    public static final class UserState {
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String userAccId = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Boolean muteVideo = null;
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getUserAccId() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Boolean getMuteVideo() {
            return null;
        }
        
        public UserState(@org.jetbrains.annotations.Nullable()
        java.lang.String userAccId, @org.jetbrains.annotations.Nullable()
        java.lang.Boolean muteVideo) {
            super();
        }
    }
}