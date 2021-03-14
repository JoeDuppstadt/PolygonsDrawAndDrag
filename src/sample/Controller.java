package sample;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Controller {
    private enum Mode {DRAW, NONE}
    private Mode mode = Mode.NONE;
    private double X1 = 0, Y1 = 0, X2 = 0, Y2 = 0;
    private Polygon selectedPolygon; //current selected polygon
    private boolean checkIfDragged = false;
    private final ObservableList<Double> pointList = FXCollections.observableArrayList();//keeps a list of all points
    private final List<Polygon> polygonList = new ArrayList<>(); // keeps a list of all added polygons

    //pointList.size() >= 4 (this is just making sure we have a valid polygon)
    @FXML
    private Button drawButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Pane drawPane;

    @FXML
    private void drawButtonClicked() {
        mode = Mode.DRAW;
        clearButton.setDisable(false);
        deleteButton.setDisable(true);
    }

    @FXML
    private void clearButtonClicked() {
        drawPane.getChildren().clear();
        polygonList.clear();
        deleteButton.setDisable(true);
        drawButton.setDisable(false);
    }

    @FXML
    private void deleteButtonClicked() {
        mode = Mode.NONE;
        deleteButton.setDisable(true);
        drawPane.getChildren().remove(selectedPolygon);
        polygonList.remove(selectedPolygon);
        drawButton.setDisable(false);
        clearButton.setDisable(drawPane.getChildren().isEmpty());
    }

    @FXML
    private void drawPaneMouseReleased() {
        if (checkIfDragged) {
            checkIfDragged = false;

            double[] pointsOfPolygon = new double[selectedPolygon.getPoints().size()];
            for (int i = 0; i < pointsOfPolygon.length; i++) { // sets all the points if the polygon is dragged
                if (i % 2 == 0) { // this will get x cords because X cords are stored on numbers divisible by 2
                    pointsOfPolygon[i] = selectedPolygon.getPoints().get(i) + selectedPolygon.getTranslateX();
                } else { // with will get y cords
                    pointsOfPolygon[i] = selectedPolygon.getPoints().get(i) + selectedPolygon.getTranslateY();
                }
            }
            Polygon polygon = new Polygon(pointsOfPolygon);
            polygon.setStroke(Color.BLACK);
            polygon.setFill(Color.WHITE);
            drawPane.getChildren().add(polygon);
            polygonList.add(polygon);
            drawPane.getChildren().remove(selectedPolygon);
            polygonList.remove(selectedPolygon);
            selectedPolygon = null;
            deleteButton.setDisable(true);
            clearButton.setDisable(false);
            drawButton.setDisable(false);
        }
    }

    @FXML
    private void drawPaneMousePressed(MouseEvent e) {
        if (mode != mode.DRAW && selectedPolygon != null && selectedPolygon.contains(e.getX(), e.getY())) {
            X2 = e.getX();
            Y2 = e.getY();
            checkIfDragged = true;
        } else {
            checkIfDragged = false;
        }
        deleteButton.setDisable(false);
        clearButton.setDisable(true);
        drawButton.setDisable(true);
    }

    @FXML
    private void drawPaneMouseDragged(MouseEvent e) {
        if (checkIfDragged) {
            selectedPolygon.setTranslateX(e.getX() - X2);
            selectedPolygon.setTranslateY(e.getY() - Y2);

        }
        deleteButton.setDisable(false);
        clearButton.setDisable(true);
        drawButton.setDisable(true);
    }

    @FXML
    private void drawPaneMouseClicked(MouseEvent e) {
        if (mode == mode.DRAW) {
            X1 = e.getX();
            Y1 = e.getY();
            pointList.add(X1);
            pointList.add(Y1);
            // i don't think you can put an observableList into a polygon (at least i wasn't allowed to)
            double[] pointListArray = new double[pointList.size()];
            for (int i = 0; i < pointListArray.length; i++) {
                pointListArray[i] = pointList.get(i);
            }
            if(getSelected(e.getX(), e.getY()) == null){
                drawButton.setDisable(false);
                deleteButton.setDisable(true);
                clearButton.setDisable(false);
            }

            Polygon drawPolygon = new Polygon(pointListArray);
            drawPolygon.setFill(Color.WHITE);
            drawPolygon.setStroke(Color.RED);
            polygonList.add(drawPolygon);
            deleteButton.setDisable(false);

            if (pointList.size() > 3) {
                drawPane.getChildren().remove(polygonList.get(polygonList.size() - 2));
                polygonList.remove(polygonList.size() - 2);
            }
            drawPane.getChildren().add(drawPolygon);

            if (pointList.size() > 3 && e.getClickCount() == 2) {
                drawPolygon.setStroke(Color.BLACK);
                pointList.clear();
                mode = mode.NONE; // so i cant draw anymore
                drawButton.setDisable(false);
                clearButton.setDisable(false);
                deleteButton.setDisable(true);
            }
        } else {
            for (int i = 0; i < polygonList.size(); i++) {
                int j = i;
                if (selectedPolygon != null) {
                    j = ((i + polygonList.indexOf(selectedPolygon) + 1) % polygonList.size());
                    selectedPolygon.setStroke(Color.BLACK);
                }
                if (polygonList.get(j).contains(e.getX(), e.getY())) {
                    selectedPolygon = polygonList.get(j);
                    selectedPolygon.setStroke(Color.RED);
                    break;
                }

            }
        }
    }

    public void initialize() {
        drawButton.setDisable(false);
        deleteButton.setDisable(true);
        clearButton.setDisable(true);
    }

    Polygon getSelected(double x, double y) {
        Polygon sh;
        for (Node n: drawPane.getChildren()) {
            if (((Polygon)n).contains(x, y)) {
                sh = (Polygon) n;
                return sh;
            }
        }
        return null;
    }
}