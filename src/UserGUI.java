import java.awt.AWTException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/*
Add GUI where user can toggle if they have hook, how long to delay, what buttons bind to what, UI scaling, slot Axe is in
    Need binds for forward, back, jump, smart cursor, autoselect, and grapple

Add button and/or timeout to stop button
*/

public class UserGUI extends Application {

    Button button;
    
    private static final int WIDTH = 650;
    private static final int HEIGHT = 225;
    private static final int COLUMNS_FOR_USER_INFO = 3;
    private static final String STAGE_TITLE = "Tree Chopper Bot";
    private static final int INTERNAL_PADDING = 10;
    private static final int VGAP = 8;
    private static final int HGAP = 10;

    private static Status botStatus;

    private static CheckBox optimizations;
    private static CheckBox hook;

    private static Stage primaryStage;

    private static ArrayList<javafx.scene.Node> togglesHolder;


    public static void main(String[] args) {
        togglesHolder = new ArrayList<>();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        UserGUI.primaryStage = primaryStage; 
        GridPane internal = initializeGridPane();

        Label userInfo = new Label(new IntroText().toString());
        optimizations = intiializeOptimizationBox();
        hook = new CheckBox("Does this character have a grappling hook? Bot will run much more consistently with one.");
        Button launcher = setLauncher();

        togglesHolder.add(optimizations);
        togglesHolder.add(hook);

        addToggles(userInfo);
        GridPane.setConstraints(launcher, 0, COLUMNS_FOR_USER_INFO+togglesHolder.size()+2);
                
        internal.getChildren().addAll(togglesHolder);
        internal.getChildren().addAll(userInfo, launcher);

        Scene options = new Scene(internal, WIDTH, HEIGHT);
        primaryStage.setTitle(STAGE_TITLE);
        primaryStage.setScene(options);
        primaryStage.show();
    }

    // Scalable method that adds all CheckBoxes in togglesHolder() to the GridPane
    private void addToggles(Label userInfo) {
        GridPane.setConstraints(userInfo, 0, 0);
        for (int i = 0; i < togglesHolder.size(); i++) {
            GridPane.setConstraints(togglesHolder.get(i), 0, COLUMNS_FOR_USER_INFO+i);
        }


    }

    private CheckBox intiializeOptimizationBox() {
        CheckBox result = new CheckBox("Enable root optimizations? Chops default trees faster but will fail on cacti, other trees, etc.");
        result.setSelected(true);
        return result;
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
        boolean optimize = optimizations.isSelected();
        boolean hasHook = hook.isSelected();
        StackPane launchedScreen = new StackPane();
        Label launchedInfo = new Label("Bot launched. Start with up, kill with down");
        launchedScreen.getChildren().add(launchedInfo);
        Scene botStarting = new Scene(launchedScreen, 500, 200);
        primaryStage.setScene(botStarting);
        TreeChopper.initiateBot(optimize, hasHook);
    }

    private GridPane initializeGridPane() {
        GridPane internal = new GridPane();
        internal.setPadding(new Insets(INTERNAL_PADDING, INTERNAL_PADDING, INTERNAL_PADDING, INTERNAL_PADDING));
        internal.setVgap(VGAP);
        internal.setHgap(HGAP);
        return internal;
    }

}
