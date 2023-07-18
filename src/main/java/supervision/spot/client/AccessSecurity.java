package supervision.spot.client;

import java.util.HashMap;
import com.palestink.server.sdk.msg.Message;

public class AccessSecurity {
    /**
     * 检查
     * 
     * @param parameter 参数
     * @return 消息对象
     */
    public static final Message check(final HashMap<String, Object> parameter) {
        final Message msg = new Message();
        final String token = (String) parameter.get("token");
        // 客户端匿名访问硬编码
        if (!token.equalsIgnoreCase("24f449dbbd29dc6381f33aad6ea9dfd4")) {
            msg.setStatus(Message.Status.ERROR);
            msg.setContent("INVALID_TOKEN");
            msg.setAttach("非法Token");
            return msg;
        }
        msg.setStatus(Message.Status.SUCCESS);
        return msg;
    }
}