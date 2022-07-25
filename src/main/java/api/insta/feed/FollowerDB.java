package api.insta.feed;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "followers", uniqueConstraints = {
		@UniqueConstraint(name = "UniqueUserAndFollower", columnNames = { "username", "follower_username" }) })
public class FollowerDB implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8948782228251759376L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "username")
	private String username;

	@Column(name = "follower_username")
	private String followerUsername;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFollowerUsername() {
		return followerUsername;
	}

	public void setFollowerUsername(String followerUsername) {
		this.followerUsername = followerUsername;
	}

}
