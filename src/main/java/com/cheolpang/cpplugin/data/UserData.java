package com.cheolpang.cpplugin.data;

import java.util.UUID;

public class UserData {
    private final UUID uuid;
    private boolean agreed;

    public UserData(UUID uuid) {
        this.uuid = uuid;
        this.agreed = false;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean hasAgreed() {
        return agreed;
    }

    public void setAgreed(boolean agreed) {
        this.agreed = agreed;
    }
}
