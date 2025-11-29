package com.cheolpang.cpplugin.listener;

import com.cheolpang.cpplugin.Main;
import com.cheolpang.cpplugin.data.UserData;
import com.cheolpang.cpplugin.data.UserDatabase;
import com.cheolpang.cpplugin.gui.AgreementGUI;

import com.cheolpang.cpplugin.util.PlayerBlocker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final UserDatabase db;
    private final PlayerBlocker blocker;

    public JoinListener(UserDatabase db, PlayerBlocker blocker) {
        this.db = db;
        this.blocker = blocker;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UserData ud = db.getOrCreate(p.getUniqueId());

        // 항상 봉인 후 체크 (최초 접속 또는 미완료 상태 강제 처리)
        blocker.block(p);

        if (!ud.hasAgreed()) {
            new AgreementGUI(p, db).open();
            return;
        }


        // 이미 모두 완료면 봉인 해제
        blocker.unblock(p);
    }
}
