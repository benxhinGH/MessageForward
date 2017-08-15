package com.usiellau.messageforward.network;

import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * Created by Administrator on 2017/8/10 0010.
 */

public class EmailUtil {


    // 邮件发送协议
    private final static String PROTOCOL = "smtp";

    // SMTP邮件服务器
    private final static String HOST = "smtp.163.com";

    // SMTP邮件服务器默认端口
    private final static String PORT = "25";

    // 是否要求身份认证
    private final static String IS_AUTH = "true";

    // 是否启用调试模式（启用调试模式可打印客户端与服务器交互过程时一问一答的响应消息）
    private final static String IS_ENABLED_DEBUG_MOD = "false";

    // 发件人
    private static String from = "15091059662@163.com";

    //用户名
    private static final String USERNAME="15091059662";
    //授权码
    private static final String AUTH_CODE="6666666";

    // 初始化连接邮件服务器的会话信息
    private static Properties props = null;

    static {
        props = new Properties();
        props.setProperty("mail.transport.protocol", PROTOCOL);
        props.setProperty("mail.smtp.host", HOST);
        props.setProperty("mail.smtp.port", PORT);
        props.setProperty("mail.smtp.auth", IS_AUTH);
        props.setProperty("mail.debug",IS_ENABLED_DEBUG_MOD);
    }

    /**
     * 发送简单的文本邮件
     */
    public static void sendTextEmail(String[] addresses,String subject,String content) throws Exception{
        // 创建Session实例对象
        Session session = Session.getDefaultInstance(props);

        // 创建MimeMessage实例对象
        MimeMessage message = new MimeMessage(session);
        // 设置发件人
        message.setFrom(new InternetAddress(from));
        // 设置邮件主题
        message.setSubject(subject);
        // 设置收件人
        message.setRecipients(MimeMessage.RecipientType.TO, getAddresss(addresses));
        // 设置发送时间
        message.setSentDate(new Date());
        // 设置纯文本内容为邮件正文
        message.setText(content);
        // 保存并生成最终的邮件内容
        message.saveChanges();
        // 获得Transport实例对象
        Transport transport = session.getTransport();
        // 打开连接
        transport.connect(USERNAME, AUTH_CODE);
        // 将message对象传递给transport对象，将邮件发送出去
        transport.sendMessage(message, message.getAllRecipients());
        // 关闭连接
        transport.close();
    }

    public static Address[] getAddresss(String[] addresses) throws AddressException {
        Address[] res=new Address[addresses.length];
        for(int i=0;i<addresses.length;++i){
            res[i]=new InternetAddress(addresses[i]);
        }
        return res;
    }
}
