package pl.mihome.stejsiWebApp.DTO.appComms;

import java.time.LocalDateTime;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import pl.mihome.stejsiWebApp.model.TipComment;

@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
public class TipCommentReadModel {
	
	@Id
	private Long id;
	private String body;
	private String authorName;
	private Long authorId;
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime whenCreated;
	
	public TipCommentReadModel() {	
	}
	
	public TipCommentReadModel(TipComment tipComment) {
		this.id = tipComment.getId();
		this.body = tipComment.getBody().strip();
		if(tipComment.getAuthor() == null) {
			this.authorName = "Stejsi";
		}
		else {
			this.authorId = tipComment.getAuthor().getId();
			this.authorName = tipComment.getAuthor().getImie() + " " + tipComment.getAuthor().getNazwisko().substring(0, 1) + ".";
		}
		this.whenCreated = tipComment.getWhenCreated();
	}
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
		
	}
	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public LocalDateTime getWhenCreated() {
		return whenCreated;
	}
	public void setWhenCreated(LocalDateTime whenCreated) {
		this.whenCreated = whenCreated;
	}
	
	

}
