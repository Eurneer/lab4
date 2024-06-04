import javafx.scene.control.TextArea;

import java.util.ArrayList;

public class InfoAboutPlace {

    String cid;
    String title;
    String author;
    String ldate;
    String desc;
    ArrayList<Region> regions;

    public String getAuthor() {
        String strAuthor = "отсутствует";

        if (author != null){
            strAuthor = author;
        }
        return strAuthor;
    }

    public String getRegions() {
        String regString = "";

        for (int i = 0; i < regions.size(); i++) {
            regString += regions.get(i).getTitle_local();
            if (i != regions.size() - 1){
                regString += ", ";
            }
        }
        return regString;
    }

    public String getData() {
        String[] buffer = ldate.split("T");

        String strDMY = buffer[0];
        String[] dataDMY = strDMY.split("-");

        String data = dataDMY[2] + "." + dataDMY[1] + "." + dataDMY[0];

        return data;
    }

    public String getTime() {
        String[] buffer = ldate.split("T");

        String strTime = buffer[1];
        String[] dataTime = strTime.split("\\.");

        return  dataTime[0];
    }

    public String getDesc() {
        String newStr = "отсутствует";
        String quots = "&quot;";
        if (desc != null) {
            newStr = desc.replaceAll(quots, "\"");
        }

        return newStr;
    }
}