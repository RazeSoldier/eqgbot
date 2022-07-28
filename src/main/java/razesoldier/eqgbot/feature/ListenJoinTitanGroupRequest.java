/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */

package razesoldier.eqgbot.feature;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import razesoldier.eqgbot.CharacterAffiliationFilter;
import razesoldier.eqgbot.CharacterAssetFilter;
import razesoldier.eqgbot.EVEUser;
import razesoldier.eqgbot.dba.SQLExecuteException;

public class ListenJoinTitanGroupRequest extends FeatureBase {
    private final Bot bot;
    private final Long titanGroup;
    private final MiraiLogger logger;

    ListenJoinTitanGroupRequest(Bot bot, Long titanGroup, MiraiLogger logger) {
        this.bot = bot;
        this.titanGroup = titanGroup;
        this.logger = logger;
    }

    @Override
    void handle() {
        bot.getEventChannel()
                .filterIsInstance(MemberJoinRequestEvent.class)
                .filter(event -> event.getGroupId() == titanGroup)
                .subscribeAlways(MemberJoinRequestEvent.class, event -> {
                    try {
                        EVEUser.newInstance(event.getFromId()).ifPresentOrElse(users -> {
                            try {
                                if (CharacterAssetFilter.of(CharacterAffiliationFilter.of(users).filterAlliance(562593865)).hasTitan()) {
                                    event.accept();
                                    logger.info("[泰坦群审核] " + event.getGroupId() + ": 允许" + event.getFromId() + "的入群请求");
                                } else {
                                    event.reject(false, "未能找到前线泰坦");
                                    logger.info("[泰坦群审核] " + event.getGroupId() + ": 拒绝" + event.getFromId() + "的入群请求,原因:未能找到前线泰坦");
                                }
                            } catch (SQLExecuteException e) {
                                event.reject(false, "机器人出错啦，请等待开发维修完毕~");
                                logger.error(e);
                            }
                        }, () -> {
                            logger.info("[泰坦群审核] " + event.getGroupId() + ": 拒绝" + event.getFromId() + "的入群请求,原因:未找到QQ记录");
                            event.reject(false, "未注册ESI");
                        });
                    } catch (SQLExecuteException e) {
                        event.reject(false, "机器人出错啦，请等待开发维修完毕~");
                        logger.error(e);
                    }
                });
    }
}
