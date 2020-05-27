package me.swag.checker.core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import me.swag.checker.Main;

import java.io.IOException;

public class Core extends Application {

    @Override
    public void init() {
        System.out.println("Loading Token Checker...");
    }




    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Loaded " + Main.NAME);
        stage.setTitle(Main.NAME);
        Parent root = FXMLLoader.load(getClass().getResource("../resources/design.fxml"));
        Scene scene = new Scene(root);
        stage.getIcons().add(new ImageView("https://www.freepnglogos.com/uploads/discord-logo-png/discord-logo-logodownload-download-logotipos-1.png").getImage());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();


    }

    @Override
    public void stop() {
        System.out.println(Main.NAME + " Closed");
    }
}
