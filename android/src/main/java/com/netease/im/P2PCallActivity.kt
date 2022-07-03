package com.netease.im

import android.Manifest
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PermissionUtils.FullCallback
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.netease.lava.nertc.sdk.NERtc
import com.netease.lava.nertc.sdk.NERtcConstants.ErrorCode.ENGINE_ERROR_DEVICE_PREVIEW_ALREADY_STARTED
import com.netease.lava.nertc.sdk.NERtcEx
import com.netease.lava.nertc.sdk.video.NERtcVideoConfig
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType
import com.netease.nimlib.sdk.avsignalling.model.ChannelFullInfo
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.yunxin.kit.alog.ALog
import com.netease.yunxin.nertc.nertcvideocall.model.CallErrorCode
import com.netease.yunxin.nertc.nertcvideocall.model.JoinChannelCallBack
import com.netease.yunxin.nertc.nertcvideocall.model.NERTCCallingDelegate
import com.netease.yunxin.nertc.nertcvideocall.model.impl.state.CallState
import com.netease.yunxin.nertc.ui.base.*
import com.netease.yunxin.nertc.ui.p2p.NERtcCallDelegateForP2P
import com.netease.yunxin.nertc.ui.utils.PermissionTipDialog
import com.netease.yunxin.nertc.ui.utils.SecondsTimer
import com.netease.yunxin.nertc.ui.utils.dip2Px
import com.netease.yunxin.nertc.ui.utils.formatSecondTime
import kotlinx.android.synthetic.main.activity_p2p_call.*

open class P2PCallActivity : CommonCallActivity() {
    private val tag = "P2PCallActivity"

    val VIDEO_CALL_CODE = "VIDEO_CALL_CODE"
    val VIDEO_CALL_SUCCESS = "VIDEO_CALL_SUCCESS"
    private val typeAudio = ChannelType.AUDIO.value
    private val typeVideo = ChannelType.VIDEO.value

    private val timer = SecondsTimer()

    private var startPreviewCode = -1

    private var callFinished = true

    fun sendAction(status: Int) {
        if (YnEventManager.instance != null) {
            YnEventManager.instance.onReceiveAvCode(
                VIDEO_CALL_CODE,
                status.toString()
            )
        }
    }

    private val delegate = object : NERtcCallDelegateForP2P() {

        override fun onFirstVideoFrameDecoded(userId: String?, width: Int, height: Int) {
            super.onFirstVideoFrameDecoded(userId, width, height)
            userId?.run {
                initForOnTheCall(this)
            }
        }

        override fun onError(errorCode: Int, errorMsg: String?, needFinish: Boolean) {
            super.onError(errorCode, errorMsg, needFinish)
            if (errorCode == CallErrorCode.OTHER_CLIENT_ACCEPT || errorCode == CallErrorCode.OTHER_CLIENT_REJECT) {
                errorMsg?.run {
                    sendAction(AVChatExitCode.NET_ERROR)
                    ToastUtils.showShort(this)
                }
            }
        }

        override fun onUserEnter(userId: String?) {
            super.onUserEnter(userId)
            if (YnEventManager.instance != null) {
                YnEventManager.instance.onAvConnectionSuccess(VIDEO_CALL_SUCCESS)
            }
            AVChatSoundPlayer.instance().stop(this@P2PCallActivity)
            if (callParam.channelType == typeAudio) {
                initForOnTheCall(userId)
            }
            timer.start {
                runOnUiThread { tvCountdown.text = it.formatSecondTime() }
            }
        }

        override fun onCallEnd(userId: String?) {
            super.onCallEnd(userId)
            sendAction(AVChatExitCode.INVALIDE_CHANNELID)
            AVChatSoundPlayer.instance().stop(this@P2PCallActivity)
        }

        override fun onCallFinished(code: Int?, msg: String?) {
            super.onCallFinished(code, msg)
            releaseAndFinish(false)
        }

        override fun onRejectByUserId(userId: String?) {
            if (!isDestroyed && !callParam.isCalled) {
                sendAction(AVChatExitCode.REJECT)
                ToastUtils.showShort("对方已经拒绝")
                AVChatSoundPlayer.instance()
                    .play(this@P2PCallActivity, AVChatSoundPlayer.RingerTypeEnum.PEER_REJECT)
            }
            super.onRejectByUserId(userId)
        }

        override fun onUserBusy(userId: String?) {
            if (!isDestroyed && !callParam.isCalled) {
                sendAction(AVChatExitCode.PEER_BUSY)
                ToastUtils.showShort("对方占线")
                AVChatSoundPlayer.instance()
                    .play(this@P2PCallActivity, AVChatSoundPlayer.RingerTypeEnum.PEER_BUSY)
            }
            super.onUserBusy(userId)
        }

        override fun onCancelByUserId(userId: String?) {
            if (!isDestroyed && callParam.isCalled) {
                sendAction(AVChatExitCode.INVALIDE_CHANNELID)
                ToastUtils.showShort("对方取消")
                AVChatSoundPlayer.instance().stop(this@P2PCallActivity)
            }
            super.onCancelByUserId(userId)
        }

        override fun onDisconnect(res: Int) {
            super.onDisconnect(res)
            AVChatSoundPlayer.instance().stop(this@P2PCallActivity)
        }

        override fun timeOut() {
            super.timeOut()
            ToastUtils.showShort("对方超时未响应")
            AVChatSoundPlayer.instance()
                .play(this@P2PCallActivity, AVChatSoundPlayer.RingerTypeEnum.NO_RESPONSE)
        }

        override fun onCallTypeChange(type: ChannelType?) {
            super.onCallTypeChange(type)
            type ?: return
            callParam.channelType = type.value
            videoCall.enableLocalVideo(false)
            if (videoCall.currentState != CallState.STATE_DIALOG) {
                if (callParam.isCalled) {
                    uiRender.renderForCalled()
                } else {
                    uiRender.renderForCaller()
                }
                return
            }

            initForOnTheCall(if (callParam.isCalled) callParam.callerAccId else callParam.p2pCalledAccId)
        }

        override fun onVideoMuted(userId: String?, isMuted: Boolean) {
            super.onVideoMuted(userId, isMuted)
            uiRender.updateOnTheCallState(
                UserState(
                    userId,
                    muteVideo = isMuted
                )
            )
        }

        override fun onJoinChannel(
            accId: String?,
            uid: Long,
            channelName: String?,
            rtcChannelId: Long
        ) {
            super.onJoinChannel(accId, uid, channelName, rtcChannelId)
            this@P2PCallActivity.onJoinChannel(accId, uid, channelName, rtcChannelId)
        }
    }

    private val onClickListener = View.OnClickListener { v ->
        when (v) {
            ivAccept -> {
                v.isEnabled = false
                ivReject.isEnabled = false
                doAccept()
            }
            ivReject -> {
                ivAccept.isEnabled = false
                v.isEnabled = false
                doReject()
            }
            ivCancel -> {
                if (!callFinished) {
                    ToastUtils.showShort("呼叫未成功发出")
                    return@OnClickListener
                }
                v.isEnabled = false
                doCancel()
            }
            ivHangUp -> {
                v.isEnabled = false
                doHangup()
            }
            ivCallMuteAudio,
            ivMuteAudio -> doMuteAudioSwitch(v as ImageView)
            ivMuteVideo -> doMuteVideo(ivMuteVideo)
            ivSwitchCamera -> doSwitchCamera()
            ivCallSwitchType,
            ivSwitchType,
            ivCallChannelTypeChange -> doSwitchCallType()
            ivCallSpeaker,
            ivMuteSpeaker -> doConfigSpeakerSwitch(v as ImageView)
            else -> ALog.d("can't response this clicked Event for $v")
        }
    }

    private val uiRender: UIRender
        get() = if (callParam.channelType == typeAudio) {
            AudioRender()
        } else {
            VideoRender()
        }

    override fun doOnCreate(savedInstanceState: Bundle?) {
        super.doOnCreate(savedInstanceState)
        ALog.e(tag, callParam.toString())
        initForLaunchUI()
        var dialog: PermissionTipDialog? = null
        if (!PermissionUtils.isGranted(
                PermissionConstants.CAMERA,
                PermissionConstants.MICROPHONE
            )
        ) {
            dialog = showPermissionDialog {
                ToastUtils.showShort("权限申请失败，暂无法使用！")
                releaseAndFinish(true)
            }
        }

        PermissionUtils.permission(
            PermissionConstants.CAMERA,
            PermissionConstants.MICROPHONE
        ).callback(object : FullCallback {
            override fun onGranted(granted: List<String>) {
                if (isFinishing || isDestroyed) {
                    return
                }
                granted.forEach {
                    ALog.i("onGranted:$it")
                }
                if (granted.containsAll(
                        listOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO
                        )
                    )
                ) {
                    dialog?.dismiss()
                    if (callParam.isCalled && videoCall.currentState == CallState.STATE_IDLE) {
                        releaseAndFinish(false)
                        return
                    }
                    initForLaunchAction()
                }
                ALog.i("extra info is ${callParam.callExtraInfo}")
            }

            override fun onDenied(deniedForever: List<String>, denied: List<String>) {
                denied.forEach {
                    ALog.i("onDenied:$it")
                }
                ToastUtils.showShort("权限申请失败，暂无法使用！")
                releaseAndFinish(true)
            }
        }).request()
    }


    override fun provideLayoutId(): Int = R.layout.activity_p2p_call

    override fun provideRtcDelegate(): NERTCCallingDelegate = delegate

    override fun releaseAndFinish(finishCall: Boolean) {
        super.releaseAndFinish(finishCall)
        AVChatSoundPlayer.instance().stop(AVChatSoundPlayer.RingerTypeEnum.RING, this)
        AVChatSoundPlayer.instance().stop(AVChatSoundPlayer.RingerTypeEnum.CONNECTING, this)

        if (startPreviewCode == 0 || startPreviewCode == ENGINE_ERROR_DEVICE_PREVIEW_ALREADY_STARTED) {
            NERtcEx.getInstance().stopVideoPreview()
        }

        if (finishCall) {
            doHangup(null)
        }
    }

    override fun onBackPressed() {
        showExitDialog()
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            AVChatSoundPlayer.instance().stop(AVChatSoundPlayer.RingerTypeEnum.RING, this)
            AVChatSoundPlayer.instance().stop(AVChatSoundPlayer.RingerTypeEnum.CONNECTING, this)
            releaseAndFinish(true)
            timer.cancel()
        }
    }

    private fun showExitDialog() {
        val confirmDialog = AlertDialog.Builder(this)
        confirmDialog.setTitle("结束通话")
        confirmDialog.setMessage("是否结束通话？")
        confirmDialog.setPositiveButton(
            "是"
        ) { _: DialogInterface?, _: Int ->
            if (!callFinished) {
                ToastUtils.showShort("呼叫未成功发出")
                return@setPositiveButton
            }
            finish()
        }
        confirmDialog.setNegativeButton(
            "否"
        ) { _: DialogInterface?, _: Int -> }
        confirmDialog.show()
    }

    private fun initForLaunchUI() {
        if (callParam.isCalled) {
            // 主叫页面初始化
            uiRender.renderForCalled()
        } else {
            // 被叫页面初始化
            uiRender.renderForCaller()
        }
    }

    private fun initForLaunchAction() {
        if (!callParam.isCalled) {
            doCall()
            if (callParam.channelType == ChannelType.VIDEO.value) {
                startPreviewCode = NERtcEx.getInstance().startVideoPreview()
            }
        } else {
            AVChatSoundPlayer.instance().play(this, AVChatSoundPlayer.RingerTypeEnum.RING)
        }
    }

    /**
     * 通话中页面初始化
     */
    private fun initForOnTheCall(userAccId: String? = null) {
        uiRender.renderForOnTheCall(userAccId)
    }

    private fun doCall() {
        callFinished = false
        doCall(object : JoinChannelCallBack {
            override fun onJoinChannel(channelFullInfo: ChannelFullInfo?) {
                callFinished = true
            }

            override fun onJoinFail(msg: String?, code: Int) {
                ToastUtils.showShort("呼叫发起失败")
            }
        })

        AVChatSoundPlayer.instance().play(this, AVChatSoundPlayer.RingerTypeEnum.CONNECTING)
    }

    private fun doCancel() {
        doCancel(object : RequestCallbackWrapper<Void>() {
            override fun onResult(code: Int, result: Void?, exception: Throwable?) {
                // 10410 邀请已经接受了
                if (code == ResponseCode.RES_INVITE_HAS_ACCEPT.toInt()) {
                    return
                }
                AVChatSoundPlayer.instance().stop(this@P2PCallActivity)
            }
        })
    }

    private fun doAccept() {
        if (tvConnectingTip.tag != true) {
            tvConnectingTip.tag = true
            tvConnectingTip.visibility = View.VISIBLE
        }
        AVChatSoundPlayer.instance().stop(this)
        doAccept(object : JoinChannelCallBack {
            override fun onJoinChannel(channelFullInfo: ChannelFullInfo?) {
                if (YnEventManager.instance != null) {
                    YnEventManager.instance.onAvConnectionSuccess(VIDEO_CALL_SUCCESS)
                }
            }

            override fun onJoinFail(msg: String?, code: Int) {
                ToastUtils.showShort("接听失败")
            }
        })
    }

    private fun doReject() {
        AVChatSoundPlayer.instance().stop(this)
        doReject(null)
    }

    private fun doHangup() {
        sendAction(AVChatExitCode.HANGUP)
        releaseAndFinish(true)
    }

    private fun doMuteVideo(view: ImageView) {
        super.doMuteVideo()
        view.setImageResource(if (isLocalMuteVideo) R.drawable.cam_off else R.drawable.cam_on)
        uiRender.updateOnTheCallState(
            UserState(
                callParam.currentAccId!!,
                muteVideo = isLocalMuteVideo
            )
        )
    }

    private fun doConfigSpeakerSwitch(
        view: ImageView? = null,
        speakerEnable: Boolean = !isSpeakerOn()
    ) {
        doConfigSpeaker(speakerEnable)
        ivMuteSpeaker.setImageResource(if (speakerEnable) R.drawable.speaker_on else R.drawable.speaker_off)
        ivCallSpeaker.setImageResource(if (speakerEnable) R.drawable.icon_call_audio_speaker_on else R.drawable.icon_call_audio_speaker_off)
    }

    private fun doMuteAudioSwitch(view: ImageView? = null) {
        super.doMuteAudio()
        ivMuteAudio.setImageResource(if (isLocalMuteAudio) R.drawable.voice_off else R.drawable.voice_on)
        ivCallMuteAudio.setImageResource(if (isLocalMuteAudio) R.drawable.icon_call_audio_off else R.drawable.icon_call_audio_on)
    }

    private fun doSwitchCallType() {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort("网络异常，请稍后重试")
            return
        }
        val toChannelType = if (callParam.channelType == typeVideo) {
            ChannelType.AUDIO
        } else {
            ChannelType.VIDEO
        }

        doSwitchCallType(toChannelType, object : RequestCallbackWrapper<Void>() {
            override fun onResult(code: Int, result: Void?, exception: Throwable?) {
                if (code == ResponseCode.RES_SUCCESS.toInt()) {
                    if (toChannelType.value == typeAudio) {
                        videoCall.enableLocalVideo(false)
                    } else {
                        videoCall.enableLocalVideo(true)
                    }
                    if (videoCall.currentState == CallState.STATE_DIALOG) {
                        initForOnTheCall(if (callParam.isCalled) callParam.callerAccId else callParam.p2pCalledAccId)
                        return
                    }
                    if (callParam.isCalled) {
                        uiRender.renderForCalled()
                    } else {
                        uiRender.renderForCaller()
                    }
                } else {
                    ToastUtils.showShort("切换失败")
                }
            }
        })
    }

    private open inner class UIRender {
        open fun renderForCaller() {
            calledSwitchGroup.visibility = View.GONE
            if (videoCall.isEnableAutoJoinWhenCalled) {
                callerSwitchGroup.visibility = View.VISIBLE
            } else {
                callerSwitchGroup.visibility = View.INVISIBLE
            }
        }

        open fun renderForCalled() {
            callerSwitchGroup.visibility = View.GONE
            if (videoCall.isEnableAutoJoinWhenCalled) {
                calledSwitchGroup.visibility = View.VISIBLE
            } else {
                calledSwitchGroup.visibility = View.GONE
            }
        }

        open fun renderForOnTheCall(userAccId: String? = null) {
            callerSwitchGroup.visibility = View.GONE
            calledSwitchGroup.visibility = View.GONE
        }

        open fun updateOnTheCallState(state: UserState) {}
    }

    private inner class AudioRender : UIRender() {
        override fun renderForCaller() {
            super.renderForCaller()
            forUserInfoUI(typeAudio, callParam.p2pCalledAccId, forVideoCaller = true)
            doConfigSpeakerSwitch(speakerEnable = false)

            if (isLocalMuteAudio) {
                doMuteAudioSwitch()
            }

            ivBg.visibility = View.VISIBLE
            tvCallSwitchTypeDesc.text = "切换到视频通话"

            videoViewRemote.visibility = View.GONE
            videoViewLocalBig.visibility = View.GONE
            videoViewLocalSmall.visibility = View.GONE

            llOnTheCallOperation.visibility = View.GONE
            calledOperationGroup.visibility = View.GONE
            callerOperationGroup.visibility = View.VISIBLE
            callerAudioOperationGroup.visibility = View.VISIBLE

            ivCancel.setOnClickListener(onClickListener)
            ivCallSwitchType.setOnClickListener(onClickListener)
            ivCallSwitchType.setImageResource(R.drawable.icon_call_tip_audio_to_video)
            ivCallChannelTypeChange.setOnClickListener(onClickListener)

            ivCallMuteAudio.setOnClickListener(onClickListener)
            ivCallSpeaker.setOnClickListener(onClickListener)
            ivBg.visibility = View.VISIBLE
            if (startPreviewCode == 0 || startPreviewCode == ENGINE_ERROR_DEVICE_PREVIEW_ALREADY_STARTED) {
                startPreviewCode = if (NERtcEx.getInstance().stopVideoPreview() == 0) -1 else 0
            }
        }

        override fun renderForCalled() {
            super.renderForCalled()
            forUserInfoUI(typeAudio, callParam.callerAccId)

            ivAccept.setImageResource(R.drawable.icon_call_audio_accept)
            ivSwitchType.setImageResource(R.drawable.icon_call_tip_audio_to_video)
            tvOtherCallTip.text = "邀请您语音通话......"
            tvSwitchTypeDesc.text = "切换到视频通话"

            videoViewLocalBig.visibility = View.GONE
            videoViewRemote.visibility = View.GONE
            videoViewLocalSmall.visibility = View.GONE

            llOnTheCallOperation.visibility = View.GONE
            calledOperationGroup.visibility = View.VISIBLE
            callerOperationGroup.visibility = View.GONE
            callerAudioOperationGroup.visibility = View.GONE

            ivAccept.setOnClickListener(onClickListener)
            ivReject.setOnClickListener(onClickListener)
            ivSwitchType.setOnClickListener(onClickListener)
            ivBg.visibility = View.VISIBLE
        }

        override fun renderForOnTheCall(userAccId: String?) {
            super.renderForOnTheCall(userAccId)

            callParam.run {
                forUserInfoUI(typeAudio, if (isCalled) callerAccId else p2pCalledAccId)
            }
            NERtcEx.getInstance().adjustRecordingSignalVolume(400)
            NERtcEx.getInstance().adjustPlaybackSignalVolume(400)

            tvOtherCallTip.text = "正在通话中"

            tvConnectingTip.visibility = View.GONE
            videoViewLocalBig.visibility = View.GONE
            videoViewLocalSmall.visibility = View.GONE
            videoViewRemote.visibility = View.GONE

            calledOperationGroup.visibility = View.GONE
            callerOperationGroup.visibility = View.GONE
            callerAudioOperationGroup.visibility = View.GONE
            llOnTheCallOperation.visibility = View.VISIBLE
            tvCountdown.visibility = View.VISIBLE

            ivCallChannelTypeChange.setImageResource(R.drawable.audio_to_video)
            ivCallChannelTypeChange.setOnClickListener(onClickListener)
            ivMuteAudio.setOnClickListener(onClickListener)
            ivMuteVideo.visibility = View.GONE
            ivHangUp.setOnClickListener(onClickListener)
            ivMuteSpeaker.setOnClickListener(onClickListener)

            ivSwitchCamera.visibility = View.GONE

            tvRemoteVideoCloseTip.visibility = View.GONE
            if (!firstLaunch || callParam.isCalled) {
                resetSwitchState(typeAudio)
            } else {
                firstLaunch = false
            }
            ivBg.visibility = View.VISIBLE
        }
    }

    private var firstLaunch = true

    private inner class VideoRender : UIRender() {
        override fun renderForCaller() {
            super.renderForCaller()
            forUserInfoUI(typeVideo, callParam.p2pCalledAccId, forVideoCaller = true)

            videoViewRemote.visibility = View.GONE
            videoViewLocalBig.visibility = View.VISIBLE
            videoViewLocalSmall.visibility = View.GONE

            llOnTheCallOperation.visibility = View.GONE
            calledOperationGroup.visibility = View.GONE
            callerOperationGroup.visibility = View.VISIBLE
            callerAudioOperationGroup.visibility = View.GONE
            ivCancel.setOnClickListener(onClickListener)
            ivCallSwitchType.setOnClickListener(onClickListener)
            ivCallSwitchType.setImageResource(R.drawable.icon_call_tip_video_to_audio)
            tvCallSwitchTypeDesc.text = "切换到语音通话"

            NERtcVideoConfig().apply {
                frontCamera = true
                NERtc.getInstance().setLocalVideoConfig(this)
            }

            videoCall.setupLocalView(videoViewLocalBig)
            if (startPreviewCode != 0 && startPreviewCode != ENGINE_ERROR_DEVICE_PREVIEW_ALREADY_STARTED) {
                startPreviewCode = NERtcEx.getInstance().startVideoPreview()
            }
            ivBg.visibility = View.GONE
        }

        override fun renderForCalled() {
            super.renderForCalled()

            forUserInfoUI(typeVideo, callParam.callerAccId)

            videoViewLocalBig.visibility = View.GONE
            videoViewRemote.visibility = View.GONE
            videoViewLocalSmall.visibility = View.GONE

            llOnTheCallOperation.visibility = View.GONE
            calledOperationGroup.visibility = View.VISIBLE
            callerOperationGroup.visibility = View.GONE
            callerAudioOperationGroup.visibility = View.GONE
            tvOtherCallTip.text = "邀请您视频通话......"
            tvSwitchTypeDesc.text = "切换到语音通话"

            ivAccept.setImageResource(R.drawable.call_accept)
            ivSwitchType.setImageResource(R.drawable.icon_call_tip_video_to_audio)

            ivAccept.setOnClickListener(onClickListener)
            ivReject.setOnClickListener(onClickListener)
            ivSwitchType.setOnClickListener(onClickListener)

            NERtcVideoConfig().apply {
                frontCamera = true
                NERtc.getInstance().setLocalVideoConfig(this)
            }
            ivBg.visibility = View.VISIBLE
        }

        override fun renderForOnTheCall(userAccId: String?) {
            super.renderForOnTheCall(userAccId)

            forUserInfoUI(type = typeVideo, visible = false)
            NERtcEx.getInstance().adjustRecordingSignalVolume(400)
            NERtcEx.getInstance().adjustPlaybackSignalVolume(400)

            tvConnectingTip.visibility = View.GONE
            videoViewLocalBig.visibility = View.GONE
            videoViewLocalSmall.visibility = View.VISIBLE
            videoViewRemote.visibility = View.VISIBLE

            calledOperationGroup.visibility = View.GONE
            callerOperationGroup.visibility = View.GONE
            callerAudioOperationGroup.visibility = View.GONE
            llOnTheCallOperation.visibility = View.VISIBLE
            tvCountdown.visibility = View.VISIBLE

            ivCallChannelTypeChange.setOnClickListener(onClickListener)
            ivCallChannelTypeChange.setImageResource(R.drawable.video_to_audio)
            ivMuteAudio.setOnClickListener(onClickListener)
            ivMuteVideo.visibility = View.VISIBLE
            ivMuteVideo.setOnClickListener(onClickListener)
            ivHangUp.setOnClickListener(onClickListener)
            ivMuteSpeaker.setOnClickListener(onClickListener)
            resetSwitchState(typeVideo)
            ivBg.visibility = View.GONE

            firstLaunch = false
            ivSwitchCamera.run {
                visibility = View.VISIBLE
                setOnClickListener(onClickListener)
            }
            if (callParam.currentUserIsCaller()) {
                NERtcEx.getInstance().stopVideoPreview()
            }
            videoCall.setupRemoteView(videoViewRemote, userAccId)
            videoViewLocalBig.release()
            videoCall.setupLocalView(videoViewLocalSmall)
        }

        override fun updateOnTheCallState(state: UserState) {
            super.updateOnTheCallState(state)

            if (TextUtils.equals(state.userAccId, callParam.currentAccId)) {
                state.muteVideo?.run {
                    videoViewLocalSmall.setBackgroundColor(if (this) Color.BLACK else Color.TRANSPARENT)
                }
            } else {
                state.muteVideo?.run {
                    var avatar = NIMClient.getService(UserService::class.java).getUserInfo(state.userAccId).avatar
                    if (this) {
                        Glide.with(this@P2PCallActivity).load(avatar).into(ivBg)
                    }
                    ivBg.visibility = if (this) View.VISIBLE else View.GONE
                    videoViewRemote.visibility = if (this) View.GONE else View.VISIBLE
                    tvRemoteVideoCloseTip.visibility = if (this) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun forUserInfoUI(
        type: Int,
        accId: String? = null,
        visible: Boolean = true,
        forVideoCaller: Boolean = false
    ) {
        if (!visible) {
            userInfoGroup.visibility = View.GONE
            return
        }
        userInfoGroup.visibility = View.VISIBLE

        accId?.run {
            fetchNickname {
                tvUserName.text = it
            }
            loadAvatarByAccId(this@P2PCallActivity, ivUserAvatar, ivBg)
        }
        val centerSize = 97.dip2Px(this)
        val topSize = 60.dip2Px(this)

        val constraintSet = ConstraintSet()
        constraintSet.clone(clRoot)
        constraintSet.clear(R.id.ivUserAvatar)
        constraintSet.clear(R.id.tvOtherCallTip)
        constraintSet.clear(R.id.tvUserName)
        constraintSet.constrainHeight(R.id.tvOtherCallTip, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainWidth(R.id.tvOtherCallTip, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainWidth(R.id.tvUserName, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainWidth(R.id.tvUserName, ConstraintSet.WRAP_CONTENT)

        if (type == typeVideo && forVideoCaller) {
            val marginSize16 = 16.dip2Px(this)
            ivUserAvatar.run {
                constraintSet.constrainWidth(id, topSize)
                constraintSet.constrainHeight(id, topSize)
                constraintSet.connect(
                    id,
                    ConstraintSet.END,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.END,
                    marginSize16
                )
                constraintSet.connect(
                    id,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP,
                    marginSize16
                )
            }
            val marginSize10 = 10.dip2Px(this)
            val marginSize5 = 5.dip2Px(this)
            tvUserName.run {
                textSize = 18f
                constraintSet.connect(
                    id,
                    ConstraintSet.END,
                    ivUserAvatar.id,
                    ConstraintSet.START,
                    marginSize10
                )
                constraintSet.connect(
                    id,
                    ConstraintSet.TOP,
                    ivUserAvatar.id,
                    ConstraintSet.TOP,
                    marginSize5
                )
            }
            tvOtherCallTip.run {
                setTextColor(ContextCompat.getColor(context, R.color.white))
                constraintSet.connect(
                    id,
                    ConstraintSet.TOP,
                    tvUserName.id,
                    ConstraintSet.BOTTOM,
                    marginSize5
                )
                constraintSet.connect(
                    id,
                    ConstraintSet.END,
                    ivUserAvatar.id,
                    ConstraintSet.START,
                    marginSize10
                )
            }
        } else {
            ivUserAvatar.run {
                constraintSet.constrainWidth(id, centerSize)
                constraintSet.constrainHeight(id, centerSize)
                constraintSet.connect(
                    id,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP,
                    160.dip2Px(context)
                )
                constraintSet.centerHorizontally(id, ConstraintSet.PARENT_ID)
            }
            tvUserName.run {
                textSize = 20f
                constraintSet.centerHorizontally(id, ConstraintSet.PARENT_ID)
                constraintSet.connect(
                    id,
                    ConstraintSet.TOP,
                    ivUserAvatar.id,
                    ConstraintSet.BOTTOM,
                    15.dip2Px(context)
                )
            }
            tvOtherCallTip.run {
                setTextColor(ContextCompat.getColor(context, R.color.color_cccccc))
                constraintSet.connect(
                    id,
                    ConstraintSet.TOP,
                    tvUserName.id,
                    ConstraintSet.BOTTOM,
                    8.dip2Px(context)
                )
                constraintSet.centerHorizontally(id, ConstraintSet.PARENT_ID)
            }
        }
        constraintSet.applyTo(clRoot)
    }

    private fun resetSwitchState(channelType: Int) {
        if (channelType == typeVideo) {
            doConfigSpeaker(true)
            ivMuteSpeaker.setImageResource(R.drawable.speaker_on)
        } else {
            doConfigSpeaker(false)
            ivMuteSpeaker.setImageResource(R.drawable.speaker_off)
        }
        // 视频
        isLocalMuteVideo = false
        videoCall.muteLocalVideo(false)
        ivMuteVideo.setImageResource(R.drawable.cam_on)
        tvRemoteVideoCloseTip.visibility = View.GONE
        videoViewLocalSmall.setBackgroundColor(Color.TRANSPARENT)
        // 音频
        if (isLocalMuteAudio) {
            doMuteAudioSwitch(ivMuteAudio)
        }
    }

    class UserState(
        val userAccId: String?,
        val muteVideo: Boolean? = null
    )
}