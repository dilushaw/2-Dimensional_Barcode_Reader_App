/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Email;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;
 
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
@Service
public class EmailSender {
 
//	public static void main(String[] args) {
// EmailSender email=new EmailSender();
// List<String> recpts=new ArrayList<String>();
// recpts.add("dilumc7@gmail.com");
//        try {
//            email.sendMailWithAuth( "smtp.gmail.com","wijepvt@gmail.com","punnandiblu777", "587", recpts, null, "somethng");
//        } catch (Exception ex) {
//            Logger.getLogger(EmailSender.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//	}
        
        public void sendMailWithAuth(String host, String user, String password, String port, List<String> toList,
     String htmlBody, String subject) throws Exception {

    Properties props = System.getProperties();

    props.put("mail.smtp.user",user); 
    props.put("mail.smtp.password", password);
    props.put("mail.smtp.host", host); 
    props.put("mail.smtp.port", port); 
    //props.put("mail.debug", "true"); 
    props.put("mail.smtp.auth", "true"); 
    props.put("mail.smtp.starttls.enable","true"); 
    props.put("mail.smtp.EnableSSL.enable","true");

    Session session = Session.getInstance(props, null);
    //session.setDebug(true);

    MimeMessage message = new MimeMessage(session);
    message.setFrom(new InternetAddress(user));

    // To get the array of addresses
    for (String to: toList) {
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
    }

    message.setSubject(subject);
    message.setText("hi");

    Transport transport = session.getTransport("smtp");
    try {
        transport.connect(host, user, password);
        transport.sendMessage(message, message.getAllRecipients());
    } finally {
        transport.close();
    }
        
}
}
