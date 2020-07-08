package pl.mihome.stejsiWebApp.security;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SSOController {

	@GetMapping("/logout")
	String logout(HttpServletRequest req, Model model, RedirectAttributes redir) throws ServletException {
		req.logout();
		redir.addFlashAttribute("msg", "Wylogowano");
		return "redirect:/";
	}
}
