package com.cheolpang.cpplugin.data;

import java.util.UUID;

public class UserData {

    private final UUID uuid;
    private String nickname;
    private boolean agreed;

    public UserData(UUID uuid, String nickname, boolean agreed) {
        this.uuid = uuid;
        this.nickname = nickname;
        this.agreed = agreed;
    }

    public UUID getUuid() { return uuid; }
    public String getNickname() { return nickname; }
    public boolean isAgreed() { return agreed; }

    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setAgreed(boolean agreed) { this.agreed = agreed; }
}
