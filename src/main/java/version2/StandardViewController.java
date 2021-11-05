//package version2;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.TabPane;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.VBox;
//
//public class StandardViewController extends MainController {
//
//    @FXML protected BorderPane mainBorderPane;
//    @FXML protected VBox mainVBox; // remove @FXML later and refer them to the MainController @FXML component
//    @FXML protected TabPane mainHistoryMemoryTab; // remove @FXML later and refer them to the MainController @FXML component
//    @FXML protected static GridPane buttonsGridPane;
//    @Override
//    public String getView() { return view.getText(); }
//
//    @Override
//    public void setView(String view) {
//    }
//
//    public void initialize() {
//        /*buttonsGridPane.prefHeightProperty().bind(mainVBox.prefHeightProperty());
//        buttonsGridPane.prefWidthProperty().bind(mainVBox.prefWidthProperty());
//
//        mainVBox.heightProperty().addListener(
//                new ChangeListener<Number>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                        mainVBox.setPrefHeight(mainBorderPane.getPrefHeight());
//                    }
//                }
//        );
//        mainVBox.widthProperty().addListener(
//                new ChangeListener<Number>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                        mainVBox.setPrefWidth(mainBorderPane.getPrefWidth());
//                    }
//                }
//        );
//        buttonsGridPane.heightProperty().addListener(
//                new ChangeListener<Number>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                        buttonsGridPane.setPrefHeight(mainVBox.getPrefHeight());
//                    }
//                }
//        );
//        buttonsGridPane.widthProperty().addListener(
//                new ChangeListener<Number>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                        buttonsGridPane.setPrefWidth(mainVBox.getPrefWidth());
//                    }
//                }
//        );*/
//    }
//
//}
