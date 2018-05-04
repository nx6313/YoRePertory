package com.mtxyao.nxx.yorepertory.entity;

public class Commodity {
    private String imgUrl;
    private String name;
    private String leiJiKuCun;
    private String price;
    private String currentKuCun;

    public Commodity(String imgUrl, String name, String leiJiKuCun, String price, String currentKuCun) {
        this.imgUrl = imgUrl;
        this.name = name;
        this.leiJiKuCun = leiJiKuCun;
        this.price = price;
        this.currentKuCun = currentKuCun;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeiJiKuCun() {
        return leiJiKuCun;
    }

    public void setLeiJiKuCun(String leiJiKuCun) {
        this.leiJiKuCun = leiJiKuCun;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrentKuCun() {
        return currentKuCun;
    }

    public void setCurrentKuCun(String currentKuCun) {
        this.currentKuCun = currentKuCun;
    }
}
