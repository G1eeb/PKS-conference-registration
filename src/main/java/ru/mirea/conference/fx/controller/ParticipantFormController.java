package ru.mirea.conference.fx.controller;
import javafx.fxml.FXML; import javafx.scene.control.*; import javafx.stage.Stage;
import ru.mirea.conference.fx.util.ValidationUtil; import ru.mirea.conference.model.Participant;
public class ParticipantFormController {
 @FXML private TextField fullName,email,organization; private Participant result;
 public void setExisting(Participant p){result=new Participant(p.getId(),p.getFullName(),p.getEmail(),p.getOrganization(),p.getCreatedAt());fullName.setText(p.getFullName());email.setText(p.getEmail());organization.setText(p.getOrganization());}
 @FXML private void save(){try{String n=ValidationUtil.required(fullName.getText(),"ФИО",150),e=ValidationUtil.email(email.getText()),o=ValidationUtil.optional(organization.getText(),"Организация",150);if(result==null)result=new Participant(n,e,o);else{result.setFullName(n);result.setEmail(e);result.setOrganization(o);}close();}catch(Exception ex){Ui.error("Ошибка ввода",ex.getMessage());}}
 @FXML private void cancel(){result=null;close();} private void close(){((Stage)fullName.getScene().getWindow()).close();} public Participant getResult(){return result;}
}