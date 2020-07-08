package pl.mihome.stejsiWebApp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sun.istack.NotNull;

@Entity
@Table(name = "tip_comments")
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
public class TipComment extends AuditBase {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "tip_id", referencedColumnName = "id")
	@NotNull
	@JsonBackReference
	private Tip tip;
	
	@ManyToOne
	@JoinColumn(name = "author")
	private Podopieczny author;
	
	@NotBlank
	private String body;
	
	public TipComment() {
	
	}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Podopieczny getAuthor() {
		return author;
	}
	public void setAuthor(Podopieczny author) {
		this.author = author;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Tip getTip() {
		return tip;
	}

	public void setTip(Tip tip) {
		this.tip = tip;
	}
	
	
	
	
	

}
