package src;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/*
Add GUI where user can toggle if they have hook, how long to delay, what buttons bind to what, UI scaling, slot Axe is in
    Need binds for forward, back, jump, smart cursor, autoselect, and grapple

Add button and/or timeout to stop button
*/

public class UserGUI extends Application {

    Button button;

    private static final int WIDTH = 500;
    private static final int HEIGHT = 300;
    private static final String STAGE_TITLE = "Tree Chopper Bot";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        button = new Button();
        button.setText("Click me");

        primaryStage.setTitle(STAGE_TITLE);
        StackPane layout = new StackPane();
        layout.getChildren().add(button);
        
        Scene scene = new Scene(layout, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
}
