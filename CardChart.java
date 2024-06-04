import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


import java.util.ArrayList;

public class CardChart extends VBox {
    HBox boxOption;
    HBox boxChart;
    ArrayList<String[]> years = new ArrayList();

    public CardChart(Place[] places, WindowInfo window){
        boxChart = new HBox();
        boxOption = new HBox();
        boxOption.setAlignment(Pos.BASELINE_CENTER);

        for(Place place : places){
            plusPicture(place.year);
        }

        createButtonBack(window);
        createChart();

        this.getChildren().addAll(boxChart, boxOption);
    }

    private void createChart() {
        ArrayList<PieChart.Data> arrayList = createListForChart();
        ObservableList<PieChart.Data> buffchart =
                FXCollections.observableList(arrayList);

        PieChart pieChart = new PieChart(buffchart);

        pieChart.setTitle("Диаграмма распределения по годам");

        for (PieChart.Data data : pieChart.getData()) {
            double percentage = (data.getPieValue() / pieChart.getData().stream().mapToDouble(PieChart.Data::getPieValue).sum()) * 100;
            data.setName(data.getName() + " (" + String.format("%.2f", percentage) + "%)");
        }

        this.boxChart.getChildren().add(pieChart);
    }

    private ArrayList<PieChart.Data> createListForChart() {
        ArrayList<PieChart.Data> arrayList = new ArrayList<>();
        for (int i = 0; i < years.size(); i++) {
            String[] cell = years.get(i);
            arrayList.add(new PieChart.Data(cell[0], Integer.parseInt(cell[1])));
        }

        return arrayList;
    }

    private void createButtonBack(WindowInfo window){
        Button buttonBACK = new Button("Обратно к карточке");
        buttonBACK.setAlignment(Pos.BOTTOM_CENTER);
        buttonBACK.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                window.changeScene("info");
            }
        });

        this.boxOption.getChildren().add(buttonBACK);
    }

    public void plusPicture(String year){
        String[] cell = null;
        for (int i = 0; i < years.size(); i++) {
            if(years.get(i)[0].equals(year)){
                cell = years.get(i);
                break;
            }
        }

        if (cell != null){
            int a = Integer.parseInt(cell[1]);
            a += 1;
            cell[1] = String.valueOf(a);
        } else {
            String[] newCell = {year, "1"};
            years.add(newCell);
        }
    }
}
