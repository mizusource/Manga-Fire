package com.fire.mangareader.domain.model.config;

import java.util.List;

public class Config {
    private String host1;
    private String host2;
    private String path;
    private List<String> fields;

    public Config(String host1, String host2, String path, List<String> fields) {
        this.host1 = host1;
        this.host2 = host2;
        this.path = path;
        this.fields = fields;
    }

    public String getHost1() { return host1; }
    public String getHost2() { return host2; }
    public String getPath() { return path; }
    public List<String> getFields() { return fields; }
}
