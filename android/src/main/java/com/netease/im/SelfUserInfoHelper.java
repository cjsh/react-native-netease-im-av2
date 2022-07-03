package com.netease.im;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.netease.im.login.LoginService;
import com.netease.im.uikit.cache.NimUserInfoCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.netease.yunxin.nertc.ui.base.UserInfoHelper;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

class SelfUserInfoHelper implements UserInfoHelper {
    @Override
    public boolean fetchNickname(@NotNull String accId, @NotNull Function1<? super String, Unit> function1) {
        function1.invoke(NimUserInfoCache.getInstance().getUserDisplayName(accId));
        if (NimUserInfoCache.getInstance().getUserDisplayName(accId) != null) {
            function1.invoke(NimUserInfoCache.getInstance().getUserDisplayName(accId));
        } else {
            NIMClient.getService(UserService.class).fetchUserInfo(Collections.singletonList(accId)).setCallback(new RequestCallbackWrapper<List<NimUserInfo>>() {
                @Override
                public void onResult(int code, List<NimUserInfo> result, Throwable exception) {
                    if (result == null || result.isEmpty()) {
                        function1.invoke(accId);
                        return;
                    }
                    function1.invoke(result.get(0).getMobile() + "");
                }
            });
        }
        return true;
    }

    @Override
    public boolean fetchNicknameByTeam(@NotNull String s, @NotNull String s1, @NotNull Function1<? super String, Unit> function1) {
        return false;
    }

    @Override
    public boolean loadAvatar(@NotNull Context context, @NotNull String s, @NotNull ImageView imageView) {
        return false;
    }
}
