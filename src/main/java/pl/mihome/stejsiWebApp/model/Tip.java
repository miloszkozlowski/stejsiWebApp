package pl.mihome.stejsiWebApp.model;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.BatchSize;
import pl.mihome.stejsiWebApp.constraints.ImageContent;

@Entity
@Table(name = "tips")
public class Tip extends AuditBase {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Porada musi mieć zawartość")
	@Size(max = 1000)
	private String body;
	
	@NotBlank(message = "Porada musi mieć nagłówek")
	@Size(max = 100)
	private String heading;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "pic_id", referencedColumnName = "id")
	@JsonIgnore
	private TipPicture picture;
	
	@Column(name = "url")
	@Size(max = 1000)
	@ImageContent
	private String imageUrl;
	
	@JsonIgnore
	private LocalDateTime usersNotified;
	
	@OneToMany(mappedBy = "tip", cascade = CascadeType.ALL)
	@BatchSize(size = 10)
	private Set<TipComment> comments;
	
	@Column(name = "image_present")
	private boolean localImagePresent;
	
	@ManyToMany(mappedBy = "tipsRead")
	@BatchSize(size = 10)
	private Set<Podopieczny> usersRead;

	public Tip() {
	
	}

	public Long getId() {
		return id;
	}


	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public TipPicture getPicture() {
		return picture;
	}

	public void setPicture(TipPicture picture) {
		this.picture = picture;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}


	public Set<TipComment> getComments() {
		return comments;
	}

	public void setComments(Set<TipComment> comments) {
		this.comments = comments;
	}

	public boolean isLocalImagePresent() {
		return localImagePresent;
	}

	public void setLocalImagePresent(boolean isLocalImagePresent) {
		this.localImagePresent = isLocalImagePresent;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Set<Podopieczny> getUsersRead() {
		return usersRead;
	}

	public LocalDateTime getUsersNotified() {
		return usersNotified;
	}

	public void setUsersNotified(LocalDateTime usersNotified) {
		this.usersNotified = usersNotified;
	}
	

}
