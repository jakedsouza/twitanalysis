package com.cloud.poly.twitter;

import com.cloud.poly.twitter.model.TwitResultModel;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.Gson;
import com.sun.jndi.toolkit.url.Uri;

@SuppressWarnings("serial")
public class TwitterSentimentAnalysisServlet extends HttpServlet {

	public static HashMap<String, Integer> sentimentMap;

	@Override
	public void init() throws ServletException {
		sentimentMap = new HashMap<>(2500);
		InputStream stream = getServletContext().getResourceAsStream("/WEB-INF/sentiment - Sheet 1.csv");
		 BufferedReader br = new BufferedReader( new InputStreamReader( stream ) );
		    StringBuffer text = new StringBuffer();
		    try{
		    for (String line; (line = br.readLine()) != null ;)
		        text.append( line );
		    }catch(IOException e){

		    }
		    String string = text.toString();
		    String[] row = string.split(",;");
		    for (int i = 0; i < row.length; i++) {
				String[] row2 = row[i].split(",");
				String word = row2[0];
				Integer value = Integer.parseInt(row2[1]);
				sentimentMap.put(word, value);
			}
		super.init();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String location = req.getParameter("location");
		String query = req.getParameter("query");
		System.out.println("Location :  " + location + "query " + query) ;
		URL googleUrl = null;
		URI uri2;
		try {
			// = new URI(str)
//			URI uri = new URI(
//					"http://maps.googleapis.com/maps/api/geocode/json?address="
//							+ location + "&sensor=false");
			uri2 = new URI("http", null, "maps.googleapis.com", 80, "/maps/api/geocode/json", "address="+location+"&sensor=false", null);
			googleUrl = uri2.normalize().toURL();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("uri " + googleUrl);
		JSONObject googleResult;
		JSONObject twitResult = null;
		List<TwitResultModel> twits = null ;
		try {
			googleResult = getRequest(googleUrl);

			Map<String, String> address = getAddressFromJson(googleResult);
			String formattedAddress = address.get("formattedAdress");
			address.put("q", query);
			address.put("rpp", "10");
			address.put("result_type", "recent");
			address.put("geocode",
					address.get("lat") + "," + address.get("lng") + ",1mi");
			address.remove("lat");
			address.remove("lng");
			address.remove("formattedAdress");

			URL twiturl = getTwitUrl(address);

			twitResult = getRequest(twiturl);
			 twits = getTwitResultsFromJson(twitResult);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		resp.getWriter().print(new Gson().toJson(twits));
	}

	public static Map<String, String> getAddressFromJson(JSONObject jsonObject)
			throws JSONException {
		Map<String, String> map = new HashMap<String, String>();
		jsonObject = jsonObject.getJSONArray("results").getJSONObject(0);
		String formattedAddress = jsonObject.getString("formatted_address");
		jsonObject = jsonObject.getJSONObject("geometry");
		JSONObject location = jsonObject.getJSONObject("location");
		String lng = location.getString("lng");
		String lat = location.getString("lat");
		// System.out.println(jsonObject);
		map.put("formattedAdress", formattedAddress);
		map.put("lng", lng);
		map.put("lat", lat);
		return map;
	}

	public static URL getTwitUrl(Map<String, String> queryMap)
			throws MalformedURLException, URISyntaxException {
		// URL uri = new URL(
		// "http://search.twitter.com:80/search.json?q=blue%20angels&rpp=5&include_entities=true&result_type=recent&geocode=37.781157,-122.398720,1mi");
		StringBuilder query1 = new StringBuilder();
		Set<String> keys = queryMap.keySet();
		for (String q : keys) {
			query1 = query1.append(q + "=" + queryMap.get(q) + "&");
		}

		URI uri2 = new URI("http", null, "search.twitter.com", 80,
				"/search.json", query1.toString(), null);

		return uri2.toURL();

	}

	public static JSONObject getRequest(URL url) throws IOException,
			JSONException {
		System.out.println("Fetching ");
		System.out.println(url.toString());
		URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
		JSONObject json = null;

		HTTPResponse response = fetcher.fetch(url);
		if (response.getResponseCode() != 200) {
			throw new NullPointerException();
		}
		byte[] content = response.getContent();
		String result = new String(response.getContent(), "UTF-8");
		json = new JSONObject(result);
		System.out.println("result");
		System.out.println(json);
		return json;

	}

	public static List<TwitResultModel> getTwitResultsFromJson(
			JSONObject jsonTwits) throws JSONException {
		JSONArray results = jsonTwits.getJSONArray("results");
		List<TwitResultModel> resultList = new ArrayList<TwitResultModel>();
		for (int i = 0; i < results.length(); i++) {
			Gson gson = new Gson();
			TwitResultModel model = gson.fromJson(results.getJSONObject(i)
					.toString(), TwitResultModel.class);
			// getting sentiment too
			model.setSentimentScore(calculateSentimentScore(model.getText()));

			resultList.add(model);
		}
		return resultList;

	}

	public static int calculateSentimentScore(String text) {
		String words[] = text.split(" ");
		int score = 0;
		for (int i = 0; i < words.length; i++) {
			if(sentimentMap.containsKey(words[i])){
				int v = sentimentMap.get(words[i]);
				score = v + score ;
			}
		}
		return score;

	}

}
