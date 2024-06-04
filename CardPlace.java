import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class CardPlace extends VBox {
    HBox boxOption; // top
    HBox boxCard;
    String urlFile;

    public CardPlace(InfoAboutPlace iaPlace, WindowInfo windowInfo) {
        this.boxOption = new HBox();
        boxOption.setAlignment(Pos.TOP_CENTER);
        boxOption.setPadding(new Insets(0, 0, 20, 0));
        this.boxCard = new HBox();

        createPicture(Integer.parseInt(iaPlace.cid));
        createButtonOption(boxOption, windowInfo);
        createImage(boxCard);
        createInfo(windowInfo, iaPlace);

        getChildren().addAll(boxOption, boxCard);
    }

    private void createInfo(WindowInfo windowInfo, InfoAboutPlace iaPlace) {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(0, 0, 0, 10));

        Label labelTitle = new Label(iaPlace.title);
        labelTitle.setFont(new Font("franklin gothic medium", 36));
        labelTitle.setWrapText(true);

        Label authorLabel = new Label("Автор: " + iaPlace.getAuthor());
        authorLabel.setFont(new Font("franklin gothic medium", 18));

        Label textLabel1 = new Label("Описание");
        textLabel1.setFont(new Font("franklin gothic medium", 24));
        textLabel1.setPadding(new Insets(32, 0, 0, 0));

        String[] str = checkLink(iaPlace.getDesc(), windowInfo);

        Label textLabel2 = new Label(str[0]);
        textLabel2.setFont(new Font("franklin gothic medium", 20));
        textLabel2.setWrapText(true);
        textLabel2.setPadding(new Insets(4, 0, 0, 0));

        Hyperlink hyperlink = null;
        if (str[1] != "" && str[2] != ""){
            hyperlink = new Hyperlink("* - " + str[2]);
            hyperlink.setBorder(null);
            hyperlink.setPadding(new Insets(8, 8, 0, 0));
            hyperlink.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    windowInfo.getHostServices().showDocument("https://pastvu.com" + str[1]);
                }
            });
        }

        Label regionLabel = new Label(iaPlace.getRegions());
        regionLabel.setFont(new Font("franklin gothic medium", 14));
        regionLabel.setWrapText(true);
        regionLabel.setPadding(new Insets(32, 32, 0, 0));

        Label dataLabel1 = new Label("Дата: " + iaPlace.getData());
        dataLabel1.setFont(new Font("franklin gothic medium", 14));
        dataLabel1.setWrapText(true);
        dataLabel1.setPadding(new Insets(32, 32, 0, 0));

        Label dataLabel2 = new Label("Время: " + iaPlace.getTime());
        dataLabel2.setFont(new Font("franklin gothic medium", 14));
        dataLabel2.setWrapText(true);
        dataLabel2.setPadding(new Insets(0, 16, 24, 0));

        Label cidLabel = new Label("CID: " + iaPlace.cid);
        cidLabel.setFont(new Font("franklin gothic medium", 14));
        cidLabel.setWrapText(true);
        cidLabel.setPadding(new Insets(0, 32, 8, 0));

        Hyperlink linkHTML = new Hyperlink("https://pastvu.com/p/" + iaPlace.cid);
        linkHTML.setFont(new Font("franklin gothic medium", 14));
        linkHTML.setPadding(new Insets(2, 0, 0, 0));
        linkHTML.setBorder(null);

        linkHTML.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                windowInfo.getHostServices().showDocument("https://pastvu.com/p/" + iaPlace.cid);
            }
        });

        vBox.getChildren().addAll(labelTitle, authorLabel, textLabel1, textLabel2);
        if (hyperlink != null){
            vBox.getChildren().add(hyperlink);
        }

        vBox.getChildren().addAll(regionLabel, dataLabel1, dataLabel2, cidLabel,
                linkHTML);

        createButtonSelect(vBox, windowInfo);

        this.boxCard.getChildren().add(vBox);

    }

    private void createImage(HBox hBox) {
        VBox vBox = new VBox();
        Image image = new Image(urlFile, 600, 600, true, false, true);
        ImageView imgView = new ImageView(image);

        vBox.getChildren().add(imgView);

        hBox.getChildren().add(vBox);

    }

    private void createPicture(int cid){
        String url = "https://pastvu.com/api2?method=photo.giveForPage&params={\"cid\": " + cid + "}";
        URLConnection connection = null;
        try {
            connection = new URL(url).openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection httpsconnection = null;
        if (connection instanceof HttpURLConnection) {
            httpsconnection = (HttpURLConnection) connection;

            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(httpsconnection.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String urlString = "";
            String current;

            while(true) {
                try {
                    if (!((current = in.readLine()) != null)) break;
                    urlString += current;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            createURLPicture(urlString);
        }
    }

    private void createURLPicture(String urlString){
        JsonObject convertedObject = new Gson().fromJson(urlString, JsonObject.class);

        JsonObject resultObject = convertedObject.getAsJsonObject("result")
                .getAsJsonObject("photo");

        String s = String.valueOf(resultObject.get("file"));

        s = s.replace("\"", ""); // убираем кавычки

        urlFile = "https://pastvu.com/_p/d/" + s;

    }

    private void createButtonOption(HBox hBox, WindowInfo window) {
        Button buttonBack = new Button("Вернуться на главную страницу");
        buttonBack.setAlignment(Pos.BASELINE_CENTER);
        buttonBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                window.changeScene("start");
            }
        });

        Button buttonDiagr = new Button("Посмотреть график");
        buttonBack.setAlignment(Pos.BASELINE_CENTER);
        buttonDiagr.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                window.changeScene("OpenChart");
            }
        });

        hBox.getChildren().addAll(buttonBack, buttonDiagr);
    }

    private void createButtonSelect(VBox box, WindowInfo window){
        HBox hBox = new HBox();
        Button buttonUP = new Button("->");
        buttonUP.setAlignment(Pos.BASELINE_LEFT);
        buttonUP.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                window.upPage();
            }
        });

        Button buttonDOWN = new Button("<-");
        buttonDOWN.setAlignment(Pos.BASELINE_RIGHT);
        buttonDOWN.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                window.downPage();
            }
        });

        hBox.getChildren().addAll(buttonDOWN, buttonUP);
        hBox.setPadding(new Insets(10, 0,0,0));
        box.getChildren().add(hBox);
    }

    private String[] checkLink(String text, WindowInfo window){
        char[] strBuf = text.toCharArray();
        int paddingLeft = 0;
        int paddingCenter = 0;
        int paddingRight = 0;
        
        for (int i = 0; i < strBuf.length; i++) {
            if (Character.toString(strBuf[i]).equals(">")) { //проверка середины элемента html (Строки)
                paddingCenter = i;                           //<a href="asas">asas</a>
                break;                                       //              |
            }                                                //             этот
        }

        for (int i = 0; i < strBuf.length; i++) {
            if (Character.toString(strBuf[i]).equals(">")) { //Проверка конечного элемента html (Строки)
                paddingRight = i;                            //<a href="asas">asas</a>
                                                             //                      |
            }                                                //                     этот
        }

        for (int i = 0; i < strBuf.length; i++) {
            if (Character.toString(strBuf[i]).equals("<")) { //Проверка начального элемента html (Строки)
                paddingLeft = i;                             //  <a href="asas">asas</a>
                break;                                       //  |
                                                             //  этот
            }
        }
        String left = "";
        String right = "";

        String newtext = "";

        String link = "";
        String nameLink = "";

        String[] mas = {newtext, link, nameLink};

        if (paddingCenter != 0 && paddingRight != 0 && paddingLeft != 0){

            for (int i = 0; i < paddingLeft; i++) {
                left += strBuf[i];
            }
            for (int i = paddingRight + 1; i < strBuf.length; i++) {
                right += strBuf[i];
            }

            for (int i = paddingCenter + 1; i < strBuf.length; i++) {
                if (Character.toString(strBuf[i]).equals("<")){
                    break;
                } else {
                    nameLink += strBuf[i];
                }
            }

            paddingCenter -= 2;
            for (int i = paddingCenter; paddingCenter > 0 ; i--) {
                if (Character.toString(strBuf[i]).equals("\"")){
                    break;
                } else {
                    link += strBuf[i];
                }
            }
            String reversedLink = reverse(link.toCharArray());

            mas[0] = left + nameLink + "*" + right;
            mas[1] = reversedLink;
            mas[2] = nameLink;

        } else {
            mas[0] = text;
        }

        return mas;
    }

    private String reverse(char[] str){
        char[] arr = new char[str.length];

        for (int i = 0; i < str.length; i++) {
            arr[str.length - 1 - i] = str[i];
        }
        String string = "";
        for (int i = 0; i < arr.length; i++) {
            string += arr[i];
        }

        return string;
    }
}
