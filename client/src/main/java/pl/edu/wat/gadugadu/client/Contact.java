package pl.edu.wat.gadugadu.client;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.util.Duration;
import pl.edu.wat.gadugadu.client.controllers.ContactInfoController;
import pl.edu.wat.gadugadu.client.controllers.MainController;
import pl.edu.wat.gadugadu.common.UserInfo;
import pl.edu.wat.gadugadu.common.UserStatus;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class Contact {
    private int id;
    private VBox vBox;
    private FXMLLoader loader;
    private Parent parent;
    private ContactInfoController contactInfoController;
    private Image contactAvatar;
    private Date lastMessageDate;
    private UserInfo userInfo;
    private String name;
    private String imageURI;
    private boolean mouseEntered;

    final public Animation animation = new Transition() {

        {
            setCycleDuration(Duration.millis(2000));
            setInterpolator(Interpolator.EASE_OUT);
            setCycleCount(Animation.INDEFINITE);
        }

        @Override
        protected void interpolate(double frac) {
            Color vColor;
            if(mouseEntered)
                vColor = new Color(185/255.0, 185/255.0, 185/255.0, (1 - frac)/2+0.2);
            else
                vColor = new Color(185/255.0, 185/255.0, 185/255.0, (1 - frac)/2);
            vBox.setBackground(new Background(new BackgroundFill(vColor, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    };

    public Contact(int id, UserInfo userInfo, MainController mainController) {
        this.id = id;
        this.userInfo = userInfo;
        this.mouseEntered=false;
        try {
            vBox = new VBox();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/contactInfo.fxml"));
            Parent parent = loader.load();
            contactInfoController = loader.getController();
            contactInfoController.setContact(this);
            contactInfoController.userName.setText(userInfo.getDefaultNick() + " (" + userInfo.getClientId() + ")");
            contactInfoController.status.setText(UserStatus.statusNames[userInfo.getUserStatus().value()]);
            contactInfoController.setId(userInfo.getClientId());
            contactInfoController.setCircleStroke(userInfo.getUserStatus());
            contactInfoController.setMainController(mainController);
            contactAvatar = new Image("/blank-profile-picture.png");
            contactInfoController.userImage.setFill(new ImagePattern(contactAvatar));

            vBox.getChildren().addAll(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStatus(UserStatus userStatus){
        contactInfoController.status.setText(UserStatus.statusNames[userStatus.value()]);
        contactInfoController.setCircleStroke(userStatus);
    }

    public VBox getvBox() {
        return vBox;
    }

    public int getId() {
        return id;
    }

    public String getContactName(){
        return userInfo.getDefaultNick();
    }

    public ContactInfoController getContactInfoController() {
        return contactInfoController;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setMouseEntered(boolean mouseEntered) {
        this.mouseEntered = mouseEntered;
    }

    public Image getContactAvatar() {
        return contactAvatar;
    }

    public void updateContactAvatar(String imageURI) {
        this.imageURI = imageURI;
        File imageFile = new File(imageURI);
        System.out.println(imageFile.getAbsolutePath());
        if (imageFile.exists()) {
            contactAvatar = new Image(imageFile.toURI().toString());
            contactInfoController.userImage.setFill(new ImagePattern(contactAvatar));
        }
    }


}
