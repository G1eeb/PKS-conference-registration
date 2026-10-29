package ru.mirea.conference.fx;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class ConferenceFxApplication extends Application {
 @Override public void start(Stage stage) throws Exception {
  FXMLLoader loader=new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
  Scene scene=new Scene(loader.load(),1280,760);
  scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
  stage.setTitle("Conference Registration — КР №2");
  stage.setMinWidth(1050); stage.setMinHeight(650); stage.setScene(scene); stage.show();
 }
 public static void main(String[] args){launch(args);}
}