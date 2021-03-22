package razesoldier.eqgbot.job.msghandler;

import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

class GitTimeCommand implements MessageHandler {
    private final GroupMessageEvent event;

    public GitTimeCommand(GroupMessageEvent event) {
        this.event = event;
    }

    @Override
    public void handle() throws Exception {
        //输入：.gittime ddhhmm
        //返回：yyyy-mm-dd hh:mm:ss
        try {
            final var sender = event.getSender();
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("+8")); // 当前的北京时间
            var reply = String.format("当前时间：%s\n计算后的时间：%s",
                    now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    plusTime(now, event.getMessage().contentToString())
            );
            event.getGroup().sendMessage(
                    new At(sender).plus(reply)
            );
        } catch (Exception e) {
            event.getGroup().sendMessage(e.getMessage());
        }
    }

    @NotNull
    private static String plusTime(ZonedDateTime baseTime, String plusText) throws Exception {
        plusText = plusText.split(" ")[1];
        if (plusText.length() != 6) {
            throw new Exception("格式必须为ddhhmm（dd为天数，含前导零；hh为小时数；mm为分钟数）");
        }

        return baseTime
                .plusDays(Long.parseLong(plusText.substring(0, 2)))
                .plusHours(Long.parseLong(plusText.substring(2, 4)))
                .plusMinutes(Long.parseLong(plusText.substring(4, 6)))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}