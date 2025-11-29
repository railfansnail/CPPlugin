package com.cheolpang.cpplugin;

import org.bukkit.plugin.java.JavaPlugin;
import com.cheolpang.cpplugin.data.SQLite;
import com.cheolpang.cpplugin.data.UserDatabase;
import com.cheolpang.cpplugin.listeners.JoinListener;
import com.cheolpang.cpplugin.util.PlayerBlocker;

public class Main extends JavaPlugin {

    private static Main instance;
    private SQLite sqlite;
    private UserDatabase userDatabase;
    private PlayerBlocker blocker;

    @Override
    public void onEnable() {
        instance = this;

        // DB 초기화
        sqlite = new SQLite(this);
        sqlite.connect();
        sqlite.createTables();
        userDatabase = new UserDatabase(sqlite);

        // 플레이어 행동 블로커 생성 및 등록
        blocker = new PlayerBlocker();
        getServer().getPluginManager().registerEvents(blocker, this);

        // 조인 리스너 등록
        getServer().getPluginManager().registerEvents(new JoinListener(userDatabase), this);

        getLogger().info("CPPlugin 활성화됨");
    }

    @Override
    public void onDisable() {
        sqlite.disconnect();
    }

    public static Main getInstance() {
        return instance;
    }

    public UserDatabase getUserDatabase() {
        return userDatabase;
    }

    public PlayerBlocker getBlocker() {
        return blocker;
    }
}
