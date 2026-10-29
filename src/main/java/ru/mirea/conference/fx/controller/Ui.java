package ru.mirea.conference.fx.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.function.Consumer;

public final class Ui {
    private Ui() {}
    public static void info(String title,String message){alert(Alert.AlertType.INFORMATION,title,message);}
    public static void error(String title,String message){alert(Alert.AlertType.ERROR,title,message);}
    public static void warn(String title,String message){alert(Alert.AlertType.WARNING,title,message);}
    public static boolean confirm(String title,String message){
        Alert a=new Alert(Alert.AlertType.CONFIRMATION,message,ButtonType.YES,ButtonType.NO);
        a.setTitle(title); a.setHeaderText(null);
        return a.showAndWait().orElse(ButtonType.NO)==ButtonType.YES;
    }
    private static void alert(Alert.AlertType type,String title,String message){
        Alert a=new Alert(type,message,ButtonType.OK); a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }
    public static <T> T openModal(String fxml,String title,Node owner,Consumer<T> setup) throws IOException {
        FXMLLoader loader=new FXMLLoader(Ui.class.getResource("/fxml/"+fxml));
        Parent root=loader.load(); T controller=loader.getController(); setup.accept(controller);
        Stage stage=new Stage(); stage.initModality(Modality.APPLICATION_MODAL);
        if(owner!=null && owner.getScene()!=null) stage.initOwner(owner.getScene().getWindow());
        stage.setTitle(title);
        Scene scene=new Scene(root);
        var css=Ui.class.getResource("/css/style.css");
        if(css!=null) scene.getStylesheets().add(css.toExternalForm());
        stage.setScene(scene); stage.setResizable(false); stage.showAndWait();
        return controller;
    }
}