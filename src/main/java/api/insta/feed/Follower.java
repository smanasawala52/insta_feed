package api.insta.feed;

import java.util.ArrayList;
import java.util.List;

public class Follower {
	private List<List<String>> followers = new ArrayList<>();
	private String maxId = "";
	private String owner = "";

	public String getMaxId() {
		return maxId;
	}

	public void setMaxId(String maxId) {
		this.maxId = maxId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public List<List<String>> getFollowers() {
		return followers;
	}

	public void setFollowers(List<List<String>> followers) {
		this.followers = followers;
	}

}
