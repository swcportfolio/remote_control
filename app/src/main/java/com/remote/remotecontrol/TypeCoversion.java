package com.remote.remotecontrol;

public class TypeCoversion {
    String typeStr ;
    String groStr ;
    int imageStr ;

    public String typeConversion(String type) {
        typeStr = type;
        switch (type){
            case "T":typeStr = "TV";break;
            case "N":typeStr = "OTT";break;
            case "C":typeStr = "STB";break;
            case "A":typeStr = "Audio";break;
            case "H":typeStr = "Lighting";break;
            case "Z":typeStr = "Air Conditioner";break;
            //("4","Projector","T")); // 삭제
            //("6","FAN","H"));// 삭제
        }
        return typeStr;
    }

    public String devGroupIdConversion(String GroId ){
       groStr = GroId;
        switch (GroId){
            case "0":groStr = "TV";break;
            case "1":groStr = "OTT";break;
            case "2":groStr = "STB";break;
            case "3":groStr = "Audio";break;
            case "4":groStr = "Projector";break;
            case "5":groStr = "Lighting";break;
            case "6":groStr = "Fan";break;
            case "7":groStr = "Air Conditioner";break;
        }
        return groStr;
    }
    public int imageConversion(String GroId ){

        switch (GroId){
            case "0":imageStr = R.drawable.ic_tv;break;
            case "1":imageStr = R.drawable.ic_ott;break;
            case "2":imageStr = R.drawable.ic_settopbox;break;
            case "3":imageStr = R.drawable.ic_audio;break;
            case "4":imageStr = R.drawable.ic_projector;break;
            case "5":imageStr = R.drawable.ic_light;break;
            case "6":imageStr = R.drawable.ic_fan;break;
            case "7":imageStr = R.drawable.ic_aircon;break;
        }
        return imageStr;
    }
}
