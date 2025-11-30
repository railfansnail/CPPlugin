package com.cheolpang.cpplugin.gui;

import com.cheolpang.cpplugin.Main;
import com.cheolpang.cpplugin.data.UserData;
import com.cheolpang.cpplugin.data.UserDatabase;
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
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.Arrays;
import java.util.List;

public class AgreementGUI implements Listener {

    private static final String TITLE = ChatColor.BOLD + "서버 규칙 동의";

    private final Player player;
    private final UserDatabase db;
    private final UserData user;
    private Inventory inv;

    private static final List<String> RULE_LINES = Arrays.asList(
            "[철팽서버 간단 규칙]",
            "1. 핵, 치트 (엑스레이 등) 사용 금지.",
            "2. 광고, 도배 등의 행위 일체 금지.",
            "3. 서버 버그 악용 금지.",
            "4. 분쟁 참여, 유발 및 가담 금지.",
            "5. 서버 테러 및 전복 시도, 유발, 가담 금지.",
            "6. 서버 관리자 비방, 펌하 금지.",
            "7. 간단 규칙 악용 금지.",
            "※ 규칙 위반 시 경고/밴/IP 밴 조치가 가능합니다."
    );

    public AgreementGUI(Player player, UserDatabase db) {
        this.player = player;
        this.db = db;
        this.user = db.getOrCreate(player.getUniqueId());

        if (user.hasAgreed()) return; // 이미 동의했으면 GUI 띄우지 않음

        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        Main.getInstance().getBlocker().block(player);

        open();
    }

    public void open() {
        inv = Bukkit.createInventory(null, 27, TITLE);

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fm = filler.getItemMeta();
        fm.setDisplayName(" ");
        filler.setItemMeta(fm);
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta pm = book.getItemMeta();
        pm.setDisplayName(ChatColor.GOLD + "서버 규칙 (반드시 읽어주세요)");
        pm.setLore(RULE_LINES.stream().map(line -> ChatColor.WHITE + line).toList());
        book.setItemMeta(pm);

        inv.setItem(13, book);

        ItemStack agree = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta am = agree.getItemMeta();
        am.setDisplayName(ChatColor.GREEN + "동의합니다");
        am.setLore(List.of(ChatColor.GRAY + "서버 규칙을 읽고 동의합니다."));
        CustomModelDataComponent compA = am.getCustomModelDataComponent(); // custom_model_data 구하기
        compA.setStrings(List.of("agree")); // custom_model_data string에 agree 박기
        am.setCustomModelDataComponent(compA); // custom_model_data 적용하기
        agree.setItemMeta(am);
        inv.setItem(11, agree);

        ItemStack deny = new ItemStack(Material.RED_CONCRETE);
        ItemMeta dm = deny.getItemMeta();
        dm.setDisplayName(ChatColor.RED + "동의하지 않습니다");
        dm.setLore(List.of(ChatColor.GRAY + "동의하지 않으면 접속이 불가합니다."));
        CustomModelDataComponent compD = dm.getCustomModelDataComponent(); // custom_model_data 구하기
        compD.setStrings(List.of("deny")); // custom_model_data string에 deny 박기
        dm.setCustomModelDataComponent(compD); // custom_model_data 적용하기
        deny.setItemMeta(dm);
        inv.setItem(15, deny);

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getView() == null || !TITLE.equals(e.getView().getTitle())) return;
        if (!p.getUniqueId().equals(player.getUniqueId())) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        ItemMeta meta = clicked.getItemMeta();
        if (!meta.hasCustomModelDataComponent()) return;

        CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
        List<String> strings = cmd.getStrings();

        if (strings.contains("agree")) {
            // 동의 처리
            user.setAgreed(true);
            db.save(user);

            // 블로커 해제
            Main.getInstance().getBlocker().unblock(p);

            // 메시지 전송
            p.sendMessage(ChatColor.GREEN + "규칙에 동의했습니다.");

            // 이벤트 언레지스터 후 GUI 닫기
            HandlerList.unregisterAll(this);
            p.closeInventory();

        } else if (strings.contains("deny")) {
            // 거부 처리
            HandlerList.unregisterAll(this);
            p.kickPlayer("규칙에 동의하지 않았습니다.");
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getView() == null || !TITLE.equals(e.getView().getTitle())) return;
        Player p = (Player) e.getPlayer();
        if (!p.getUniqueId().equals(player.getUniqueId())) return;

        // 동의 안 했으면 강제 재오픈
        if (!user.hasAgreed()) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (p.isOnline() && !user.hasAgreed()) open();
            }, 5L);
        }
    }
}
