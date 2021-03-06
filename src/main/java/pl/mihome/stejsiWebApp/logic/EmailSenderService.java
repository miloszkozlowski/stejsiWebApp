package pl.mihome.stejsiWebApp.logic;

import javax.mail.SendFailedException;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
   
    public JavaMailSender javaMailSender;



    public EmailSenderService(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}



	public void sendSimpleMessage(String to, String subject, String text) {
		var email = new SimpleMailMessage();
		email.setFrom("Stejsi <automat@stejsi.pl>");
		email.setSubject(subject);
		email.setTo(to);
		email.setText(text);
		
		javaMailSender.send(email);
    }
}
