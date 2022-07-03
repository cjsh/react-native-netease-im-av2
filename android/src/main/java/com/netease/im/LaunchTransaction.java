package com.netease.im;
import java.io.Serializable;

public class LaunchTransaction implements Serializable {
    private String teamID;
    private String roomName;

    public String getRoomName() {
        return roomName;
    }

    public String getTeamID() {
        return teamID;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }
}
