package com.netease.im;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.netease.im.login.LoginService;
import com.netease.im.uikit.cache.SimpleCallback;
import com.netease.im.uikit.cache.TeamDataCache;
import com.netease.im.uikit.common.util.log.LogUtil;
import com.netease.lava.nertc.sdk.NERtc;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.FriendFieldEnum;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.yunxin.nertc.ui.CallKitUI;
import com.netease.yunxin.nertc.ui.base.CallParam;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//todo:liujixin待办事项:直接加入视频聊天室功能;后台java实现接口功能
public class AvChatModule extends ReactContextBaseJavaModule {

    protected Context context;
    private final static String TAG = "AvChatSession";
    //{"id":3,"members":["nim275955","nim490073"],"room":"remoteCharRoom424","teamId":"2890115941","teamName":"TeamNaming190"}
//    private String teamId = "2890115941";//2890115941,2891056379,2891167176
//    private String teamName = "TeamNaming190";//TeamNaming190,TeamNaming510,TeamNaming999

    private static final String KEY_ID = "id";
    private static final String KEY_MEMBER = "members";
    private static final String KEY_TID = "teamId";
    private static final String KEY_RID = "room";
    private static final String KEY_TNAME = "teamName";
    private boolean isTeamAVChatting = false;
    private boolean isSyncComplete = true; // 未开始也算同步完成，可能存在不启动同步的情况
    private static final long OFFLINE_EXPIRY = 45 * 1000;
    private static final int ID = 3;
    private ReactInstanceManager mReactInstanceManager;

    //15800736491,18701746457,17521706684
//    private ArrayList<String> nimAccounts = new ArrayList<String>(Arrays.asList("nim195974", "nim198316", "nim481658"));
    //teamId为2890115941的成员: "nim275955", "nim490073", "nim195974", "nim481658",好像是这个:nim195974;nim481658;nim490073
//    private ArrayList<String> nimAccounts = new ArrayList<String>(Arrays.asList("nim275955", "nim490073", "nim195974"));

    public AvChatModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void doTest(final String param1, String extraMessage, final Callback callback) {
        //String teamId = "2890115941";
//        TeamDataCache.getInstance().fetchTeamMemberList(param1, new SimpleCallback<List<TeamMember>>() {
//            @Override
//            public void onResult(boolean success, List<TeamMember> result) {
//                if (success && result != null) {
//                    final ArrayList<String> accounts = new ArrayList<String>();
//                    String string = "";
//                    for (TeamMember member : result) {
//                        if (!member.getAccount().equalsIgnoreCase(AVChatKit.getAccount())) {
//                            accounts.add(member.getAccount());
//                            string += member.getAccount();
//                        }
//                    }
//                    callback.invoke("获取到群组的成员为:" + string, "success");
//                }
//            }
//        });

//        ReactInstanceManager mReactInstanceManager = getCurrentActivity().getApplication()
//                .getReactNativeHost().getReactInstanceManager();
//        ReactApplicationContext context = (ReactApplicationContext) mReactInstanceManager.getCurrentReactContext();
//        mReactInstanceManager.addReactInstanceEventListener(new ReactInstanceManager.ReactInstanceEventListener() {
//            public void onReactContextInitialized(ReactContext validContext) {
//                // Use validContext here
//
//            }
//        });

//        mReactInstanceManager = ReactInstanceManager.builder()
//                .setApplication(getCurrentActivity().getApplication())
//                .setBundleAssetName("index.android.bundle")
//                //.setJSMainModulePath("index")
//                .setJSMainModuleName("index.android")
//                .addPackage(new MainReactPackage())
//                .setUseDeveloperSupport(BuildConfig.DEBUG)
//                .setInitialLifecycleState(LifecycleState.RESUMED)
//                .build();
//        ReactContext reactContext = mReactInstanceManager != null ? mReactInstanceManager.getCurrentReactContext() : null;
        ReactContext reactContext = getReactApplicationContext();
        if (reactContext == null) {
            return;
        }
        WritableMap params = Arguments.createMap();
        params.putString("data", "AAAA");
        String eventName = "onPickUpTeamAv";
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }


    @ReactMethod
    public void callAvChat(ReadableMap params, Promise promise) {
        String sessionId = params.getString("sessionId");
        String sessionName = params.getString("sessionName");
        String seesionAvatar = params.getString("seesionAvatar");
        String extend = "";
        String account = LoginService.getInstance().getLoginInfo(context).getAccount();

        // 自定义透传字段，被叫用户在收到呼叫邀请时通过参数进行解析
        JSONObject extraInfo = new JSONObject();

        try {
            extraInfo.putOpt("key", "call");
            extraInfo.putOpt("value", "testValue");
            extraInfo.putOpt("userName", sessionName);
            extraInfo.putOpt("avatar", seesionAvatar);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // @param type：呼叫类型 1-音频呼叫，2-视频呼叫
        // @param callerAccId： 呼叫方 IM 账号 Id
        // @param calledAccId： 被叫方 IM 账号 id
        String chatType = params.getString("chatType");
        CallKitUI.startSingleCall(getCurrentActivity(),
                CallParam.createSingleCallParam(chatType.equals("1") ? ChannelType.AUDIO.getValue() : ChannelType.VIDEO.getValue(), account, sessionId, extraInfo.toString()));
    }

    @ReactMethod
    public void hangupAvChat(ReadableMap params, Promise promise) {
        AVP2PActivity.hangupAvChat();
    }

    @ReactMethod
    public void setAvDuration(ReadableMap params, Promise promise) {
        Activity activity = getCurrentActivity();
        LogUtil.e("setAvDuration", "params=" + params + "-activity：" + activity + " context：" + context);
        int sessionId = params.getInt("duration");
        AVP2PActivity.setDurationTime(sessionId);
    }

    @ReactMethod
    public void showRechargeAmountDialog(ReadableMap params, Promise promise) {
        AVP2PActivity.showDurationDialog();
    }

    @Override
    public String getName() {
        return "AvChatSession";
    }

    @ReactMethod
    public void createRoom(String roomName, String extraMessage, final Callback callback) {
        callback.invoke("success callback step1!!", "success");
//        callback.invoke("success callback step2!!", "success");
    }

    @ReactMethod
    public void joinMpvRoom(final String teamId, final String teamName, final String roomId, String extraMessage, final Callback callback) {
        TeamDataCache.getInstance().fetchTeamMemberList(teamId, new SimpleCallback<List<TeamMember>>() {
            @Override
            public void onResult(boolean success, List<TeamMember> result) {
                if (success && result != null) {
                    //获取到群组成员
                    final ArrayList<String> accounts = new ArrayList<String>();
                    for (TeamMember member : result) {
//                        if (!member.getAccount().equalsIgnoreCase(AVChatKit.getAccount())) {
                        accounts.add(member.getAccount());
//                        }
                    }
                    CallParam param = CallParam.createGroupCallParam(2, LoginService.getInstance().getLoginInfo(context).getAccount(), accounts, teamId);
                    CallKitUI.startGroupCall(getCurrentActivity(), param);
                    callback.invoke("成功!", "success");
                } else {
                    callback.invoke("无法获取到群组的成员!", "failed");
                }
            }
        });
        callback.invoke("data", "joinMpvRoom success");
    }

    /**
     * 创建多人视频房间
     */
    @ReactMethod
    public void createAndJoinRoom2(final String teamId, final String teamName, final String extraMessage, final Callback callback) {
//        final String roomName = "mpvRoom" + roomId;//todo:测试用,这种方式一个team不能开启多个视频房间
        TeamDataCache.getInstance().fetchTeamMemberList(teamId, new SimpleCallback<List<TeamMember>>() {
            @Override
            public void onResult(boolean success, List<TeamMember> result) {
                if (success && result != null) {
                    //获取到群组成员
                    final ArrayList<String> accounts = new ArrayList<String>();
                    for (TeamMember member : result) {
                        if (!member.getAccount().equalsIgnoreCase(LoginService.getInstance().getLoginInfo(context).getAccount())) {
                            accounts.add(member.getAccount());
                        }
                    }
                    CallParam param = CallParam.createGroupCallParam(2, LoginService.getInstance().getLoginInfo(context).getAccount(), accounts, teamId);
                    CallKitUI.startGroupCall(getCurrentActivity(), param);
                } else {
                    callback.invoke("无法获取到群组的成员,无法创建视频房间!", "failed");
                }
            }
        });

    }

    /**
     * 加入已经预先创建的多人会议房间
     */
    @ReactMethod
    public void joinRoom2(String roomName, final Callback callback) {
        final String roomId = UUID.randomUUID().toString().replaceAll("-", "");

        //开启视频预览
        NERtcEx.getInstance().startVideoPreview();
        NERtc.getInstance().joinChannel("", roomId, Long.parseLong(LoginService.getInstance().getLoginInfo(context).getAccount()));
    }

    private void onJoinRoomSuccess() {
//        startTimer();
        startLocalPreview();
        //startTimerForCheckReceivedCall();
//        LogUtil.i(TAG, "team avchat running..." + ", roomId=" + roomName);
    }

    private void startLocalPreview() {
//        NERtcEx.getInstance().startVideoPreview();
    }

    private void onTest() {

    }

    /**
     * 离开加入的多人会议房间
     */
    @ReactMethod
    public void leaveRoom2(String roomName, final Callback callback) {
        //关闭视频预览
        NERtcEx.getInstance().stopVideoPreview();
        NERtcEx.getInstance().leaveChannel();
    }

    private void onCreateRoom2Success(String teamId, String teamName, String roomName, List<String> accounts) {
        //String teamID = transaction.getTeamID();
        //在群里发送tip消息
//        IMMessage message = MessageBuilder.createTipMessage(teamID, SessionTypeEnum.Team);
//        CustomMessageConfig tipConfig = new CustomMessageConfig();
//        tipConfig.enableHistory = false;
//        tipConfig.enableRoaming = false;
//        tipConfig.enablePush = false;
//        String teamNick = TeamHelper.getDisplayNameWithoutMe(teamID, DemoCache.getAccount());
//        message.setContent(teamNick + getActivity().getString(R.string.t_avchat_start));
//        message.setConfig(tipConfig);
//        sendMessage(message);

        //对各个成员发送点对点自定义通知
//        String content = TeamAVChatProfile.sharedInstance().buildContent(roomName, teamId, accounts, teamName);
//        CustomNotificationConfig config = new CustomNotificationConfig();
//        config.enablePush = true;
//        config.enablePushNick = false;
//        config.enableUnreadCount = true;
////
//        String teamNick = "某teamNick某";
//        for (String account : accounts) {
//            CustomNotification command = new CustomNotification();
//            command.setSessionId(account);
//            command.setSessionType(SessionTypeEnum.P2P);
//            command.setConfig(config);
//            command.setContent(content);
//            command.setApnsText(teamNick + getCurrentActivity().getString(R.string.t_avchat_push_content));
//            command.setApnsText("某teamNick某发起了视频聊天!");
//
//            command.setSendToOnlineUserOnly(false);
//            NIMClient.getService(MsgService.class).sendCustomNotification(command);
//        }
    }

}
