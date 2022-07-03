package com.netease.im;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.location.LocationProvider;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.DrawableRes;

import com.netease.im.common.ImageLoaderKit;
import com.netease.im.common.sys.SystemUtil;
import com.netease.im.contact.DefalutUserInfoProvider;
import com.netease.im.contact.DefaultContactProvider;
import com.netease.im.login.LoginService;
import com.netease.im.session.SessionUtil;
import com.netease.im.session.extension.CustomAttachParser;
import com.netease.im.uikit.LoginSyncDataStatusObserver;
import com.netease.im.uikit.cache.DataCacheManager;
import com.netease.im.uikit.common.util.log.LogUtil;
import com.netease.im.uikit.common.util.media.ImageUtil;
import com.netease.im.uikit.common.util.storage.StorageType;
import com.netease.im.uikit.common.util.storage.StorageUtil;
import com.netease.im.uikit.common.util.sys.ScreenUtil;
import com.netease.im.uikit.contact.core.ContactProvider;
import com.netease.im.uikit.contact.core.query.PinYin;
import com.netease.im.uikit.session.helper.MessageHelper;
import com.netease.lava.nertc.sdk.NERtcOption;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.mixpush.MixPushConfig;
import com.netease.nimlib.sdk.mixpush.MixPushService;
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RevokeMsgNotification;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.util.NIMUtil;
import com.netease.yunxin.nertc.ui.CallKitUI;
import com.netease.yunxin.nertc.ui.CallKitUIOptions;
import com.netease.yunxin.nertc.ui.base.AVChatSoundPlayer;
import com.netease.yunxin.nertc.ui.base.SoundHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by dowin on 2017/4/28.
 */

public class IMApplication {


    public static class MiPushConfig {

        public String certificate;
        public String appID;
        public String appKey;

        /**
         * 注册小米推送证书名称 、推送appID 以及在云信管理后台添加的appKey
         */
        public MiPushConfig(String certificate, String appID, String appKey) {
            this.certificate = certificate;
            this.appID = appID;
            this.appKey = appKey;
        }
    }

    // context
    private static Context context;

    private static Class mainActivityClass;
    @DrawableRes
    private static int notify_msg_drawable_id;
    // 用户信息提供者
    private static UserInfoProvider userInfoProvider;

    // 通讯录信息提供者
    private static ContactProvider contactProvider;

    // 地理位置信息提供者
    private static LocationProvider locationProvider;

    // 图片加载、缓存与管理组件
    private static ImageLoaderKit imageLoaderKit;
    private static StatusBarNotificationConfig statusBarNotificationConfig;
    private static boolean DEBUG = false;

    public static void config(Context context, MiPushConfig miPushConfig) {
        Log.d("cj", "config-------");
        IMApplication.context = context.getApplicationContext();
        MixPushConfig config = new MixPushConfig();
        // 注册小米推送appID 、appKey 以及在云信管理后台添加的小米推送证书名称，该逻辑放在 NIMClient init 之前
        if (miPushConfig != null) {
            // NIMPushClient.registerMiPush(context, miPushConfig.certificate, miPushConfig.appID, miPushConfig.appKey);
            //NIMPushClient.registerMixPushMessageHandler();
            config.xmAppId = miPushConfig.appID;
            config.xmAppKey = miPushConfig.appKey;
            config.xmCertificateName = miPushConfig.certificate;
        }
        NIMClient.config(context, loginInfo(), getOptions(context, config));
    }

    public static void init(Context context, Class mainActivityClass, @DrawableRes int notify_msg_drawable_id, MiPushConfig miPushConfig) {
        IMApplication.context = context.getApplicationContext();
        IMApplication.mainActivityClass = mainActivityClass;
        IMApplication.notify_msg_drawable_id = notify_msg_drawable_id;
        NIMClient.initSDK();
        // crash handler
//        AppCrashHandler.getInstance(context);
        if (NIMUtil.isMainProcess(IMApplication.context)) {
            NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(new Observer<StatusCode>() {
                @Override
                public void onEvent(StatusCode statusCode) {
                    if (statusCode == StatusCode.LOGINED) {
                        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(this, false);
                        CallKitUIOptions options = new CallKitUIOptions.Builder()
                                // 音视频通话 sdk appKey，用于通话中使用
                                .rtcAppKey(context.getString(R.string.app_key))
                                // 当前用户 accId
//                                .currentUserAccId("")
                                // 通话接听成功的超时时间单位 毫秒，默认30s
                                .timeOutMillisecond(30 * 1000L)
                                // 当系统版本为 Android Q及以上时，若应用在后台系统限制不直接展示页面
                                // 而是展示 notification，通过点击 notification 跳转呼叫页面
                                // 此处为 notification 相关配置，如图标，提示语等。
//                                .notificationConfigFetcher(new SelfNotificationConfigFetcher())
                                // 收到被叫时若 app 在后台，在恢复到前台时是否自动唤起被叫页面，默认为 true
                                .resumeBGInvitation(true)
                                .p2pVideoActivity(AVP2PActivity.class)
                                .p2pAudioActivity(AVP2PActivity.class)
                                // 请求 rtc token 服务，若非安全模式不需设置，安全模式按照官网实现 token 服务通过如下接口设置回组件
//                            .rtcTokenService(new TokenService() {
//                                @Override
//                                public void getToken(long uid, RequestCallback<String> callback) {
//                                    Result result = network.requestToken(uid);
//                                    if (result.success) {
//                                        callback.onSuccess(result.token);
//                                    } else if (result.exception != null) {
//                                        callback.onException(result.exception);
//                                    } else {
//                                        callback.onFailed(result.code);
//                                    }
//                                }
//                            })
                                // 设置初始化 rtc sdk 相关配置，按照所需进行配置
                                .rtcSdkOption(new NERtcOption())
                                .rtcInitScope(false)
                                // 设置用户信息
                                .userInfoHelper(new SelfUserInfoHelper())
                                .build();
                        // 若重复初始化会销毁之前的初始化实例，重新初始化
                        CallKitUI.init(context, options);
                    }
                }
            }, true);

            // init pinyin
            PinYin.init(context);
            PinYin.validate();

            if (miPushConfig != null) {
                NIMClient.getService(MixPushService.class).enable(true);
            }

            // 初始化Kit模块
            initKit();
            Log.d("cj", "initKit-------");
        }
    }

    public static void setDebugAble(boolean debugAble) {
        DEBUG = debugAble;
        LogUtil.setDebugAble(debugAble);
    }

    private static Observer<CustomNotification> notificationObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification customNotification) {
            NotificationManager notificationManager = (NotificationManager) IMApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            SessionUtil.receiver(notificationManager, customNotification);
        }
    };

    private static boolean inMainProcess(Context context) {
        String packageName = context.getPackageName();
        String processName = SystemUtil.getProcessName(context);
        return packageName.equals(processName);
    }


    public static Context getContext() {
        return context;
    }

    public static int getNotify_msg_drawable_id() {
        return notify_msg_drawable_id;
    }

    public static Class getMainActivityClass() {
        return mainActivityClass;
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private static LoginInfo loginInfo() {
        UserModel userModel = ProfileManager.getInstance().getUserModel();
        if (userModel != null && !TextUtils.isEmpty(userModel.imToken) && !TextUtils.isEmpty(userModel.imAccid)) {
            return new LoginInfo(String.valueOf(userModel.imAccid), userModel.imToken);
        }
        return null;
    }

    public static String getSdkStorageRooPath() {
        return Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/nim";
    }

    private static SDKOptions getOptions(Context context, MixPushConfig config) {
        SDKOptions options = new SDKOptions();
        options.appKey = "71d314ba4d301b42167d460a49ced993";

        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
        initStatusBarNotificationConfig(options, context);

        // 配置保存图片，文件，log等数据的目录

        options.sdkStorageRootPath = getSdkStorageRooPath();

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "NETEASE";

        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小，
        options.thumbnailSize = ImageUtil.getImageMaxEdge();

        // 用户信息提供者
        options.userInfoProvider = new DefalutUserInfoProvider(context);

        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
        options.messageNotifierCustomization = messageNotifierCustomization;

        // 在线多端同步未读数
        options.sessionReadAck = true;
        //自动检查 SDK 配置是否完全
        options.checkManifestConfig = DEBUG;
        options.mixPushConfig = config;
        //reducedIM 支持弱 IM 场景
        //asyncInitSDK 支持异步 SDK 初始化
        //teamNotificationMessageMarkUnread 登录选项添加群通知消息是否计入未读数开关
        //sdkStorageRootPath 配置的外置存储缓存根目录

        return options;
    }

    // 这里开发者可以自定义该应用初始的 StatusBarNotificationConfig
    private static StatusBarNotificationConfig loadStatusBarNotificationConfig(Context context) {
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        // 点击通知需要跳转到的界面
        config.notificationEntrance = mainActivityClass;
        config.notificationSmallIconId = notify_msg_drawable_id;

        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://" + context.getPackageName() + "/raw/msg";

        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;

        // save cache，留做切换账号备用
        setStatusBarNotificationConfig(config);
        return config;
    }

    private static void initStatusBarNotificationConfig(SDKOptions options, Context context) {
        // load 应用的状态栏配置
        StatusBarNotificationConfig config = loadStatusBarNotificationConfig(context);

        // load 用户的 StatusBarNotificationConfig 设置项
        StatusBarNotificationConfig userConfig = null;//UserPreferences.getStatusConfig();
        if (userConfig == null) {
            userConfig = config;
        } else {
            // 新增的 UserPreferences 存储项更新，兼容 3.4 及以前版本
            // APP默认 StatusBarNotificationConfig 配置修改后，使其生效
            userConfig.notificationEntrance = config.notificationEntrance;
            userConfig.notificationFolded = config.notificationFolded;
//          userConfig.notificationColor = Color.parseColor("#3a9efb");
        }
        // 持久化生效
//        UserPreferences.setStatusConfig(config);
        // SDK statusBarNotificationConfig 生效
        options.statusBarNotificationConfig = userConfig;
    }

    private static MessageNotifierCustomization messageNotifierCustomization = new MessageNotifierCustomization() {
        @Override
        public String makeNotifyContent(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }

        @Override
        public String makeTicker(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }

        @Override
        public String makeRevokeMsgTip(String revokeAccount, IMMessage item) {
            return MessageUtil.getRevokeTipContent(item, revokeAccount);
        }
    };


    /*********************/
    public static void initKit() {
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new CustomAttachParser());
        initUserInfoProvider(userInfoProvider);
        initContactProvider(contactProvider);
//        initDefalutSessionCustomization();
//        initDefalutContactEventListener();

        imageLoaderKit = new ImageLoaderKit(context, null);

        // init data cache
        LoginSyncDataStatusObserver.getInstance().registerLoginSyncDataStatus(true);  // 监听登录同步数据完成通知
        DataCacheManager.observeSDKDataChanged(true);
        if (!TextUtils.isEmpty(LoginService.getInstance().getAccount())) {
            DataCacheManager.buildDataCache(); // build data cache on auto login
        }

        // init tools
        StorageUtil.init(context, null);
        ScreenUtil.init(context);

        // 注册消息撤回监听器
        registerMsgRevokeObserver();

//        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(notificationObserver, true);

        // init log
        String path = StorageUtil.getDirectoryByDirType(StorageType.TYPE_LOG);
        LogUtil.init(path, Log.DEBUG);
    }

    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void registerMsgRevokeObserver() {
        NIMClient.getService(MsgServiceObserve.class).observeRevokeMessage(new Observer<RevokeMsgNotification>() {
            @Override
            public void onEvent(RevokeMsgNotification message) {
                if (message == null && message.getMessage() == null) {
                    return;
                }

                MessageHelper.getInstance().onRevokeMessage(message.getMessage());
            }
        }, true);
    }

    // 初始化用户信息提供者
    private static void initUserInfoProvider(UserInfoProvider userInfoProvider) {

        if (userInfoProvider == null) {
            userInfoProvider = new DefalutUserInfoProvider(context);
        }

        IMApplication.userInfoProvider = userInfoProvider;
    }

    // 初始化联系人信息提供者
    private static void initContactProvider(ContactProvider contactProvider) {

        if (contactProvider == null) {
            contactProvider = new DefaultContactProvider();
        }

        IMApplication.contactProvider = contactProvider;
    }

    public static UserInfoProvider getUserInfoProvider() {
        return userInfoProvider;
    }

    public static ContactProvider getContactProvider() {
        return contactProvider;
    }

    public static ImageLoaderKit getImageLoaderKit() {
        return imageLoaderKit;
    }

    public static void setStatusBarNotificationConfig(StatusBarNotificationConfig statusBarNotificationConfig) {
        IMApplication.statusBarNotificationConfig = statusBarNotificationConfig;
    }

    public static StatusBarNotificationConfig getNotificationConfig() {
        return statusBarNotificationConfig;
    }
}
