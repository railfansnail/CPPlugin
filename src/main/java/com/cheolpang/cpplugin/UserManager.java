package com.cheolpang.cpplugin;

import org.bukkit.entity.Player;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {
    private final Map<Player, String> nicknameMap = new ConcurrentHashMap<>();
    private final Map<Player, Boolean> agreedMap = new ConcurrentHashMap<>();

    public void setNickname(Player p, String nickname) {
        nicknameMap.put(p, nickname);
    }

    public String getNickname(Player p) {
        return nicknameMap.getOrDefault(p, p.getName());
    }

    public void setAgreed(Player p) {
        agreedMap.put(p, true);
    }

    public boolean hasAgreed(Player p) {
        return agreedMap.getOrDefault(p, false);
    }
}
