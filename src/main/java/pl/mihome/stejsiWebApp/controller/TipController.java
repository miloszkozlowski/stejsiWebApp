package pl.mihome.stejsiWebApp.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.LoggerFactory;
import org.imgscalr.Scalr;
import org.slf4j.Logger;

import pl.mihome.stejsiWebApp.DTO.appComms.TipCommentWriteModel;
import pl.mihome.stejsiWebApp.DTO.appComms.TipReadModel;
import pl.mihome.stejsiWebApp.logic.TipService;
import pl.mihome.stejsiWebApp.model.Tip;
import pl.mihome.stejsiWebApp.model.TipPicture;

@Controller
@Secured("ROLE_STEJSI")
@RequestMapping("/tips")
public class TipController {

	TipService service;
	private static final Logger log = LoggerFactory.getLogger(TipController.class);

	public TipController(TipService service) {
		this.service = service;
	}
	
	@GetMapping
	public String addTipBlank(@RequestParam(required = false) Long remove, @RequestParam(required = false) Long comments, Model model, RedirectAttributes redir) {
		model.addAttribute("newcomment", new TipCommentWriteModel());
		if(remove != null) {
			service.removeComment(remove);
			redir.addFlashAttribute("msg", "Twoja porada została opublikowana!");
			return "redirect:/tips?comments=" + comments + "#comments";
		}
		return "tipAdd";
	}
	
	@PostMapping(params = "addnew")
	public String addNew(Model model) {
		model.addAttribute("newTip", new Tip());
		model.addAttribute("newcomment", new TipCommentWriteModel());
		return "tipAdd";
	}
	
	@PostMapping(params = "newcomment")
	public String newComment(@ModelAttribute("newcomment") @Valid TipCommentWriteModel newComment,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redir) {
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("newcomment", newComment);
		}
		
		else {
			service.trainerToAddComment(newComment);
			redir.addFlashAttribute("msg", "Twój komentarz został zapisany");
			return "redirect:/tips?comments=" + newComment.getTipId() + "#comments";

		}

		return "tipAdd";
	}
	
	@PostMapping("/dodaj")
	public String addTip(
			@ModelAttribute("newTip") @Valid Tip source,
			BindingResult bindingResult,
			@RequestParam(name = "picture_upload", required = false) MultipartFile picture_upload,
			Model model,
			RedirectAttributes redir) throws IllegalArgumentException, IOException {
		
		model.addAttribute("newcomment", new TipCommentWriteModel());
		
		if(bindingResult.hasErrors() || (source.getImageUrl().isBlank() && picture_upload.isEmpty())) {
			if(source.getImageUrl().isBlank() && picture_upload.isEmpty()) {
				model.addAttribute("err", "Do porady należy albo watawić zdjęcie z dysku albo wpisać link");
			}
			return "tipAdd";
		}
		else {
			
			if(!picture_upload.isEmpty()) {
				log.info("Załadowany plik o rozmiarze " + picture_upload.getSize());
				try {
					var pic = new TipPicture();
					pic.setPicture(picture_upload.getBytes());
					
					//tworzenie miniaturki
					ByteArrayInputStream bais = new ByteArrayInputStream(picture_upload.getBytes());
					String imageType = URLConnection.guessContentTypeFromStream(bais);
					if(imageType == null) {
						throw new IllegalArgumentException();
					}
					pic.setContentType(imageType);
					BufferedImage bi = ImageIO.read(bais);
					BufferedImage thumbBi = Scalr.resize(bi, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, 180, Scalr.OP_ANTIALIAS);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();

					ImageIO.write(thumbBi, imageType.substring(6), baos);
		
					//tworzenie obiektu to zapisu:
					pic.setThumb(baos.toByteArray());
					pic.setContentType(imageType);
					source.setPicture(pic);
					source.setLocalImagePresent(true);
				}
				catch(IOException ex) {
					log.error("Nie powiodło się ładowanie obrazu z pliku " + picture_upload.getName() + ", bo: /n" + ex.getLocalizedMessage());
				}
			}
			else if(!source.getImageUrl().isBlank()) {
				
				try {
					URL url = new URL(source.getImageUrl());
					HttpURLConnection connection = (HttpURLConnection)  url.openConnection();
					connection.setRequestMethod("HEAD");
					connection.connect();
					String contentType = connection.getContentType();
					BufferedImage bi = ImageIO.read(url);
					BufferedImage thumbBi = Scalr.resize(bi, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_WIDTH, 180, Scalr.OP_ANTIALIAS);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(thumbBi, contentType.substring(6), baos);
					var pic = new TipPicture();		
					pic.setContentType(contentType);
					pic.setThumb(baos.toByteArray());
					source.setPicture(pic);
				}
				 catch (IOException e) {
					e.printStackTrace();
				}
			}
			service.saveNewTip(source);
			redir.addFlashAttribute("msg", "Twoja porada została opublikowana!");
			return "redirect:/tips";
		}
		
	}
	
	@GetMapping("/usun/{tid}")
	public String removeType(@PathVariable Long tid, Model model) {
		
		String removedName = service.removeById(tid);
		if(!removedName.equals("")) {
			model.addAttribute("msg", "Rada \"" + removedName + "\" została usunięta");
			model.addAttribute("tips", service.getAllTips());
			model.addAttribute("newcomment", new TipCommentWriteModel());
		}
		
		return "tipAdd";
		
	}
	
	//zwraca miniaturę
	@GetMapping("/img/{tid}")
	public ResponseEntity<byte[]> getImage(@PathVariable Long tid) {
		var media = service.getTipPictureFromTipId(tid);
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(media.getImageType()));
		return new ResponseEntity<byte[]>(media.getThumb(), headers, HttpStatus.CREATED);
	}
	
	@ModelAttribute("tips")
	List<TipReadModel> wszystkiePakiety() {
		return service.getAllTips();
	}
	
	
}
