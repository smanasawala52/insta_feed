package api.insta.feed;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.websocket.server.PathParam;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
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
			System.out.println(line);
			JSONObject jsonUserInfo = new JSONObject(line);
			String owner = jsonUserInfo.getJSONObject("data").getJSONObject("user").getString("id");
			modelAndView.addObject("owner", owner);
			modelAndView.addObject("username", username);
			modelAndView.addAllObjects(populateInstaUserFollowerPage(owner, "").getModelMap());
			modelAndView.addObject("followers_count", jsonUserInfo.getJSONObject("data").getJSONObject("user")
					.getJSONObject("edge_followed_by").getString("count"));

			modelAndView.addObject("biography",
					jsonUserInfo.getJSONObject("data").getJSONObject("user").getString("biography"));
			modelAndView.addObject("profilePicUrl",
					jsonUserInfo.getJSONObject("data").getJSONObject("user").getString("profile_pic_url"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modelAndView;
	}

	@GetMapping("/instaFollowersPage/{owner}/{maxId}")
	public ModelAndView populateInstaUserFollowerPage(@PathParam("owner") String owner,
			@PathParam("maxId") String maxId) {
		ModelAndView modelAndView = new ModelAndView("instadetails");
		try {
			// basic info
			URL obj = new URL("https://i.instagram.com/api/v1/friendships/" + owner
					+ "/followers/?count=12&search_surface=follow_list_page&max_id=" + maxId);
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
			System.out.println(line);
			JSONObject jsonUserInfo = new JSONObject(line);
			Map<String, List<String>> followers = new LinkedHashMap<>();
			JSONArray followerJson = jsonUserInfo.getJSONArray("users");
			modelAndView.addObject("owner", owner);
			for (int i = 0; i < followerJson.length(); i++) {
				JSONObject object = (JSONObject) followerJson.get(i);
				try {
					String pkFollower = object.getString("pk");
					String usernameFollower = object.getString("username");
					String fullNameFollower = object.getString("full_name");
					String profilePicUrlFollower = object.getString("profile_pic_url");
					List<String> follower = followers.get(pkFollower);
					if (follower == null) {
						follower = new ArrayList<String>();
					}
					follower.add(pkFollower);
					follower.add(usernameFollower);
					follower.add(fullNameFollower);
					if (profilePicUrlFollower != null) {
						follower.add(profilePicUrlFollower);
					}
					followers.put(pkFollower, follower);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			modelAndView.addObject("followers", followers);
			modelAndView.addObject("maxId", jsonUserInfo.getString("next_max_id"));
			System.out.println(followers);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modelAndView;
	}

}
