package pl.mihome.stejsiWebApp.DTO.appComms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TipCommentWriteModel {
	
	@NotBlank(message = "Komentarz nie może być pusty")
	private String body;
	
	@NotNull
	private Long tipId;
	
	public TipCommentWriteModel() {
		super();
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Long getTipId() {
		return tipId;
	}
	public void setTipId(Long tipId) {
		this.tipId = tipId;
	}
	
	

}
