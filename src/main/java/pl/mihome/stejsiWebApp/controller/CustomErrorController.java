package pl.mihome.stejsiWebApp.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

	@RequestMapping("/error")
	public String errorHandler(HttpServletRequest req, Model model) {
		var status = req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		model.addAttribute("errorcode", status);
		model.addAttribute("errorreason", req.getAttribute(RequestDispatcher.ERROR_MESSAGE));
		return "error";
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}
}
