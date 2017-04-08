package cn.cc.ccu.tools;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 * Created by NTG on 2016/5/31.
 */
public class EmailYz {
	
	String email=null;
	
	/**
	 * 
	 * @param email 需要接收验证码的邮箱地址
	 */
	public EmailYz(String email){
		
		this.email=email;
	}

	/**
	 * 获取随机验证码
	 * @return 验证码
	 */
    public String getYzm(){

        String yzm= (int)(1+Math.random()*(9))+""+(int)(1+Math.random()*(9))+""+(int)(1+Math.random()*(9))+""+(int)(1+Math.random()*(9))+"";
        return yzm;
    }
    /**
     * 发送邮件
     * @return
     * @throws Exception
     */
    public  String sendYzm() throws Exception{
        
        String yzm=getYzm();

        Properties props=new Properties();

        props.setProperty("mail.smtp.auth", "true");

        props.setProperty("mail.transport.protocol", "smtp");

        props.put("mail.smtp.starttls.enable", "true");

        Session session=Session.getDefaultInstance(props);

        session.setDebug(true);

        Message msg=new MimeMessage(session);

        msg.setText("祝你身体健康，万事如意，你的验证码为："+yzm);

        msg.setFrom(new InternetAddress("ntg_javamail@sina.com"));

        Transport transport=session.getTransport();

        transport.connect("smtp.sina.com", 25, "ntg_javamail","yulanlan5201314");

        transport.sendMessage(msg, new Address[]{new InternetAddress(email)});

        transport.close();
            return yzm;
    }
    
    public static void main(String[] args) {
		
    	EmailYz em=new EmailYz("1310556122@qq.com");
    	
    	try {
			em.sendYzm();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}


}
