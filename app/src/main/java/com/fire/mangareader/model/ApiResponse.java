package com.fire.mangareader.model;

import java.util.List;

public class ApiResponse<T> {
    private String result;
    private List<T> data;
    private int total;

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
}
