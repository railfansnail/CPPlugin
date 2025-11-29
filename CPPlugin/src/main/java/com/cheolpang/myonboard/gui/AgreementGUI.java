package com.cheolpang.cpplugin.gui;

import com.cheolpang.cpplugin.Main;
import com.cheolpang.cpplugin.data.UserDatabase;
import com.cheolpang.cpplugin.data.UserData;
import com.cheolpang.cpplugin.util.PlayerBlocker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AgreementGUI implements Listener {

    private static final String TITLE = ChatColor.BOLD + "서버 규칙 동의";
    private final Player player;
    private final UserDatabase db;
    private final UserData user;
    private Inventory inv;

    public AgreementGUI(Player player, UserDatabase db, UserData user) {
        this.player = player;
        this.db = db;
        this.user = user;
        // 리스너 등록
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        // 블로커 등록
        Main.getInstance().getBlocker().block(player);
    }

    public void open() {
        inv = Bukkit.createInventory(null, 9, TITLE);

        ItemStack agree = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta am = agree.getItemMeta();
        am.setDisplayName(ChatColor.GREEN + "동의합니다");
        am.setLore(List.of("§7서버 규칙에 동의합니다."));
        agree.setItemMeta(am);

        ItemStack deny = new ItemStack(Material.RED_CONCRETE);
        ItemMeta dm = deny.getItemMeta();
        dm.setDisplayName(ChatColor.RED + "동의하지 않습니다");
        dm.setLore(List.of("§7동의하지 않으면 접속이 불가합니다."));
        deny.setItemMeta(dm);

        // 배치: 가운데에 동의 버튼, 끝에 거부
        inv.setItem(4, agree);
        inv.setItem(8, deny);

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (e.getInventory() == null || e.getClickedInventory() == null) return;
        if (!e.getView().getTitle().equals(TITLE)) return;
        Player p = (Player) e.getWhoClicked();
        if (!p.getUniqueId().equals(player.getUniqueId())) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;
        String name = clicked.getItemMeta().getDisplayName();

        if (name.contains("동의합니다")) {
            // 동의 처리
            user.setAgreed(true);
            db.saveUser(user);
            p.sendMessage("§a규칙에 동의했습니다. 다음 단계로 이동합니다.");
            // 언블로킹
            Main.getInstance().getBlocker().unblock(p);
            closeAndUnregister();
            // 닉네임이 없으면 닉네임 창 실행
            if (user.getNickname() == null) {
                new NicknameGUI(p, db, user).open();
            }
        } else if (name.contains("동의하지")) {
            p.kickPlayer("규칙에 동의하지 않았습니다.");
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!e.getView().getTitle().equals(TITLE)) return;
        Player p = (Player) e.getPlayer();
        if (!p.getUniqueId().equals(player.getUniqueId())) return;

        // 아직 동의 안 했으면 바로 다시 띄움(1틱 뒤)
        if (!user.isAgreed()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (p.isOnline()) open();
                }
            }.runTaskLater(Main.getInstance(), 1L);
        } else {
            closeAndUnregister();
        }
    }

    private void closeAndUnregister() {
        HandlerList.unregisterAll(this);
        // 안전하게 언블로킹 (이미 해제되었을 수도 있음)
        Main.getInstance().getBlocker().unblock(player);
    }
}
