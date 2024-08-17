/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.job;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import razesoldier.eqgbot.AllianceBlacklistFilter;
import razesoldier.eqgbot.dba.DatabaseAccessHolding;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用来处理搜索eims用户的命令
 */
public class HandleSearchEsiUserCommand extends SimpleListenerHost {
    @EventHandler
    public void handle(@NotNull GroupMessageEvent event) {
        String revMsg = event.getMessage().contentToString();
        if (!revMsg.startsWith(".esi ")) {
            return;
        }
        String searchText = revMsg.substring(5);
        try (var conn = DatabaseAccessHolding.getInstance().getConnection()) {
            @Language("MySQL") var preSql = """
                    select c.id as `id`
                    from user_characters
                            inner join characters c on user_characters.character_id = c.id
                            inner join tokens on tokens.character_id = c.id
                    where name = ? and valid = 1
                      and JSON_CONTAINS(scopes, JSON_ARRAY('esi-mail.read_mail.v1',
                                                           'esi-wallet.read_character_wallet.v1',
                                                           'esi-assets.read_assets.v1',
                                                           'esi-characters.read_contacts.v1',
                                                           'esi-location.read_location.v1',
                                                           'esi-location.read_ship_type.v1',
                                                           'esi-contracts.read_character_contracts.v1',
                                                           'esi-skills.read_skills.v1',
                                                           'esi-clones.read_clones.v1',
                                                           'esi-universe.read_structures.v1',
                                                           'esi-characters.read_notifications.v1',
                                                           'esi-killmails.read_killmails.v1',
                                                           'esi-location.read_online.v1'
                    ))
                    """;
            ResultSet res;
            try (PreparedStatement stat = conn.prepareStatement(preSql)) {
                stat.setString(1, searchText);
                res = stat.executeQuery();

                MessageChain msgChain = new At(event.getSender().getId()).plus(searchText);
                if (res.next()) {
                    // 检查是否在联盟黑名单内
                    if (new AllianceBlacklistFilter().filter(res.getInt("id"))) {
                        event.getGroup().sendMessage(msgChain.plus(" 联盟黑名单内"));
                    } else {
                        event.getGroup().sendMessage(msgChain.plus(" 已注册EIMS"));
                    }
                } else {
                    event.getGroup().sendMessage(msgChain.plus(" 尚未注册EIMS"));
                }
            }
        } catch (SQLException e) {
            event.getGroup().sendMessage(e.toString());
            event.getBot().getLogger().error(e);
        }
    }

    @Override
    public void  handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }
}
