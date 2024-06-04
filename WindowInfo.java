import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import javax.swing.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class WindowInfo extends Application {

    private TilePane panel; // чудесный тайл

    Place[] places = new Place[30];
    InfoAboutPlace[] iaplace = new InfoAboutPlace[30];

    static double coordX, coordY, distance;
    static double defaultX = 61, defaultY = 34, defaultDistance = 5000;

    final String[] labels = {"Координата X  ", "Координата Y  ", "Дистанция (м)"}; //массив названия лейблов
    final TextField[] textField = new TextField[labels.length]; //массив текстовых полей
    int labelsLength = labels.length;

    public static WindowInfo window;

    private int selectedID = 0;

    private boolean PlacesIsEmpty = true;

    public boolean getPlacesIsEmpty() {
        return PlacesIsEmpty;
    }

    void setPlacesIsEmpty(boolean b) {
        this.PlacesIsEmpty = b;
    }

    @Override
    public void start(Stage stage) {
        this.panel = new TilePane();
        panel.setOrientation(Orientation.VERTICAL);
        panel.setVgap(10);

        changeScene("start");

        Scene scene = new Scene(panel, 1280, 720);
        stage.setTitle("PastVU");
        stage.setScene(scene);
        stage.show();
        window = this;
    }

    public void changeToStart(TilePane panel){
       for (int i = 0; i < labelsLength; i++) {
            HBox hBox = new HBox();
            Label l = new Label(labels[i]);
            hBox.getChildren().add(l);
            textField[i] = new TextField();
            l.setLabelFor(textField[i]);
            hBox.getChildren().add(textField[i]);
            if (i != 2) {
                hBox.setSpacing(11);
            } else {
                hBox.setSpacing(10);
            }
            panel.getChildren().add(hBox);
        }

        Button button = new Button("Найти интересные места");

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (textField[0].getText().isEmpty() || textField[1].getText().isEmpty() || textField[2].getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Все поля должны быть заполнены!");
                } else {

                    try {

                        Double.parseDouble(textField[0].getText());
                        Double.parseDouble(textField[1].getText());
                        double dis = Double.parseDouble(textField[2].getText());

                        if (dis <= 1000000)
                        {
                            connect();
                            changeScene("info");

                        }else  JOptionPane.showMessageDialog(null, "Слишком уж большая дистанция, она должна быть меньше или равна 1000 км");


                    }
                    catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Пожалуйста, введите числа!");
                    }

                }
            }
        });
        panel.getChildren().add(button);

        textField[0].setText(Double.toString(defaultX));
        textField[1].setText(Double.toString(defaultY));
        textField[2].setText(Double.toString(defaultDistance));
    }

    public void changeScene(String name){
        panel.getChildren().clear();

        switch (name){
            case "start":
                panel.setAlignment(Pos.CENTER);
                changeToStart(panel);
                break;

            case "info":

                if (!getPlacesIsEmpty())
                {
                    panel.setAlignment(Pos.CENTER_LEFT);
                    panel.setPadding(new Insets(0,0,0,10));  //(top/right/bottom/left)
                    createCard();
                } else
                {
                    JOptionPane.showMessageDialog(null, "К сожалению, по данным значениям фотографии прошлого не нашлись");
                    changeScene("start");
                }
                break;

            case "OpenChart":
                panel.setAlignment(Pos.CENTER);
                panel.setPadding(new Insets(0,0,0,10));
                createCardChart();
                break;
        }
    }

    public void upPage(){
        if (iaplace[selectedID + 1] != null){
            selectedID += 1;
            createCard();
        }
    }

    public void downPage(){
        if (selectedID != 0) {
            if (iaplace[selectedID - 1] != null){
                selectedID -= 1;
                createCard();
            }
        }
    }

    public void createCardChart(){
        CardChart cardChart = new CardChart(places, window);
        panel.getChildren().add(cardChart);
    }

    public void createCard(){
        panel.getChildren().clear();
        CardPlace cardPlace = new CardPlace(iaplace[selectedID], window);
        panel.getChildren().add(cardPlace);
    }

    public void getPlaces(Place[] places){
        this.places = places;
    }

    public void getInfoPlaces(InfoAboutPlace[] iaPlaces){
        this.iaplace = iaPlaces;
    }

    public void connect(){
        try {
            coordX = Double.parseDouble(textField[0].getText());
            coordY = Double.parseDouble(textField[1].getText());
            distance = Double.parseDouble(textField[2].getText());

            RequestProcessing RP = new RequestProcessing();

            String url = "https://pastvu.com/api2?method=photo.giveNearestPhotos&params={\"geo\":[" + coordX + ", " + coordY + "], \"limit\":15, \"distance\":" + distance + "}";

            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("Content-Type", "application/json");

            System.out.println(url);

            if (RP.getStatusCode(connection) == HttpURLConnection.HTTP_OK) {

                RP.getNearestPhotos(connection, this);

            } else
            {
                JOptionPane.showMessageDialog(null, "Не удалось отправить запрос, проверьте подключение к Интернету");
            }

        } catch (NumberFormatException | IOException ex) {

            JOptionPane.showMessageDialog(null, "Проверьте вводимые поля!");

        }
    }

    public static void main(String[] args) {
        launch();
    }


}
