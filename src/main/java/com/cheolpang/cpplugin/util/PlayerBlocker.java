package com.cheolpang.cpplugin.util;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerBlocker implements Listener {

    private final Set<UUID> blocked = ConcurrentHashMap.newKeySet();

    public PlayerBlocker() {
        // DB 참조 불필요 → 필요 시 getter 통해 별도 접근 가능
    }

    // 플레이어 행동 차단
    public void block(Player p) {
        blocked.add(p.getUniqueId());
    }

    // 차단 해제
    public void unblock(Player p) {
        blocked.remove(p.getUniqueId());
    }

    // 차단 여부 확인
    public boolean isBlocked(Player p) {
        return blocked.contains(p.getUniqueId());
    }

    // 이동 막기
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (blocked.contains(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    // 아이템 드롭 막기
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (blocked.contains(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }
}
