//package version2;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TabPane;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.VBox;
//
//public abstract class MainController {
//
//    // Main Components
//    @FXML protected BorderPane mainBorderPane;
//    @FXML protected VBox mainVBox;
//    @FXML protected TabPane mainHistoryMemoryTab;
//
//    @FXML protected static GridPane buttonsGridPane;
//
//    // Buttons
//    @FXML protected final static Button memoryClearButton = new Button("MC");
//    @FXML protected final static Button memoryRecallButton = new Button("MR");
//    @FXML protected final static Button memoryAddButton = new Button("M+");
//    @FXML protected final static Button memorySubtractButton = new Button("M-");
//    @FXML protected final static Button memoryStoreButton = new Button("MS");
//    @FXML protected final static Button memoryViewButton = new Button("MView");
//    @FXML protected final static Button zeroButton = new Button("0");
//    @FXML protected final static Button oneButton = new Button("1");
//    @FXML protected final static Button twoButton = new Button("2");
//    @FXML protected final static Button threeButton = new Button("3");
//    @FXML protected final static Button fourButton = new Button("4");
//    @FXML protected final static Button fiveButton = new Button("5");
//    @FXML protected final static Button sixButton = new Button("6");
//    @FXML protected final static Button sevenButton = new Button("7");
//    @FXML protected final static Button eightButton = new Button("8");
//    @FXML protected final static Button nineButton = new Button("9");
//    @FXML protected final static Button aButton = new Button("A");
//    @FXML protected final static Button bButton = new Button("B");
//    @FXML protected final static Button cButton = new Button("C");
//    @FXML protected final static Button dButton = new Button("D");
//    @FXML protected final static Button eButton = new Button("E");
//    @FXML protected final static Button fButton = new Button("F");
//
//    @FXML protected final static Button percentButton = new Button("%");
//    @FXML protected final static Button sqrtButton = new Button("sqrtIcon");
//    @FXML protected final static Button exponentButton = new Button("exponentIcon");
//    @FXML protected final static Button fractionButton = new Button("fraction");
//    @FXML protected final static Button clearEntryButton = new Button("CE");
//    @FXML protected final static Button clearButton = new Button("C");
//    @FXML protected final static Button deleteButton = new Button("Delete");
//    @FXML protected final static Button negateButton = new Button("negateIcon");
//    @FXML protected final static Button divideButton = new Button("/");
//    @FXML protected final static Button multiplyButton = new Button("X");
//    @FXML protected final static Button subtractButton = new Button("-");
//    @FXML protected final static Button addButton = new Button("+");
//    @FXML protected final static Button equalsButton = new Button("=");
//    @FXML protected final static Button dotButton = new Button(".");
//
//    // Memory
//    List<String> memory = new ArrayList<String>(4);
//
//    // Label
//    @FXML
//    Label view = new Label("Standard");
//
//    // Abstract Methods
//    abstract String getView();
//    abstract void setView(String view);
//
//    @FXML
//    private void displayButtonBorder(MouseEvent mouseEvent) {
//    }
//
//    public void initialize() {
//        mainBorderPane.setMinHeight(501);
//        mainBorderPane.setMinWidth(332);
//        mainHistoryMemoryTab.setVisible(false);
//
//        buttonsGridPane.prefHeightProperty().bind(mainVBox.prefHeightProperty());
//        buttonsGridPane.prefWidthProperty().bind(mainVBox.prefWidthProperty());
//        buttonsGridPane.minHeightProperty().bind(mainVBox.minHeightProperty());
//        buttonsGridPane.minWidthProperty().bind(mainVBox.minWidthProperty());
//
//        mainVBox.heightProperty().addListener(
//                new ChangeListener<Number>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                        mainVBox.setPrefHeight(newValue.doubleValue());
//                    }
//                }
//        );
//        mainVBox.widthProperty().addListener(
//                new ChangeListener<Number>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                        mainVBox.setPrefWidth(newValue.doubleValue());
//                    }
//                }
//        );
//        buttonsGridPane.prefHeightProperty().addListener(
//                new ChangeListener<Number>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                        buttonsGridPane.setPrefHeight(mainVBox.getPrefHeight());
//                    }
//                }
//        );
//        buttonsGridPane.prefWidthProperty().addListener(
//                new ChangeListener<Number>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                        buttonsGridPane.setPrefWidth(mainVBox.getPrefWidth());
//                    }
//                }
//        );
//        buttonsGridPane.minHeightProperty().addListener(
//                new ChangeListener<Number>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                        buttonsGridPane.setMinHeight(mainVBox.getPrefHeight());
//                    }
//                }
//        );
//        buttonsGridPane.minWidthProperty().addListener(
//                new ChangeListener<Number>() {
//                    @Override
//                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                        buttonsGridPane.setMinWidth(mainVBox.getPrefWidth());
//                    }
//                }
//        );
//    }
//}
