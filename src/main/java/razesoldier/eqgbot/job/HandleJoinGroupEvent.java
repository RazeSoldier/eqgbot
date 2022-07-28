/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 with Mamoe Exceptions license that can be found via the following link.
 *
 * https://github.com/RazeSoldier/eqgbot/blob/master/LICENSE
 */

package razesoldier.eqgbot.job;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import razesoldier.eqgbot.CharacterFilter;
import razesoldier.eqgbot.EVECharacter;
import razesoldier.eqgbot.EVEUser;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 处理进群请求。
 */
public class HandleJoinGroupEvent implements Job, Consumer<MemberJoinRequestEvent> {
    private final MiraiLogger logger;
    private final Bot bot;
    private final List<Long> pingGroupList;

    public HandleJoinGroupEvent(List<Long> pingGroupList, MiraiLogger logger, Bot bot) {
        this.pingGroupList = pingGroupList;
        this.logger = logger;
        this.bot = bot;
    }

    @Override
    public void accept(MemberJoinRequestEvent event) {
        try {
            final var groupId = event.getGroupId();
            final var fromId = event.getFromId();
            logger.info(groupId + ": 接受到" + fromId + "的入群请求");
            boolean isPingGroup = pingGroupList.contains(groupId); // 判断当前申请的是不是集结群

            if (isPingGroup && checkAccountIsJoin(fromId)) {
                logger.info(groupId + ": 拒绝" + fromId + "的入群请求，原因：角色已经在另外一个集结群");
                event.reject(false, "你已经加入另一个集结群了");
                return;
            }

            Optional<List<EVEUser>> user = EVEUser.newInstance(fromId);
            if (user.isPresent()) {
                List<EVECharacter> filterCharacters = CharacterFilter.of(user.get()).filterAlliance(562593865);
                if (filterCharacters.isEmpty()) {
                    logger.info(groupId + ": 拒绝" + fromId + "的入群请求,原因:角色不在主联盟");
                    event.reject(false, "查询不到QQ绑定记录");
                } else {
                    event.accept(); // 接受请求
                    logger.info(groupId + ": 接受" + fromId + "的入群请求");
                    if (isPingGroup) {
                        // 并发送“军团-角色名”到群聊
                        EVECharacter mainCharacter = filterCharacters.get(0).getUser().getMainCharacter();
                        event.getGroup().sendMessage("欢迎加入VVV国服集结群，" +
                                mainCharacter.getCorporationName() + '-' + mainCharacter.getName() + "，进群后请屏蔽本机器人");
                    }
                }
            } else {
                logger.info(groupId + ": 拒绝" + fromId + "的入群请求,原因:查询不到QQ绑定记录");
                event.reject(false, "查询不到QQ绑定记录");
            }
        } catch (Exception e) {
            event.reject(false, "机器人出错啦，请等待开发维修完毕~");
            logger.error(e);
        }
    }

    /**
     * 检查指定的QQ帐号是否存在于{@link HandleJoinGroupEvent#pingGroupList}中
     * @return 如果存在返回TRUE，不存在返回FALSE
     */
    private boolean checkAccountIsJoin(Long id) {
        for (Long groupId : pingGroupList) {
            if (bot.getGroupOrFail(groupId).contains(id)) {
                return true;
            }
        }
        return false;
    }
}
