package com.shizhefei.mvc.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

public class UrlBuilder {
    private String url;
    private LinkedList<String> paths = new LinkedList<String>();
    private LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();

    public UrlBuilder(String url) {
        super();
        this.url = url;
    }

    public UrlBuilder sp(Object path) {
        paths.add(String.valueOf(path));
        return this;
    }

    public UrlBuilder param(String param, String value) {
        params.put(param, value);
        return this;
    }

    public UrlBuilder param(String param, int value) {
        params.put(param, value);
        return this;
    }

    public UrlBuilder param(String param, boolean value) {
        params.put(param, value);
        return this;
    }

    public UrlBuilder param(String param, double value) {
        params.put(param, value);
        return this;
    }

    public UrlBuilder param(String param, float value) {
        params.put(param, value);
        return this;
    }

    public UrlBuilder param(String param, byte value) {
        params.put(param, value);
        return this;
    }

    public UrlBuilder param(String param, long value) {
        params.put(param, value);
        return this;
    }

    public UrlBuilder params(Map<String, Object> p) {
        params.putAll(p);
        return this;
    }

    public String build() {
        StringBuilder builder = new StringBuilder(url);
        if (builder.length() > 0 && builder.charAt(builder.length() - 1) == '/') {
            builder.deleteCharAt(builder.length() - 1);
        }
        for (String path : paths) {
            builder.append("/").append(path);
        }
        if (!params.isEmpty()) {
            if (builder.indexOf("?") < 0)
                builder.append('?');
            for (Entry<String, ?> entry : params.entrySet()) {
                builder.append('&');
                builder.append(entry.getKey());
                builder.append('=');
                // url.append(String.valueOf(entry.getValue()));
                // 不做URLEncoder处理
                try {
                    builder.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString().replace("?&", "?");
    }
}
