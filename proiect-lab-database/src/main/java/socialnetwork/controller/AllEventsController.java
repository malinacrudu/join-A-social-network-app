package socialnetwork.controller;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import socialnetwork.domain.Event;
import socialnetwork.domain.User;
import socialnetwork.exceptions.AlreadyExistsException;
import socialnetwork.service.CommunityService;
import socialnetwork.utils.Observer;

import java.io.File;


public class AllEventsController implements Observer
{

    public Label pageLabel;
    public ListView<HBox> listView;
    User user;
    private CommunityService service;

    public void setService(CommunityService service, User user)
    {
        this.service=service;
        this.user = user;
        listView.setStyle("-fx-spacing: 5");
        service.add_observer(this);
        initModel(service.getFirstPageEvents(null));
    }

    public void go_previous()
    {
        Iterable<Event> eventsIterable = service.getPreviousPageEvents(null);
        if(eventsIterable!=null)
        {
            initModel(eventsIterable);
            pageLabel.setText(String.valueOf(Integer.parseInt(pageLabel.getText()) - 1));
        }
    }

    public void go_next()
    {
        Iterable<Event> eventsIterable = service.getNextPageEvents(null);
        if(eventsIterable!=null)
        {
            initModel(eventsIterable);
            pageLabel.setText(String.valueOf(Integer.parseInt(pageLabel.getText()) + 1));
        }
    }


    private void initModel(Iterable<Event> eventIterable)
    {
        listView.getItems().clear();
        for(Event e : eventIterable)
        {
            Label label1 = new Label("Title: "+e.getTitle());
            Label label2 = new Label("Description: "+e.getDescription());
            Label label3 = new Label("Location: "+e.getLocation());
            Label label4 = new Label("Period: "+e.getStart().toString()+"->"+e.getEnd().toString());
            Label label5 = new Label("Number of participants: "+e.getParticipants().size());
            label1.setFont(Font.font("Century Gothic", 13));
            label1.setTextFill(Paint.valueOf("slategray"));
            label2.setFont(Font.font("Century Gothic", 13));
            label2.setTextFill(Paint.valueOf("slategray"));
            label3.setFont(Font.font("Century Gothic", 13));
            label3.setTextFill(Paint.valueOf("slategray"));
            label4.setFont(Font.font("Century Gothic", 13));
            label4.setTextFill(Paint.valueOf("slategray"));
            label5.setFont(Font.font("Century Gothic", 13));
            label5.setTextFill(Paint.valueOf("slategray"));
            HBox hBox = new HBox();
            hBox.setSpacing(7);
            File file = new File(e.getPath());
            Image image = new Image(file.toURI().toString());
            ImageView iv = new ImageView();
            iv.setImage(image);
            iv.setFitWidth(100);
            iv.setFitHeight(100);
            hBox.getChildren().add(iv);
            hBox.getChildren().add(label1);
            hBox.getChildren().add(label2);
            hBox.getChildren().add(label3);
            hBox.getChildren().add(label4);
            hBox.getChildren().add(label5);
            hBox.setAccessibleText(e.getId().toString());
            listView.getItems().add(hBox);
        }
    }

    public void follow_event(ActionEvent actionEvent)
    {


        try {
            Event event = service.findEvent(listView.getSelectionModel().getSelectedItem().getAccessibleText());
            if(event.getParticipants().contains(user.getId()))throw new AlreadyExistsException("You are already a follower of this event!");
            if (service.un_follow(event, user,true) == null)
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Congrats!You officially follow "+event.getTitle()+" !", ButtonType.OK);
                alert.setResizable(true);
                alert.showAndWait();
                execute_update();
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Something went wrong! Please try again later!", ButtonType.OK);
                alert.setResizable(true);
                alert.showAndWait();
            }

        }
        catch (Exception e)
        {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                alert.setResizable(true);
                alert.showAndWait();

        }
    }

    @Override
    public void execute_update()
    {
        initModel(service.getFirstPageEvents(null));
    }
}
