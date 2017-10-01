package com.evjeny.learner.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Evjeny on 01.07.2017 18:17.
 */

public class JSONIO {
    public JSONIO() {}

    public String getJson(ArrayList<LearnItem> list) {
        JSONObject out = new JSONObject();
        JSONArray words = new JSONArray();
        for (LearnItem i: list) {
            JSONObject item = new JSONObject();
            item.put("title", i.getName());
            item.put("content", i.getContent());
            item.put("html", i.getHtml());
            words.add(item);
        }
        out.put("items", words);
        return out.toString();
    }

    public ArrayList<LearnItem> getList(String json) throws ParseException {
        ArrayList<LearnItem> result = new ArrayList();
        JSONParser parser = new JSONParser();
        JSONObject root = (JSONObject) parser.parse(json);
        JSONArray items = (JSONArray) root.get("items");
        for(int i = 0; i<items.size(); i++) {
            JSONObject curr = (JSONObject) items.get(i);
            LearnItem item = new LearnItem((String) curr.get("title"), (String) curr.get("content"),
                    (String) curr.get("html"));
            result.add(item);
        }
        return result;
    }

    public ArrayList<LearnItem> getList(String[] jsons) throws IOException, ParseException {
        ArrayList<LearnItem> result = new ArrayList<>();
        for(String json: jsons) {
            result.addAll(getList(json));
        }
        return result;
    }
}
