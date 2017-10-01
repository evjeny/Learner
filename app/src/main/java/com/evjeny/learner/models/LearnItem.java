package com.evjeny.learner.models;

/**
 * Created by Evjeny on 30.06.2017 19:34.
 */

public class LearnItem {
    private String name = "";
    private String content = "";
    private String html = "";

    public LearnItem(String name, String content, String html) {
        this.name = name;
        this.content = content;
        this.html = html;
    }

    public LearnItem(String name) {
        this.name = name;
    }

    public LearnItem() {}

    public String getHtml() {
        return html;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
