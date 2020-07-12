package pl.mihome.stejsiWebApp.DTO.appComms;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.mihome.stejsiWebApp.model.Podopieczny;
import pl.mihome.stejsiWebApp.model.Tip;
import pl.mihome.stejsiWebApp.model.TipComment;
import pl.mihome.stejsiWebApp.model.TipStatusByUser;

public class TipReadModel {
	
	private Long id;
	private String body;
	private String heading;
	private String imageUrl;
	private boolean localImagePresent;
	private List<TipCommentReadModel> comments;
	private LocalDateTime whenCreated;
	private LocalDateTime whenNotificationSent;
	
	private TipStatusByUser tipStatusByUser;
	
	public TipReadModel(Tip tip) {
		this.id = tip.getId();
		this.body = tip.getBody();
		this.heading = tip.getHeading();
		this.imageUrl = tip.getImageUrl();
		this.localImagePresent = tip.isLocalImagePresent();
		this.comments = tip.getComments().stream()
				.filter(c -> !c.isRemoved())
				.sorted(Comparator.comparing(TipComment::getWhenCreated))
				.map(TipCommentReadModel::new)
				.collect(Collectors.toList());
		this.whenCreated = tip.getWhenCreated();
		this.whenNotificationSent = tip.getUsersNotified();
	}
	
	public TipReadModel(Tip tip, Podopieczny user) {
		this(tip);
		if(tip.getUsersRead().contains(user)) {
			this.tipStatusByUser = TipStatusByUser.READ;
		}
		else {
			this.tipStatusByUser = TipStatusByUser.NEW;
		}
		
	}

	public Long getId() {
		return id;
	}

	public String getBody() {
		return body;
	}

	public String getHeading() {
		return heading;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public boolean isLocalImagePresent() {
		return localImagePresent;
	}

	public List<TipCommentReadModel> getComments() {
		return comments;
	}

	public LocalDateTime getWhenCreated() {
		return whenCreated;
	}

	public TipStatusByUser getTipStatusByUser() {
		return tipStatusByUser;
	}

	public void setTipStatusByUser(TipStatusByUser tipStatusByUser) {
		this.tipStatusByUser = tipStatusByUser;
	}
	
	@JsonIgnore
	public LocalDateTime getWhenNotificationSent() {
		return whenNotificationSent;
	}

	@JsonIgnore
	public int getCommentsAmount() {
		return getComments().size();
	}
	

}
