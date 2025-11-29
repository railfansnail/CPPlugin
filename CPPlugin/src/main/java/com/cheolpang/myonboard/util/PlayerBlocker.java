package com.cheolpang.cpplugin.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerBlocker implements Listener {

    private final Set<UUID> blocked = ConcurrentHashMap.newKeySet();

    public void block(Player p) {
        blocked.add(p.getUniqueId());
        // 플레이어를 고정시킬 수도 있지만, 이벤트에서 이동을 취소함
    }

    public void unblock(Player p) {
        blocked.remove(p.getUniqueId());
    }

    public boolean isBlocked(Player p) {
        return blocked.contains(p.getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!blocked.contains(e.getPlayer().getUniqueId())) return;

        Location from = e.getFrom();
        Location to = e.getTo();
        if (to == null) return;
        // 블록 단위로 이동하려 할 때만 막음 (미세한 회전은 허용)
        if (from.getBlockX() != to.getBlockX() ||
            from.getBlockY() != to.getBlockY() ||
            from.getBlockZ() != to.getBlockZ()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!blocked.contains(e.getPlayer().getUniqueId())) return;
        // 허용할 액션이 있다면 조건 추가
        e.setCancelled(true);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!blocked.contains(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
        e.getPlayer().sendMessage("§c동의 및 닉네임 설정을 완료해야 명령어 사용 가능함.");
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (!blocked.contains(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!blocked.contains(p.getUniqueId())) return;
        e.setCancelled(true);
    }

    // 채팅은 Conversation에서 사용하므로 그대로 놔둠 (AsyncPlayerChatEvent는 차단하지 않음)
}
