package pl.mihome.stejsiWebApp.DTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import pl.mihome.stejsiWebApp.model.Tip;

public class TipWriteModel {
	
	@NotBlank
	@Size(max = 100)
	private final String heading;
	
	@NotBlank
	@Size(max = 1000)
	private final String body;
	
	private final String imageUrl;

	private final String uploadedImage;
	
	public TipWriteModel(String heading, String body, String imageUrl, String uploadedImage) {
		this.heading = heading;
		this.body = body;
		this.imageUrl = imageUrl;
		this.uploadedImage = uploadedImage;
	}
	
	public Tip mapToTip() {
		var tip = new Tip();
		tip.setHeading(heading);
		tip.setBody(body);
		tip.setImageUrl(imageUrl);
		tip.setLocalImagePresent(false);
		return tip;
	}

	public String getHeading() {
		return heading;
	}

	public String getBody() {
		return body;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getUploadedImage() {
		return uploadedImage;
	}
}
