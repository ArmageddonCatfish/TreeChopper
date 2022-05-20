import java.awt.AWTException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/*
Add GUI where user can toggle if they have hook, how long to delay, what buttons bind to what, UI scaling, slot Axe is in
    Need binds for forward, back, jump, smart cursor, autoselect, and grapple

Add button and/or timeout to stop button
*/

public class UserGUI extends Application {

    Button button;
    
    private static final int WIDTH = 460;
    private static final int HEIGHT = 200;
    private static final String STAGE_TITLE = "Tree Chopper Bot";
    private static final int INTERNAL_PADDING = 10;
    private static final int VGAP = 8;
    private static final int HGAP = 10;

    private static CheckBox hermes;
    private static CheckBox hook;

    private static Stage primaryStage;

    private static ArrayList<javafx.scene.Node> elementHolder;


    public static void main(String[] args) {
        elementHolder = new ArrayList<>();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        UserGUI.primaryStage = primaryStage; 
        GridPane internal = initializeGridPane();

        Label userInfo = new Label(new IntroText().toString());
        hermes = new CheckBox("Do you have Hermes boots?");
        hook = new CheckBox("Do you have a grappling hook?");
        Button launcher = setLauncher();

        GridPane.setConstraints(userInfo, 0, 0);
        GridPane.setConstraints(hermes, 0, 3);
        GridPane.setConstraints(hook, 0, 4);
        GridPane.setConstraints(launcher, 0, 6);

        elementHolder.add(hermes);
        elementHolder.add(hook);
        elementHolder.add(userInfo);
        elementHolder.add(launcher);
        internal.getChildren().addAll(elementHolder);

        Scene options = new Scene(internal, WIDTH, HEIGHT);
        primaryStage.setTitle(STAGE_TITLE);
        primaryStage.setScene(options);
        primaryStage.show();
    }


    private Button setLauncher() {
        Button result = new Button();
        result.setText("Launch");
        result.setOnAction(e -> {
            try {
                launchBot();
            } catch (AWTException e1) {
                e1.printStackTrace();
            }
        });
        return result;
    }

    private static void launchBot() throws AWTException {
        primaryStage.close();
        TreeChopper.initiateBot(hermes.isSelected(), hook.isSelected());
    }

    private GridPane initializeGridPane() {
        GridPane internal = new GridPane();
        internal.setPadding(new Insets(INTERNAL_PADDING, INTERNAL_PADDING, INTERNAL_PADDING, INTERNAL_PADDING));
        internal.setVgap(VGAP);
        internal.setHgap(HGAP);
        return internal;
    }

}
