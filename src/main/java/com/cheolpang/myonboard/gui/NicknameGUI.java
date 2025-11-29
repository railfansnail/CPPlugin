package com.cheolpang.cpplugin.gui;

import com.cheolpang.cpplugin.Main;
import com.cheolpang.cpplugin.data.UserDatabase;
import com.cheolpang.cpplugin.data.UserData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.conversations.*;

public class NicknameGUI {

    private final Player player;
    private final UserDatabase db;
    private final UserData user;

    public NicknameGUI(Player player, UserDatabase db, UserData user) {
        this.player = player;
        this.db = db;
        this.user = user;
    }

    public void open() {
        // 블로커에 등록 (이동/명령 등 차단)
        Main.getInstance().getBlocker().block(player);

        ConversationFactory factory = new ConversationFactory(Main.getInstance())
                .withModality(true)
                .withEscapeSequence("/cancel")
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return ChatColor.YELLOW + "원하는 닉네임을 입력하세요. (한글/영어/숫자 허용, 2~16자). 취소하려면 /cancel";
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        String name = input == null ? "" : input.trim();

                        if (!isValidNickname(name)) {
                            return new StringPrompt() {
                                @Override
                                public String getPromptText(ConversationContext context) {
                                    return ChatColor.RED + "유효하지 않은 닉네임입니다. 2~16자, 공백 불가. 다시 입력하세요:";
                                }

                                @Override
                                public Prompt acceptInput(ConversationContext context, String input) {
                                    // 이 내부 프롬프트에서 다시 검증 (간단 재사용)
                                    String name2 = input == null ? "" : input.trim();
                                    if (!isValidNickname(name2)) {
                                        return Prompt.END_OF_CONVERSATION;
                                    }
                                    finish(name2);
                                    return Prompt.END_OF_CONVERSATION;
                                }
                            };
                        } else {
                            finish(name);
                            return Prompt.END_OF_CONVERSATION;
                        }
                    }
                })
                .withLocalEcho(false);

        Conversation convo = factory.buildConversation(player);
        convo.begin();
    }

    private boolean isValidNickname(String s) {
        if (s == null) return false;
        int len = s.codePointCount(0, s.length());
        if (len < 2 || len > 16) return false;
        if (s.contains(" ")) return false;
        if (s.contains("§")) return false; // § 같은 색코드 문자 금지
        // 추가 검증(금칙어, 특수문자 등)은 여기서 처리하면 됨
        return true;
    }

    private void finish(String nickname) {
        user.setNickname(nickname);
        db.saveUser(user);
        player.sendMessage(ChatColor.GREEN + "닉네임이 설정되었습니다: " + ChatColor.AQUA + nickname);
        // 언블로킹
        Main.getInstance().getBlocker().unblock(player);
    }
}
