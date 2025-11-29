package com.cheolpang.cpplugin.data;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserDatabase {

    private final Map<UUID, UserData> users = new HashMap<>();
    private final File file;
    private final YamlConfiguration config;

    public UserDatabase(File pluginFolder) {
        this.file = new File(pluginFolder, "users.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        loadAll();
    }

    private void loadAll() {
        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                boolean agreed = config.getBoolean(key + ".agreed", false);
                UserData data = new UserData(uuid);
                data.setAgreed(agreed);
                users.put(uuid, data);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public UserData get(UUID uuid) {
        return users.get(uuid);
    }

    public UserData getOrCreate(UUID uuid) {
        return users.computeIfAbsent(uuid, UserData::new);
    }

    public void save(UserData data) {
        users.put(data.getUuid(), data);
        config.set(data.getUuid().toString() + ".agreed", data.hasAgreed());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
