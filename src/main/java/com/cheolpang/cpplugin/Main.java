package com.cheolpang.cpplugin;

import org.bukkit.plugin.java.JavaPlugin;
import com.cheolpang.cpplugin.data.UserDatabase;
import com.cheolpang.cpplugin.util.PlayerBlocker;
import com.cheolpang.cpplugin.listener.JoinListener;

import java.io.File;

public class Main extends JavaPlugin {

    private static Main instance;
    private UserDatabase userDatabase;
    private PlayerBlocker blocker;

    @Override
    public void onEnable() {
        instance = this;

        // 데이터 폴더 생성
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        // 유저 DB 생성
        userDatabase = new UserDatabase(getDataFolder());

        // 플레이어 블로커 생성 (DB 필요 없음)
        blocker = new PlayerBlocker();
        getServer().getPluginManager().registerEvents(blocker, this);

        // 접속 리스너 등록
        getServer().getPluginManager().registerEvents(new JoinListener(userDatabase, blocker), this);

        getLogger().info("CPPlugin Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("CPPlugin Disabled!");
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
