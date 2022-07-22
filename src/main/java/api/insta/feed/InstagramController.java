package api.insta.feed;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.websocket.server.PathParam;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class InstagramController {

	@GetMapping("/")
	public ModelAndView getHome(@RequestParam final Map<String, String> queryParams) {
		ModelAndView modelAndView = new ModelAndView("home");
		return modelAndView;
	}

	@GetMapping("/instadetails")
	public ModelAndView populateInstaUserDetails(@PathParam("username") String username) {
		ModelAndView modelAndView = new ModelAndView("instadetails");
		try {
			// basic info
			URL obj = new URL("https://i.instagram.com/api/v1/users/web_profile_info/?username=" + username);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestMethod("GET");
			// add request header
			con.setRequestProperty("Accept-Encoding", "gzip");
			con.setRequestProperty("cookie",
					"sessionid=8485620030:WM9DLpiiVsYD4E:11:AYf2u72g7nTjxWXW-v4X-Z46fvCADtb4yQd-ZxZJFQ;");
			con.setRequestProperty("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
			con.setRequestProperty("x-ig-app-id", "936619743392459");

			int responseCode = con.getResponseCode();
			// System.out.println(con.getHeaderFields());
//			System.out.println("\nSending 'GET' request to URL : " + obj);
//			System.out.println("Response Code : " + responseCode);
			// reader = new InputStreamReader(con.getInputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(con.getInputStream())));
			String inputLine = "";
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String line = response.toString();
			// print in String
//			System.out.println(line);
			JSONObject jsonUserInfo = new JSONObject(line);
			String owner = jsonUserInfo.getJSONObject("data").getJSONObject("user").getString("id");
			modelAndView.addObject("owner", owner);
			modelAndView.addObject("username", username);
			Follower follower = populateInstaUserFollowerPage(owner, "");
			if (follower != null && follower.getFollowers() != null) {
				modelAndView.addObject("followers", follower.getFollowers());
				modelAndView.addObject("maxId", follower.getMaxId() + "^");
				modelAndView.addObject("owner", follower.getOwner() + "^");
			}
			modelAndView.addObject("followers_count", jsonUserInfo.getJSONObject("data").getJSONObject("user")
					.getJSONObject("edge_followed_by").getString("count"));

			modelAndView.addObject("biography",
					jsonUserInfo.getJSONObject("data").getJSONObject("user").getString("biography"));
			String profilePicUrl = jsonUserInfo.getJSONObject("data").getJSONObject("user").getString("profile_pic_url")
					.replaceAll("\\u0026", "&");
			modelAndView.addObject("profilePicUrl", profilePicUrl);
			String imageBytes = getImageBytes(profilePicUrl);
			modelAndView.addObject("profilePicUrlData", imageBytes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modelAndView;
	}

	private String getImageBytes(String profilePicUrl) {
		try {
			System.out.println(profilePicUrl);
			URL obj = new URL(profilePicUrl);
//		System.out.println(obj);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setDoOutput(true);
			con.setRequestMethod("GET");
			// add request header
			con.setRequestProperty("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
			int responseCode = con.getResponseCode();
			// System.out.println(con.getHeaderFields());
			System.out.println("\nSending 'GET' request to URL : " + obj);
			System.out.println("Response Code : " + responseCode);
			// reader = new InputStreamReader(con.getInputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader((con.getInputStream())));
			String inputLine = "";
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String line = response.toString();
			// System.out.println(line);
			line = new String(Base64.getEncoder().encode(line.getBytes()));
			return line;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/instaFollowers/{owner}/{maxId}")
	public Follower populateInstaUserFollowerPage(@PathVariable("owner") String owner,
			@PathVariable("maxId") String maxId) {
		Follower followerResult = new Follower();
		try {
			// basic info
			URL obj = new URL("https://i.instagram.com/api/v1/friendships/" + owner.replace("^", "").replace("%5E", "")
					+ "/followers/?count=12&search_surface=follow_list_page&max_id="
					+ maxId.replace("^", "").replace("%5E", ""));
//			System.out.println(obj);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestMethod("GET");
			// add request header
			con.setRequestProperty("Accept-Encoding", "gzip");
			con.setRequestProperty("cookie",
					"sessionid=8485620030:WM9DLpiiVsYD4E:11:AYf2u72g7nTjxWXW-v4X-Z46fvCADtb4yQd-ZxZJFQ;");
			con.setRequestProperty("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
			con.setRequestProperty("x-ig-app-id", "936619743392459");

			int responseCode = con.getResponseCode();
			// System.out.println(con.getHeaderFields());
			System.out.println("\nSending 'GET' request to URL : " + obj);
			System.out.println("Response Code : " + responseCode);
			// reader = new InputStreamReader(con.getInputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(con.getInputStream())));
			String inputLine = "";
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String line = response.toString();
			// print in String
			// System.out.println(line);
			JSONObject jsonUserInfo = new JSONObject(line);
			List<List<String>> followers = new ArrayList<>();
			JSONArray followerJson = jsonUserInfo.getJSONArray("users");
			followerResult.setOwner(owner);
			for (int i = 0; i < followerJson.length(); i++) {
				JSONObject object = (JSONObject) followerJson.get(i);
				try {
					String pkFollower = object.getString("pk");
					String usernameFollower = object.getString("username");
					String fullNameFollower = object.getString("full_name");
					String profilePicUrlFollower = object.getString("profile_pic_url").replaceAll("\\u0026", "&");
					List<String> follower = new ArrayList<String>();
					follower.add(pkFollower);
					follower.add(usernameFollower);
					follower.add(fullNameFollower);
					if (profilePicUrlFollower != null) {
						follower.add(profilePicUrlFollower);
					}
					followers.add(follower);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			followerResult.setFollowers(followers);
			followerResult.setMaxId(jsonUserInfo.getString("next_max_id"));
			// System.out.println(followers);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return followerResult;
	}

}
