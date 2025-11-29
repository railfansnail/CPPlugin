package com.cheolpang.cpplugin.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDatabase {

    private final SQLite sqlite;

    public UserDatabase(SQLite sqlite) {
        this.sqlite = sqlite;
    }

    public UserData getUser(UUID uuid) {
        try {
            PreparedStatement st = sqlite.getConnection().prepareStatement(
                "SELECT * FROM users WHERE uuid = ?"
            );
            st.setString(1, uuid.toString());
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return new UserData(
                    uuid,
                    rs.getString("nickname"),
                    rs.getInt("agreed") == 1
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new UserData(uuid, null, false);
    }

    public void saveUser(UserData user) {
        try {
            PreparedStatement st = sqlite.getConnection().prepareStatement(
                """
                INSERT INTO users(uuid, nickname, agreed)
                VALUES (?, ?, ?)
                ON CONFLICT(uuid)
                DO UPDATE SET nickname = excluded.nickname, agreed = excluded.agreed;
                """
            );

            st.setString(1, user.getUuid().toString());
            st.setString(2, user.getNickname());
            st.setInt(3, user.isAgreed() ? 1 : 0);

            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
