package pl.mihome.stejsiWebApp.logic;

import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pl.mihome.stejsiWebApp.config.AngularConfiguration;
import pl.mihome.stejsiWebApp.exeptions.NotFoundCustomException;
import pl.mihome.stejsiWebApp.model.TipPicture;
import pl.mihome.stejsiWebApp.model.TipPictureRepo;

@Service
public class TipPictureService {

	TipPictureRepo pictureRepo;
	AngularConfiguration angularConfig;
	
	private static final Logger log = LoggerFactory.getLogger(TipPictureService.class);

	public TipPictureService(TipPictureRepo pictureRepo, AngularConfiguration angularConfig) {
		this.pictureRepo = pictureRepo;
		this.angularConfig = angularConfig;
	}
	
	public TipPicture getTipPictureFromTipId(Long tid) throws NotFoundCustomException {
		log.info("Ładowanie obiekty obrazka dla tip id: " + tid);
		var img = pictureRepo.findByTipId(tid);
		
		if(img.isPresent()) {
			return img.get();
		}
		log.warn("Nie załadowano obiektu obrazka, bo nie ma takiego obrazka dla tip id: " + tid);
		throw new NotFoundCustomException();
	}
	
	public TipPicture processImageUploaded(MultipartFile source) throws IllegalArgumentException, IOException {
		return processImageBytes(source.getBytes());
	}

	public TipPicture processImageBytes(byte[] source) throws IllegalArgumentException, IOException {
		var pic = new TipPicture();
		pic.setContentType(getImageType(source));
		var pictureBytes = source.length > angularConfig.getMaxStoredPictureSize() ? resizeUploadedPicture(source) : source;
		pic.setPicture(pictureBytes);
		pic.setThumb(createThumbnail(source));
		return pic;
	}

	
	public TipPicture processImageUrl(String urlString) throws MalformedURLException, ProtocolException, IOException {
		URL url = new URL(urlString);
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
		return pic; 
	}
	
	private byte[] createThumbnail(byte[] original) throws IllegalArgumentException, IOException {
		var imageType = getImageType(original);
		ByteArrayInputStream thbais = new ByteArrayInputStream(original);
		BufferedImage thbi = ImageIO.read(thbais);
		BufferedImage thumbBi = Scalr.resize(thbi, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, 180, Scalr.OP_ANTIALIAS);
		ByteArrayOutputStream thbaos = new ByteArrayOutputStream();
		ImageIO.write(thumbBi, imageType.substring(6), thbaos);
		return thbaos.toByteArray();
	}
	
	private String getImageType(byte[] original) throws IllegalArgumentException, IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(original);
		String imageType = URLConnection.guessContentTypeFromStream(bais);
		if(imageType == null) {
			throw new IllegalArgumentException();
		}
		return imageType;
	}
	
	private byte[] resizeUploadedPicture(byte[] pictureUploaded) throws IOException {
			ByteArrayInputStream bais = new ByteArrayInputStream(pictureUploaded);
			BufferedImage bi = ImageIO.read(bais);
			BufferedImage outputPicBi = Scalr.resize(bi, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, 800, Scalr.OP_ANTIALIAS);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(outputPicBi, getImageType(pictureUploaded).substring(6), baos);
			return baos.toByteArray();

	}
}
