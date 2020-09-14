package pl.mihome.stejsiWebApp.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "tokens")
public class Token extends AuditBase {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "owner", referencedColumnName = "id")
	@JsonBackReference
	private Podopieczny owner;
	
	@Column(unique = true, name = "token_body", nullable = false, length = 32)
	@Size(max = 32, min = 32)
	private String tokenString;
	
	@Column(name = "device_id")
	private String assignedDeviceId;
	
	@Column(name = "token_FCM")
	private String tokenFCM;
	
	@Column(name = "activation_code")
	@Size(max = 16, min = 16)
	private String activationCode;
	
	private boolean active;
	

	public Token() {
	}

	public Token(Podopieczny owner, @Size(max = 32, min = 32) String tokenString, String assignedDeviceId,
			String FCMToken, @Size(max = 16, min = 16) String activationCode) {
		this.owner = owner;
		this.tokenString = tokenString;
		this.assignedDeviceId = assignedDeviceId;
		this.activationCode = activationCode;
		this.tokenFCM = FCMToken;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public Podopieczny getOwner() {
		return owner;
	}

	public String getTokenString() {
		return tokenString;
	}

	public String getAssignedDeviceId() {
		return assignedDeviceId;
	}

	public String getTokenFCM() {
		return tokenFCM;
	}

	public void setTokenFCM(String tokenFCM) {
		this.tokenFCM = tokenFCM;
	}
	
	
}
