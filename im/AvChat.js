/**
 * Created by dowin on 2017/8/2.
 */
import {NativeModules, Platform} from 'react-native'

const {AvChatSession} = NativeModules;

class AvChat {
    /**
     * 安卓点对点音视频
     */
    callAvChat = ({sessionId, sessionName, seesionAvatar, extend, chatType}) => {
        AvChatSession.callAvChat({sessionId, sessionName, seesionAvatar, extend, chatType})
    }

    /**
     * 挂断音视频通话
     */
    hangupAvChat = () => {
        AvChatSession.hangupAvChat({})
    }

    /**
     * 设置倒计时
     */
    setAvChatDuration = (duration) => {
        AvChatSession.setAvDuration({duration : duration})
    }

    /**
     * 费用不足native弹窗
     * @param duration
     */
    showRechargeAmountDialog = (duration) => {
        AvChatSession.showRechargeAmountDialog({})
    }
    /**
     * 创建多人视频房间
     */
    createRoom = (roomName, extraMessage, callback) => {
        try {
            AvChatSession.createRoom2(roomName, extraMessage, callback)
        } catch (e) {
        }
    }

    /**
     *
     * @param roomName
     * @param extraMessage
     * @param callback
     */
    createAndJoinRoom2 = (teamId, teamName, extraMessage, callback) => {
        try {
            AvChatSession.createAndJoinRoom2(teamId, teamName, extraMessage, callback)
        } catch (e) {
        }
    }

    mpvTeamCall = (roomName, extraMessage, callback) => {
        try {
            AvChatSession.mpvTeamCall(roomName, extraMessage, callback)
        } catch (e) {
        }
    }

    joinMpvRoom = (teamId, teamName, roomId, extraMessage, callback) => {
        try {
            AvChatSession.joinMpvRoom(teamId, teamName, roomId, extraMessage, callback)
        } catch (e) {
        }
    }

    doTest = (param1, extraMessage, callback) => {
        try {
            AvChatSession.doTest(param1, extraMessage, callback)
        } catch (e) {
        }
    }

    /**
     * 加入多人视频房间
     */
    joinRoom2 = (roomName, callback) => {
        AvChatSession.joinRoom2(roomName, callback)
    }

}

export default new AvChat()
