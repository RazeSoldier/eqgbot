package razesoldier.eqgbot.job.msghandler;

import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import java.text.SimpleDateFormat;
import java.util.Date;

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
            Date d = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            final var sender = event.getSender();
            final var msg= event.getMessage().contentToString();
            int dd = Integer.parseInt(msg.substring(9,10));
            int hh = Integer.parseInt(msg.substring(11,12));
            int mm = Integer.parseInt(msg.substring(13,14));
            String text = df.format(new Date(d.getTime() + 8 * 60 * 60 * 1000L//+8时区
                                                         + dd * 24 * 60 * 60 * 1000L//加上增强时间天数
                                                         + hh * 60 * 60 * 1000L//加上增强时间小时数
                                                         + mm * 60 * 1000L//加上增强时间分钟数
                                            )
            );
            event.getGroup().sendMessage(
                    new At(sender).plus(text)
            );
        } catch (Exception e) {

        }
    }
}
