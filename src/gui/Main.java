package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Optional;

public class Main extends Application {
    static final String OSSP_PARSER = "ossp";
    static final String MSGU_PARSER = "msgu";
    static final String BC_PARSER = "bc";
    static final String ROOT = System.getProperty("user.dir");

    final String PYTHON_SCRIPT = "python " + ROOT + "\\..\\log_parser\\main.py";
    final String DEFAULT_OUTPUT_DIR = ROOT + "\\result";

    final String BG_DIR =  ROOT + "\\data\\background";

    //params for layout
    static final double LOAD_OUT_Y = 60.0;
    static final double LOAD_IN_Y = 100.0;
    static final double LOAD_WS_Y = 140.0;

    static final double LABEL_X = 5.0;
    static final double TF_X = 70.0;

    static final double BUTTON_Y = 200.0;

    private String inFile = "";
    private String outDir = DEFAULT_OUTPUT_DIR;
    private String workspace = "";

    private String debug = "";

    private final TextArea consoleTA = new TextArea();
    private final int consoleTA_MAX_LENTH = 1800000;

    //dim of main stage
    private final int width = 800;
    private final int height = 600;

    public Main() {
        //Optional constructor
    }

    @Override
    public void init() {
        //By default this does nothing, but it
        //can carry out code to set up your app.
        //It runs once before the start method,
        //and after the constructor.
    }

    @Override
    public void start(Stage primaryStage) {
        final Button osspButton = new Button(OSSP_PARSER.toUpperCase());
        final Button msguButton = new Button(MSGU_PARSER.toUpperCase());
        final Button bcButton = new Button(BC_PARSER.toUpperCase());
        //output buttons
        final Button loadOutButton = new Button("Change");
        final Button defaultOutButton = new Button("Default");
        // input button
        final Button loadInButton = new Button("Choose");
        // bc workspace button
        final Button loadWsButton = new Button("Choose");

        // clear console button
        final Button clearButton = new Button("Clear");

        final Label outLabel = new Label("Output");
        final Label inLabel = new Label("Input");
        final Label wsLabel = new Label("Workspace");

        final TextField loadInTF = new TextField();
        final TextField loadOutTF = new TextField();
        final TextField loadWsTF = new TextField();

        // check box for debug mode
        final CheckBox debugCheckBox = new CheckBox("Debug Mode");
        debugCheckBox.setSelected(false);

        final FileChooser inFileChooser = new FileChooser();
        inFileChooser.setTitle("Select Input Dump File");
        inFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("LOG files", "*.txt", "*.log"),
                new FileChooser.ExtensionFilter("BIN files (*.bin)", "*.bin"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select a Directory");

        // Effect
        final DropShadow effectDS = new DropShadow();

        loadInTF.setPromptText("Choose an input dump file");
        if (!inFile.equals("")) {
            loadInTF.setText(inFile);
        }
        loadOutTF.setText(outDir);
        loadWsTF.setPromptText("Set path to workspace, only used by BC parser");

        _initReadOnlyTF(loadInTF);
        _initReadOnlyTF(loadOutTF);
        _initReadOnlyTF(loadWsTF);

        /* Layout position */
        osspButton.setLayoutX(400);
        osspButton.setLayoutY(BUTTON_Y);

        msguButton.setLayoutX(475);
        msguButton.setLayoutY(BUTTON_Y);

        bcButton.setLayoutX(550);
        bcButton.setLayoutY(BUTTON_Y);

        debugCheckBox.setLayoutX(625);
        debugCheckBox.setLayoutY(BUTTON_Y + 3.0);

        loadInButton.setLayoutX(400);
        loadInButton.setLayoutY(LOAD_IN_Y);
        loadInTF.setLayoutX(TF_X );
        loadInTF.setLayoutY(LOAD_IN_Y);
        inLabel.setLayoutX(LABEL_X);
        inLabel.setLayoutY(LOAD_IN_Y + 3);
        inLabel.setEffect(effectDS);

        loadOutButton.setLayoutX(400);
        loadOutButton.setLayoutY(LOAD_OUT_Y);
        defaultOutButton.setLayoutX(475);
        defaultOutButton.setLayoutY(LOAD_OUT_Y);
        loadOutTF.setLayoutX(TF_X);
        loadOutTF.setLayoutY(LOAD_OUT_Y);
        outLabel.setLayoutX(LABEL_X);
        outLabel.setLayoutY(LOAD_OUT_Y + 3);
        outLabel.setEffect(effectDS);

        loadWsButton.setLayoutX(400);
        loadWsButton.setLayoutY(LOAD_WS_Y);
        loadWsTF.setLayoutX(TF_X);
        loadWsTF.setLayoutY(LOAD_WS_Y);
        wsLabel.setLayoutX(LABEL_X);
        wsLabel.setLayoutY(LOAD_WS_Y + 3);
        wsLabel.setEffect(effectDS);

        consoleTA.setLayoutX(10);
        consoleTA.setLayoutY(250);
        consoleTA.setPrefSize(760, 300);
        consoleTA.setWrapText(true);

        clearButton.setLayoutX(700);
        clearButton.setLayoutY(560);

        // Registering handler
        osspButton.setOnAction((ActionEvent event) -> {
            exec(OSSP_PARSER, inFile, outDir, debug, "");

        });
        msguButton.setOnAction((ActionEvent event) -> {
            exec(MSGU_PARSER, inFile, outDir, debug, "");
        });
        bcButton.setOnAction((ActionEvent event) -> {

            exec(BC_PARSER, inFile, outDir, debug, workspace);
        });

        loadInButton.setOnAction((ActionEvent event) -> {
            File selectedFile = inFileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                inFile = selectedFile.getAbsolutePath();
                loadInTF.setText(inFile);
                _updateConsole("Select input file: " + inFile);
            } else {
                _updateConsole("No input file selected");
            }

        });

        loadOutButton.setOnAction((ActionEvent event) -> {
            File selectedDir = dirChooser.showDialog(primaryStage);
            if (selectedDir != null) {
                outDir = selectedDir.getAbsolutePath();
                loadOutTF.setText(outDir);
                _updateConsole("Set output dir: " + outDir);
            } else {
                _updateConsole("No output dir selected");
            }
        });

        defaultOutButton.setOnAction((ActionEvent event) -> {
            outDir = DEFAULT_OUTPUT_DIR;
            loadOutTF.setText(outDir);
            _updateConsole("Set default output dir: " + outDir);
        });

        loadWsButton.setOnAction((ActionEvent event) -> {
            File selectedDir = dirChooser.showDialog(primaryStage);
            if (selectedDir != null) {
                workspace = selectedDir.getAbsolutePath();
                loadWsTF.setText(workspace);
                _updateConsole("Set BaseCode workspace: " + workspace);
            } else {
                _updateConsole("No BaseCode workspace selected");
            }
        });

        clearButton.setOnAction((ActionEvent event) -> {
            consoleTA.setText("");
        });

        debugCheckBox.onMouseClickedProperty().setValue(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                debug = "";
                if (!debugCheckBox.isSelected()) {
                    _updateConsole("Debug mode is disabled.");
                    System.out.print(debugCheckBox.isSelected());
                } else {
                    debugCheckBox.setSelected(false);
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText("Attempt to Enable Debug Mode");
                    alert.setContentText("Enable debug mode is not a recommended action. Are you sure to proceed?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK){
                        debugCheckBox.setSelected(true);
                        debug = "debug";
                        _updateConsole("Debug mode is enabled. This action is NOT recommended.");
                    } else {
                        _updateConsole("Action on enabling debug mode has been cancelled.");
                    }
                }
            }
        });
        
        // Initializing Pane class
        final Pane pane = new Pane();
        // Adding all the nodes to the FlowPane
        pane.getChildren().add(osspButton);
        pane.getChildren().add(msguButton);
        pane.getChildren().add(bcButton);

        pane.getChildren().add(loadOutButton);
        pane.getChildren().add(defaultOutButton);
        pane.getChildren().add(loadOutTF);
        pane.getChildren().add(outLabel);

        pane.getChildren().add(loadInButton);
        pane.getChildren().add(loadInTF);
        pane.getChildren().add(inLabel);

        pane.getChildren().add(loadWsButton);
        pane.getChildren().add(loadWsTF);
        pane.getChildren().add(wsLabel);

        pane.getChildren().add(consoleTA);
        pane.getChildren().add(clearButton);

        pane.getChildren().add(debugCheckBox);

        Scene scene = new Scene(pane, width, height);
        scene.getStylesheets().add(this.getClass().getResource("config.css").toExternalForm());
        //background
        try {
            BackgroundImage backgroundImage = new BackgroundImage(new Image
                    (new BufferedInputStream(new FileInputStream(BG_DIR + "\\kimiuso.png"))),
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    BackgroundSize.DEFAULT);
            //Background background = new Background(backgroundImage);
            pane.setBackground(new Background(backgroundImage));
        } catch (FileNotFoundException e) {
            System.err.println("Caught FileNotFoundException: " + e.getMessage());
        }


        // Adding the title to the window (primaryStage)
        primaryStage.setTitle("BaseCode Log Parser");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        // Show the window(primaryStage)
        primaryStage.show();
    }

    @Override
    public void stop() {
        //By default this does nothing
        //It runs if the user clicks the go-away button
        //closing the window or if Platorm.exit() is called.
        //Use Platorm.exit() instead of System.exit(0).
        //is called. This is where you should offer to
        //save unsaved stuff the user has generated.
    }

    /**
     * Entry point
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }

    /**
     * Setup a read-only TextField
     *
     * @param tf TextField to be set to read-only
     * @apiNote tf becomes read-only after calling this
     */
    private static void _initReadOnlyTF(final TextField tf) {
        tf.setEditable(false);
        tf.setFocusTraversable(false);
        tf.setPrefColumnCount(25);
    }

    private void _popup(Boolean isError, String msg) {
        Alert alert;
        if (isError) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");

        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MESSAGE");
        }
        alert.setContentText(msg);
        alert.show();
    }

    private void _updateConsole(String msg) {
        String old = consoleTA.getText();
        if (old.length() + msg.length() > consoleTA_MAX_LENTH) {
            int idx = consoleTA_MAX_LENTH - msg.length();
            if (idx > 0 ) {
                if(idx <= old.length()) {
                    consoleTA.setText(old.substring(old.length() - idx, old.length()) + "\n" + msg);
                    consoleTA.appendText("");
                } else {
                    consoleTA.appendText("\n" + msg);
                }
            } else {
                consoleTA.setText(msg);
                consoleTA.appendText("");
            }
        } else {
            consoleTA.appendText("\n" + msg);
        }
    }

    private void exec(final String parser, final String inFile, final String outDir, final String debug, final String workspace) {
        // clear console if debug mode is enabled
        if (!debug.equals("")) {
            consoleTA.setText("");
        }

        if (inFile.equals("")) {
            _popup(true, "Please first select an input file for " + parser.toUpperCase() + " parser.");
            return;
        }

        if (parser.equals(BC_PARSER) && workspace.equals("")) {
            _popup(true, "Please choose a BaseCode workspace for " + parser.toUpperCase() + " parser.");
            return;
        }

        final String cmd;
        if(parser.equals(BC_PARSER)) {
            // add " to prevent from whitespace error
            cmd = PYTHON_SCRIPT + " \"" + parser + "\" \"" + workspace + "\" \"" + inFile + "\" \"" +  outDir + "\" " + debug;
        } else {
            // add " to prevent from whitespace error
            cmd = PYTHON_SCRIPT + " \"" + parser + "\" \"" + inFile + "\" \"" + outDir + "\" " + debug;
        }

        _updateConsole("Cmd is [" + cmd + "]");

        // prevent from flooding console
        final int updateThreshold;
        if (debug.equals("")){
            updateThreshold = 10;
        } else {
            updateThreshold = 2000;
        }

        Runnable task = new Runnable() {
            @Override
            public void run() {
                Boolean errFlag = false;
                StringBuilder sb = new StringBuilder();
                String msg;
                String stdMsg = "";
                String errMsg = "";
                int count = 0;
                try {
                    Process p = Runtime.getRuntime().exec(cmd);

                    BufferedReader stdInput = new BufferedReader(new
                            InputStreamReader(p.getInputStream()));

                    BufferedReader stdError = new BufferedReader(new
                            InputStreamReader(p.getErrorStream()));

                    // read the output from the command
                    while ((msg = stdInput.readLine()) != null) {
                        if (msg.contains("Error:") && !errFlag) {
                            errFlag = true;
                            errMsg = msg;
                        }
                        stdMsg = msg;
                        sb.append(msg);
                        sb.append("\n");
                        count ++;
                        if (count >= updateThreshold) {
                            count = 0;
                            final String line = sb.toString();
                            sb.setLength(0);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    _updateConsole(line);
                                }
                            });
                        }
                        //System.out.println(msg);
                    }
                    if (count > 0 ) {
                        final String line = sb.toString();
                        sb.setLength(0);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                _updateConsole(line);
                            }
                        });
                    }

                    // read any errors from the attempted command
                    while ((msg = stdError.readLine()) != null) {
                        errFlag = true;
                        sb.append(msg);
                        sb.append("\n");
                        final String line = msg;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                _updateConsole(line);
                            }
                        });
                        //System.out.println(msg);
                    }
                } catch (IOException e) {
                    System.err.println("Caught IOException: " + e.getMessage());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            _popup(true, "Error, detected IO exception while running.");
                        }
                    });
                }
                if (errMsg.equals("") && errFlag) {
                    errMsg = sb.toString();
                }
                final boolean errFlagUI = errFlag;
                final String errMsgUI = errMsg;
                final String stdMsgUI = stdMsg;

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        String lookForMsg = "Found no log for " + parser;
                        if (errFlagUI) {
                            if (errMsgUI.equals("")) {
                                _popup(errFlagUI, "Unknown error.");
                            } else {
                                _popup(errFlagUI, errMsgUI);
                            }
                        } else {
                            if ( (stdMsgUI.length() > 50 && stdMsgUI.substring(stdMsgUI.length() - 50).contains(lookForMsg)) ||
                                    stdMsgUI.contains(lookForMsg))
                            {
                                _popup(errFlagUI, "Found no " + parser.toUpperCase() + " register dump in [" + inFile + "].");
                            } else {
                                _popup(errFlagUI, "Completed parsing " + parser.toUpperCase() + " register dump for [" + inFile +
                                        "].\nResult saved in [" + outDir + "].");
                            }
                        }
                    }
                });
            }
        };
        // run on new thread so that UI is not blocked
        new Thread(task).start();
    }
}


