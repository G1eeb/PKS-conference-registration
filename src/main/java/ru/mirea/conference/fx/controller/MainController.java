package ru.mirea.conference.fx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import ru.mirea.conference.model.*;
import ru.mirea.conference.service.*;
import ru.mirea.conference.util.ExcelExporter;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {
    private final ParticipantService participantService=new ParticipantService();
    private final ConferenceService conferenceService=new ConferenceService();
    private final RegistrationService registrationService=new RegistrationService();

    @FXML private TableView<Participant> participantTable;
    @FXML private TableColumn<Participant,Number> participantIdCol;
    @FXML private TableColumn<Participant,String> participantNameCol,participantEmailCol,participantOrgCol,participantCreatedCol;
    @FXML private TextField participantSearch;
    @FXML private ComboBox<String> participantOrgFilter,participantSort;

    @FXML private TableView<Conference> conferenceTable;
    @FXML private TableColumn<Conference,Number> conferenceIdCol;
    @FXML private TableColumn<Conference,String> conferenceTitleCol,conferenceTrackCol,conferenceStartCol,conferenceEndCol,conferenceLocationCol;
    @FXML private TextField conferenceSearch;
    @FXML private ComboBox<String> conferenceTrackFilter,conferenceSort;

    @FXML private TableView<Registration> registrationTable;
    @FXML private TableColumn<Registration,Number> registrationIdCol;
    @FXML private TableColumn<Registration,String> registrationParticipantCol,registrationConferenceCol,registrationStatusCol,registrationDateCol;
    @FXML private TextField registrationSearch;
    @FXML private ComboBox<String> registrationStatusFilter,registrationConferenceFilter,registrationSort;

    @FXML private Label statParticipants,statConferences,statRegistrations,statActive,statAttended,statCancelled,statTracks,connectionStatus;
    private final ObservableList<Participant> participants=FXCollections.observableArrayList();
    private final ObservableList<Conference> conferences=FXCollections.observableArrayList();
    private final ObservableList<Registration> registrations=FXCollections.observableArrayList();

    @FXML public void initialize(){
        participantSort.getItems().setAll("Без сортировки","По ФИО ↑","По дате ↓");
        conferenceSort.getItems().setAll("Без сортировки","По дате ↑","По названию ↑");
        registrationSort.getItems().setAll("Без сортировки","По дате ↑","По статусу ↑");
        setupTables();
        participantSearch.textProperty().addListener((o,a,b)->refreshParticipants());
        participantOrgFilter.valueProperty().addListener((o,a,b)->refreshParticipants());
        participantSort.valueProperty().addListener((o,a,b)->refreshParticipants());
        conferenceSearch.textProperty().addListener((o,a,b)->refreshConferences());
        conferenceTrackFilter.valueProperty().addListener((o,a,b)->refreshConferences());
        conferenceSort.valueProperty().addListener((o,a,b)->refreshConferences());
        registrationSearch.textProperty().addListener((o,a,b)->refreshRegistrations());
        registrationStatusFilter.valueProperty().addListener((o,a,b)->refreshRegistrations());
        registrationConferenceFilter.valueProperty().addListener((o,a,b)->refreshRegistrations());
        registrationSort.valueProperty().addListener((o,a,b)->refreshRegistrations());
        refreshAll();
    }
    private void setupTables(){
        participantIdCol.setCellValueFactory(d->new javafx.beans.property.SimpleLongProperty(d.getValue().getId()));
        participantNameCol.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue().getFullName()));
        participantEmailCol.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue().getEmail()));
        participantOrgCol.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(Optional.ofNullable(d.getValue().getOrganization()).orElse("—")));
        participantCreatedCol.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue().getCreatedAt()==null?"":d.getValue().getCreatedAt().toLocalDate().toString()));
        conferenceIdCol.setCellValueFactory(d->new javafx.beans.property.SimpleLongProperty(d.getValue().getId()));
        conferenceTitleCol.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue().getTitle()));
        conferenceTrackCol.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue().getTrack().name()));
        conferenceStartCol.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue().getStartDate().toString()));
        conferenceEndCol.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue().getEndDate().toString()));
        conferenceLocationCol.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(Optional.ofNullable(d.getValue().getLocation()).orElse("—")));
        registrationIdCol.setCellValueFactory(d->new javafx.beans.property.SimpleLongProperty(d.getValue().getId()));
        registrationParticipantCol.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue().getParticipantName()));
        registrationConferenceCol.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue().getConferenceTitle()));
        registrationStatusCol.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(rusStatus(d.getValue().getStatus())));
        registrationDateCol.setCellValueFactory(d->new javafx.beans.property.SimpleStringProperty(d.getValue().getRegisteredAt()==null?"":d.getValue().getRegisteredAt().toString()));
    }
    private String rusStatus(RegistrationStatus s){return switch(s){case CREATED->"Создана";case CONFIRMED->"Подтверждена";case PAID->"Оплачена";case ATTENDED->"Посетил";case CANCELLED->"Отменена";};}
    @FXML private void refreshAll(){
        try{
            boolean ok=ru.mirea.conference.util.DatabaseManager.testConnection();
            connectionStatus.setText(ok?"● PostgreSQL подключён":"● PostgreSQL недоступен");
            participants.setAll(participantService.getAll()); conferences.setAll(conferenceService.getAll()); registrations.setAll(registrationService.getAll());
            refreshParticipantFilters(); refreshConferenceFilters(); refreshRegistrationFilters();
            refreshParticipants(); refreshConferences(); refreshRegistrations(); refreshStats();
        }catch(Exception e){connectionStatus.setText("● Ошибка БД");Ui.error("Ошибка загрузки",message(e));}
    }
    private void refreshParticipantFilters(){
        String old=participantOrgFilter.getValue();
        participantOrgFilter.getItems().setAll("Все организации");
        participants.stream().map(Participant::getOrganization).filter(Objects::nonNull).filter(s->!s.isBlank()).distinct().sorted().forEach(s->participantOrgFilter.getItems().add(s));
        participantOrgFilter.setValue(old!=null&&participantOrgFilter.getItems().contains(old)?old:"Все организации");
        if(participantSort.getValue()==null) participantSort.setValue("Без сортировки");
    }
    private void refreshConferenceFilters(){
        String old=conferenceTrackFilter.getValue(); conferenceTrackFilter.getItems().setAll("Все треки");
        for(ConferenceTrack t:ConferenceTrack.values()) conferenceTrackFilter.getItems().add(t.name());
        conferenceTrackFilter.setValue(old!=null&&conferenceTrackFilter.getItems().contains(old)?old:"Все треки");
        if(conferenceSort.getValue()==null) conferenceSort.setValue("Без сортировки");
    }
    private void refreshRegistrationFilters(){
        String oldC=registrationConferenceFilter.getValue(), oldS=registrationStatusFilter.getValue();
        registrationStatusFilter.getItems().setAll("Все статусы"); for(RegistrationStatus s:RegistrationStatus.values()) registrationStatusFilter.getItems().add(s.name());
        registrationStatusFilter.setValue(oldS!=null&&registrationStatusFilter.getItems().contains(oldS)?oldS:"Все статусы");
        registrationConferenceFilter.getItems().setAll("Все конференции"); conferences.forEach(c->registrationConferenceFilter.getItems().add(c.getId()+" — "+c.getTitle()));
        registrationConferenceFilter.setValue(oldC!=null&&registrationConferenceFilter.getItems().contains(oldC)?oldC:"Все конференции");
        if(registrationSort.getValue()==null) registrationSort.setValue("Без сортировки");
    }
    private void refreshParticipants(){
        String q=lower(participantSearch.getText()),org=participantOrgFilter.getValue();
        List<Participant> list=participants.stream().filter(p->q.isBlank()||p.getFullName().toLowerCase().contains(q)||p.getEmail().toLowerCase().contains(q)||Optional.ofNullable(p.getOrganization()).orElse("").toLowerCase().contains(q))
            .filter(p->org==null||org.equals("Все организации")||Objects.equals(p.getOrganization(),org)).collect(Collectors.toList());
        if("По ФИО ↑".equals(participantSort.getValue()))list.sort(Comparator.comparing(Participant::getFullName,String.CASE_INSENSITIVE_ORDER));
        if("По дате ↓".equals(participantSort.getValue()))list.sort(Comparator.comparing(Participant::getCreatedAt,Comparator.nullsLast(Comparator.reverseOrder())));
        participantTable.setItems(FXCollections.observableArrayList(list));
    }
    private void refreshConferences(){
        String q=lower(conferenceSearch.getText()),track=conferenceTrackFilter.getValue();
        List<Conference> list=conferences.stream().filter(c->q.isBlank()||c.getTitle().toLowerCase().contains(q)||Optional.ofNullable(c.getLocation()).orElse("").toLowerCase().contains(q))
            .filter(c->track==null||track.equals("Все треки")||c.getTrack().name().equals(track)).collect(Collectors.toList());
        if("По дате ↑".equals(conferenceSort.getValue()))list.sort(Comparator.comparing(Conference::getStartDate));
        if("По названию ↑".equals(conferenceSort.getValue()))list.sort(Comparator.comparing(Conference::getTitle,String.CASE_INSENSITIVE_ORDER));
        conferenceTable.setItems(FXCollections.observableArrayList(list));
    }
    private void refreshRegistrations(){
        String q=lower(registrationSearch.getText()),status=registrationStatusFilter.getValue(),cf=registrationConferenceFilter.getValue();
        List<Registration> list=registrations.stream().filter(r->q.isBlank()||Optional.ofNullable(r.getParticipantName()).orElse("").toLowerCase().contains(q)||Optional.ofNullable(r.getConferenceTitle()).orElse("").toLowerCase().contains(q))
            .filter(r->status==null||status.equals("Все статусы")||r.getStatus().name().equals(status))
            .filter(r->cf==null||cf.equals("Все конференции")||cf.startsWith(r.getConferenceId()+" — ")).collect(Collectors.toList());
        if("По дате ↑".equals(registrationSort.getValue()))list.sort(Comparator.comparing(Registration::getRegisteredAt,Comparator.nullsLast(Comparator.naturalOrder())));
        if("По статусу ↑".equals(registrationSort.getValue()))list.sort(Comparator.comparing((Registration r)->r.getStatus().name()));
        registrationTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML private void addParticipant(){try{ParticipantFormController c=Ui.openModal("participant-form.fxml","Новый участник",participantTable,x->{});if(c.getResult()!=null){participantService.create(c.getResult());refreshAll();}}catch(Exception e){showError(e);}}
    @FXML private void editParticipant(){Participant p=participantTable.getSelectionModel().getSelectedItem();if(p==null){Ui.warn("Редактирование","Выберите участника.");return;}try{ParticipantFormController c=Ui.openModal("participant-form.fxml","Редактирование участника",participantTable,x->x.setExisting(p));if(c.getResult()!=null){participantService.update(c.getResult());refreshAll();}}catch(Exception e){showError(e);}}
    @FXML private void deleteParticipant(){Participant p=participantTable.getSelectionModel().getSelectedItem();if(p==null){Ui.warn("Удаление","Выберите участника.");return;}if(Ui.confirm("Удаление","Удалить участника «"+p.getFullName()+"»? Все его регистрации также будут удалены из-за ON DELETE CASCADE.")){try{participantService.delete(p.getId());refreshAll();}catch(Exception e){showError(e);}}}

    @FXML private void addConference(){try{ConferenceFormController c=Ui.openModal("conference-form.fxml","Новая конференция",conferenceTable,x->{});if(c.getResult()!=null){conferenceService.create(c.getResult());refreshAll();}}catch(Exception e){showError(e);}}
    @FXML private void editConference(){Conference c=conferenceTable.getSelectionModel().getSelectedItem();if(c==null){Ui.warn("Редактирование","Выберите конференцию.");return;}try{ConferenceFormController f=Ui.openModal("conference-form.fxml","Редактирование конференции",conferenceTable,x->x.setExisting(c));if(f.getResult()!=null){conferenceService.update(f.getResult());refreshAll();}}catch(Exception e){showError(e);}}
    @FXML private void deleteConference(){Conference c=conferenceTable.getSelectionModel().getSelectedItem();if(c==null){Ui.warn("Удаление","Выберите конференцию.");return;}if(Ui.confirm("Удаление","Удалить конференцию «"+c.getTitle()+"»? Регистрации будут удалены каскадно.")){try{conferenceService.delete(c.getId());refreshAll();}catch(Exception e){showError(e);}}}

    @FXML private void addRegistration(){try{RegistrationFormController f=Ui.openModal("registration-form.fxml","Новая регистрация",registrationTable,x->{x.setParticipants(participantService.getAll());x.setConferences(conferenceService.getAll());});if(f.getResult()!=null){registrationService.create(f.getResult().getParticipantId(),f.getResult().getConferenceId(),f.getResult().getStatus());refreshAll();}}catch(Exception e){showError(e);}}
    @FXML private void editRegistration(){Registration r=registrationTable.getSelectionModel().getSelectedItem();if(r==null){Ui.warn("Редактирование","Выберите регистрацию.");return;}try{RegistrationFormController f=Ui.openModal("registration-form.fxml","Редактирование регистрации",registrationTable,x->{x.setParticipants(participantService.getAll());x.setConferences(conferenceService.getAll());x.setExisting(r);});if(f.getResult()!=null){registrationService.update(f.getResult());refreshAll();}}catch(Exception e){showError(e);}}
    @FXML private void deleteRegistration(){Registration r=registrationTable.getSelectionModel().getSelectedItem();if(r==null){Ui.warn("Удаление","Выберите регистрацию.");return;}if(Ui.confirm("Удаление","Удалить выбранную регистрацию?")){try{registrationService.delete(r.getId());refreshAll();}catch(Exception e){showError(e);}}}

    @FXML private void exportRegistrations(){FileChooser fc=new FileChooser();fc.setTitle("Экспорт регистраций");fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)","*.xlsx"));fc.setInitialFileName("registrations.xlsx");File file=fc.showSaveDialog(registrationTable.getScene().getWindow());if(file!=null)try{ExcelExporter.exportRegistrations(new ArrayList<>(registrationTable.getItems()),file.getAbsolutePath());Ui.info("Экспорт","Данные успешно сохранены в:\n"+file.getAbsolutePath());}catch(Exception e){showError(e);}}
    @FXML private void refreshStats(){try{List<Participant> p=participantService.getAll();List<Conference> c=conferenceService.getAll();List<Registration> r=registrationService.getAll();statParticipants.setText(String.valueOf(p.size()));statConferences.setText(String.valueOf(c.size()));statRegistrations.setText(String.valueOf(r.size()));statActive.setText(String.valueOf(r.stream().filter(x->x.getStatus()==RegistrationStatus.CONFIRMED||x.getStatus()==RegistrationStatus.PAID).count()));statAttended.setText(String.valueOf(r.stream().filter(x->x.getStatus()==RegistrationStatus.ATTENDED).count()));statCancelled.setText(String.valueOf(r.stream().filter(x->x.getStatus()==RegistrationStatus.CANCELLED).count()));Map<String,Long> tracks=r.stream().collect(Collectors.groupingBy(x->c.stream().filter(cc->cc.getId().equals(x.getConferenceId())).map(cc->cc.getTrack().name()).findFirst().orElse("UNKNOWN"),TreeMap::new,Collectors.counting()));statTracks.setText(tracks.entrySet().stream().map(e->e.getKey()+": "+e.getValue()).collect(Collectors.joining("   ")));}catch(Exception e){showError(e);}}
    private String lower(String s){return s==null?"":s.trim().toLowerCase();}
    private void showError(Exception e){Ui.error("Ошибка операции",message(e));}
    private String message(Throwable e){Throwable t=e;while(t.getCause()!=null&&t.getMessage()!=null&&t.getMessage().isBlank())t=t.getCause();return t.getMessage()==null?t.getClass().getSimpleName():t.getMessage();}
}