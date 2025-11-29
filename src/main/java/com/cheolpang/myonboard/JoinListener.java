package com.cheolpang.cpplugin.listeners;

import com.cheolpang.cpplugin.data.UserData;
import com.cheolpang.cpplugin.data.UserDatabase;
import com.cheolpang.cpplugin.gui.AgreementGUI;
import com.cheolpang.cpplugin.gui.NicknameGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final UserDatabase db;

    public JoinListener(UserDatabase db) {
        this.db = db;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var p = e.getPlayer();
        UserData user = db.getUser(p.getUniqueId());

        if (!user.isAgreed()) {
            p.sendMessage("§c규칙에 동의해야 게임 이용 가능함");
            new AgreementGUI(p, db, user).open();
            return;
        }

        if (user.getNickname() == null) {
            p.sendMessage("§e닉네임 설정 먼저 해라잉");
            new NicknameGUI(p, db, user).open();
            return;
        }
    }
}
