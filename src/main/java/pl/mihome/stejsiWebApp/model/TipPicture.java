package pl.mihome.stejsiWebApp.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "tip_pictures")
public class TipPicture extends AuditBase {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(mappedBy = "picture", fetch = FetchType.LAZY)
	private Tip tip;
	
	@Lob
	@Type(type="org.hibernate.type.ImageType")
	@Column(columnDefinition = "BLOB")
	@Basic(fetch = FetchType.LAZY)
	private byte[] picture;
	
	@Lob
	@Type(type="org.hibernate.type.ImageType")
	@Column(columnDefinition = "BLOB")
	@Basic(fetch = FetchType.LAZY)
	private byte[] thumb;
	
	@JsonIgnore
	private String contentType;
	

	public TipPicture() {
	
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tip getTip() {
		return tip;
	}

	public void setTip(Tip tip) {
		this.tip = tip;
	}

	public byte[] getSource() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}
	
	public byte[] getThumb() {
		return thumb;
	}

	public void setThumb(byte[] thumb) {
		this.thumb = thumb;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getImageType() {
		if(getContentType() != null) {
			return getContentType();
		}
		return "";

	}
}
