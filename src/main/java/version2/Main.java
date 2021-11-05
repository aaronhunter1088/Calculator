//package version2;
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//// Main is always going to open in Standard View but because it is a view
//// in how we are designing our project, View will be separated from the
//// main method and its class.
//@SuppressWarnings("all")
//public class Main extends Application {
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(Main.class.getResource("/version2/StandardView.fxml" ));
//        primaryStage.setTitle("Calculator");
//        primaryStage.setMinHeight(501);
//        primaryStage.setMinWidth(332);
//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();
//
//    }
//}
