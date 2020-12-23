package com.remote.remotecontrol.gridview;

public class SingerItem { // singerViewer 어뎁터에서 필요한 아이템을 하나의 객체로 만들어 정의
    private String name;
    private int image;

    public SingerItem(String name ,int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
