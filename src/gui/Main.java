package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.stream.FactoryConfigurationError;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Optional;

public class Main extends Application {
    static final String OSSP_PARSER = "ossp";
    static final String MSGU_PARSER = "msgu";
    static final String BC_PARSER = "bc";
    static final String ROOT = System.getProperty("user.dir");

    final String PYTHON_SCRIPT = "python " + ROOT + "\\log_parser\\main.py";
    final String DEFAULT_OUTPUT_DIR = ROOT + "\\result";

    final String USER_DATA_DIR  = ROOT + "\\data";
    final String BACKGROUND_DIR = USER_DATA_DIR + "\\background";
    final String USER_SETTINGS_FILENAME = USER_DATA_DIR + "\\settings";

    //params for layout
    static final double LOAD_IN_Y = 60.0;
    static final double LOAD_OUT_Y = 100.0;
    static final double LOAD_WS_Y = 140.0;

    static final double LABEL_X = 5.0;
    static final double TF_X = 70.0;

    static final double BUTTON_Y = 200.0;

    private String inFile = "";
    private String outDir = DEFAULT_OUTPUT_DIR;
    private String workspace = "";

    private String debug = "";

    private String background = "";

    private final TextArea consoleTA = new TextArea();
    private final int consoleTA_MAX_LENTH = 100000;

    //dim of main stage
    private final int width = 800;
    private final int height = 600;

    private boolean userSetOutDir = false;

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
        // input button
        final Button loadInButton = new Button("Choose");
        // bc workspace button
        final Button loadWsButton = new Button("Choose");

		//change background button
        final Button changeBGButton = new Button("Background");

        // clear console button
        final Button clearButton = new Button("Clear");

        final Label outLabel = new Label("Output");
        final Label inLabel = new Label("Input");
        final Label wsLabel = new Label("Workspace");

        final TextField loadInTF = new TextField();
        final TextField loadOutTF = new TextField();
        final TextField loadWsTF = new TextField();
        loadInTF.setPromptText("Choose an input dump file");
        loadOutTF.setPromptText("Choose an output dir");
        loadWsTF.setPromptText("Set path to workspace, required by BC parser");

        // check box for debug mode
        final CheckBox debugCheckBox = new CheckBox("Debug Mode");

        final FileChooser inFileChooser = new FileChooser();
        inFileChooser.setTitle("Select Input Dump File");
        inFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("LOG files", "*.txt", "*.log"),
                new FileChooser.ExtensionFilter("BIN files (*.bin)", "*.bin"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );

        DirectoryChooser outDirChooser = new DirectoryChooser();
        outDirChooser.setTitle("Select a Directory");

        DirectoryChooser wsDirChooser = new DirectoryChooser();
        wsDirChooser.setTitle("Select a Directory to BaseCode Workspace");

        // background image chooser
        final FileChooser bgFileChooser = new FileChooser();
        bgFileChooser.setTitle("Select Background");
        bgFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.png", "*.bmp")
        );
        File backgroundDir = new File(BACKGROUND_DIR);
        if (backgroundDir.exists() && backgroundDir.isDirectory()){
            bgFileChooser.setInitialDirectory(backgroundDir);
        }

        // Effect
        final DropShadow effectDS = new DropShadow();

        _getSettings(USER_SETTINGS_FILENAME);
        loadInTF.setText(inFile);
        loadOutTF.setText(outDir);
        loadWsTF.setText((workspace));
        if (debug.equals("-d")){
            debugCheckBox.setSelected(true);
        } else {
            debugCheckBox.setSelected(false);
        }
        inFileChooser.setInitialDirectory(new File(inFile).getParentFile());
        outDirChooser.setInitialDirectory(new File(outDir));
        wsDirChooser.setInitialDirectory(new File(workspace).getAbsoluteFile());

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

        changeBGButton.setLayoutX(625);
        changeBGButton.setLayoutY(60);

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
            exec(MSGU_PARSER, inFile, outDir, debug, workspace);
        });
        bcButton.setOnAction((ActionEvent event) -> {

            exec(BC_PARSER, inFile, outDir, debug, workspace);
        });

        loadInButton.setOnMousePressed((MouseEvent event) -> {
            if (event.isPrimaryButtonDown()) {
                File selectedFile = inFileChooser.showOpenDialog(primaryStage);
                if (selectedFile != null) {
                    inFile = selectedFile.getAbsolutePath();
                    loadInTF.setText(inFile);
                    // next time open the file chooser, go to current folder
                    inFileChooser.setInitialDirectory(selectedFile.getParentFile());

                    _updateConsole("Select input file: " + inFile);

                    if (!userSetOutDir) {
                        // also set output dir to input file's parent dir
                        outDir = selectedFile.getParentFile().getAbsolutePath();
                        loadOutTF.setText(outDir);

                        outDirChooser.setInitialDirectory(selectedFile.getParentFile());

                        _updateConsole("Auto select output directory: " + outDir);
                    }
                } else {
                    if (inFile.equals("")) {
                        _updateConsole("No input file selected.");
                    }
                }
            } else if(event.isSecondaryButtonDown()){
                _updateConsole("Clear input file.");

                // also set back out dir if out dir was not manually changed by user
                if(!userSetOutDir && new File(inFile).getParentFile().toString().equals(outDir)) {
                    outDir = DEFAULT_OUTPUT_DIR;
                    loadOutTF.setText(outDir);
                    _updateConsole("Set default output dir: " + outDir);
                }
                inFile = "";
                loadInTF.setText(inFile);
            }

        });

        loadOutButton.setOnMousePressed((MouseEvent event) -> {
            if (event.isPrimaryButtonDown()) {
                File selectedDir = outDirChooser.showDialog(primaryStage);
                if (selectedDir != null) {
                    outDir = selectedDir.getAbsolutePath();
                    loadOutTF.setText(outDir);

                    outDirChooser.setInitialDirectory(selectedDir);

                    // if user manually change out dir,
                    // then do not auto select out dir based on input dump file
                    userSetOutDir = true;

                    _updateConsole("Set output dir: " + outDir);
                }
            } else if (event.isSecondaryButtonDown()){
                outDir = DEFAULT_OUTPUT_DIR;
                loadOutTF.setText(outDir);
                _updateConsole("Set default output dir: " + outDir);
            }
        });

        loadWsButton.setOnMousePressed((MouseEvent event) -> {
            if (event.isPrimaryButtonDown()) {
                File selectedDir = wsDirChooser.showDialog(primaryStage);
                if (selectedDir != null) {
                    workspace = selectedDir.getAbsolutePath();
                    loadWsTF.setText(workspace);

                    wsDirChooser.setInitialDirectory(selectedDir);

                    _updateConsole("Set BaseCode workspace: " + workspace);
                } else {
                    if (workspace.equals("")) {
                        _updateConsole("No workspace selected.");
                    }
                }
            } else if (event.isSecondaryButtonDown()){
                workspace = "";
                loadWsTF.setText("");
                _updateConsole("Clear BaseCode workspace");
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
                } else {
                    debugCheckBox.setSelected(false);
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText("Attempt to Enable Debug Mode");
                    alert.setContentText("Enable debug mode in this GUI may cause lagging. Do you still want to proceed?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK){
                        debugCheckBox.setSelected(true);
                        debug = "-d";
                        _updateConsole("Debug mode is enabled. This action is NOT recommended.");
                    } else {
                        _updateConsole("Debug mode remains disabled.");
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

        pane.getChildren().add(loadOutTF);
        pane.getChildren().add(outLabel);

        pane.getChildren().add(loadInButton);
        pane.getChildren().add(loadInTF);
        pane.getChildren().add(inLabel);

        pane.getChildren().add(loadWsButton);

        pane.getChildren().add(loadWsTF);
        pane.getChildren().add(wsLabel);

        pane.getChildren().add(changeBGButton);

        pane.getChildren().add(consoleTA);
        pane.getChildren().add(clearButton);

        pane.getChildren().add(debugCheckBox);

        Scene scene = new Scene(pane, width, height);
        scene.getStylesheets().add(this.getClass().getResource("config.css").toExternalForm());
        
        //background
        changeBGButton.setOnMousePressed((MouseEvent event) -> {
            if (event.isPrimaryButtonDown()) {
                File selectedFile = bgFileChooser.showOpenDialog(primaryStage);
                if (selectedFile != null) {
                    String temp = background;
                    try {
                        background = _copyFile(selectedFile, BACKGROUND_DIR);
                        BackgroundImage backgroundImage = new BackgroundImage(new Image
                                (new BufferedInputStream(new FileInputStream(selectedFile.getAbsolutePath()))),
                                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER,
                                new BackgroundSize(100, 100, true, true, true,false));
                        pane.setBackground(new Background(backgroundImage));
                        _updateConsole("Update background image.");
                        bgFileChooser.setInitialDirectory(selectedFile.getParentFile());
                    } catch (FileNotFoundException e) {
                        background = temp;
                        System.err.println("Caught FileNotFoundException: " + e.getMessage());
                    }
                } else {
                    _updateConsole("No background image selected.");
                }
            } else if (event.isSecondaryButtonDown()){
                InputStream in = getClass().getResourceAsStream("resources/background/kimiuso.png");
                BackgroundImage backgroundImage = new BackgroundImage(new Image(in), BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
                pane.setBackground(new Background(backgroundImage));
                background = "";
                _updateConsole("Set default background.");
            }
        });

        // Adding the title to the window (primaryStage)
        primaryStage.setTitle("BaseCode Log Parser");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        // Show the window(primaryStage)
        primaryStage.show();

        // set background, this must be done after primaryStage.show()
        if (!background.equals("")){
            try {
                BackgroundImage backgroundImage = new BackgroundImage(new Image
                        (new BufferedInputStream(new FileInputStream(background))),
                        BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER,
                        new BackgroundSize(100, 100, true, true, true, false));
                pane.setBackground(new Background(backgroundImage));
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        //By default this does nothing
        //It runs if the user clicks the go-away button
        //closing the window or if Platorm.exit() is called.
        //Use Platorm.exit() instead of System.exit(0).
        //is called. This is where you should offer to
        //save unsaved stuff the user has generated.
        _saveSettings(USER_SETTINGS_FILENAME);
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

    /**
     *  popup a dialog
     * @param isError type of the dialog, error or message
     * @param msg msg for this dialog
     * @param showAndWait whether to show and wait
     */
    private void _popup(Boolean isError, String msg, boolean showAndWait) {
        Alert alert;
        if (isError) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");

        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MESSAGE");
        }
        alert.setContentText(msg);

        if (showAndWait){
            alert.showAndWait();
        } else {
            alert.show();
        }
    }

    /**
     * popup a parser completed dialog with hyperlink to the files
     * @param parser which parser is finished
     * @param outFilename path to output file
     */
    private void _popupComplete(final String parser, final  String outFilename){
        if (!outFilename.equals("")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MESSAGE");
            GridPane gp = new GridPane();
            Label iLbl = new Label("Completed parsing " + parser.toUpperCase() + " register dump for: ");
            Label oLbl = new Label("Result saved in: ");
            Hyperlink iLink = new Hyperlink(inFile);
            Hyperlink oLink = new Hyperlink(outFilename);
            gp.addRow(0, iLbl);
            gp.addRow(1, iLink);
            gp.addRow(2, oLbl);
            gp.addRow(3, oLink);

            iLink.setOnAction( (evt) -> {
                try {
                    Desktop.getDesktop().open(new File(inFile));
                } catch (IOException e){
                    _popup(true, "Cannot open [" + inFile + "].", false);
                }

            } );

            oLink.setOnAction( (evt) -> {
                try {
                    Desktop.getDesktop().open(new File(outFilename));
                } catch (IOException e){
                    _popup(true, "Cannot open [" + outFilename + "].", false);
                }

            } );
            alert.getDialogPane().contentProperty().set(gp);
            alert.showAndWait();
        }
    }

    /**
     * update consoleTA with text, with limited max char defined by consoleTA_MAX_LENTH
     * @param msg msg to update
     */
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

    /**
     * exec a parser cmd on a new thread in python
     * @apiNote : request python 2.7.10 or above, request "python" in system path
     */
    private void exec(final String parser, final String inFile, final String outDir, final String debug, final String workspace) {
        // clear console if debug mode is enabled
        if (!debug.equals("")) {
            consoleTA.setText("");
        }

        if (inFile.equals("")) {
            _popup(true, "Please first select an input file for " + parser.toUpperCase() + " parser.", false);
            return;
        }

        if (workspace.equals("")) {
            if(parser.equals(BC_PARSER)) {
                _popup(true, "Please choose a BaseCode workspace for " + parser.toUpperCase() + " parser.", false);
                return;
            } else if (parser.equals(MSGU_PARSER)) {
                // busy wait on this warning until user click a button
                _popup(false, "Warning, workspace not selected, use pre-defined header for MSGU firmware log", true);
                // this is just a warning, not return
            }
        }

        final String cmd;
        if(parser.equals(BC_PARSER) || parser.equals(MSGU_PARSER)) {
            // add " to prevent from whitespace error
            cmd = PYTHON_SCRIPT + " \"" + parser + "\" -w \"" + workspace + "\" -i \"" + inFile + "\" -o \"" +  outDir + "\" " + debug;
        } else {
            // add " to prevent from whitespace error
            cmd = PYTHON_SCRIPT + " \"" + parser + "\" -i \"" + inFile + "\" -o \"" + outDir + "\" " + debug;
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
                String outFilename = "";
                int count = 0;
                try {
                    Process p = Runtime.getRuntime().exec(cmd);

                    BufferedReader stdInput = new BufferedReader(new
                            InputStreamReader(p.getInputStream()));

                    BufferedReader stdError = new BufferedReader(new
                            InputStreamReader(p.getErrorStream()));

                    // read the output from the command
                    while ((msg = stdInput.readLine()) != null) {
                        stdMsg = msg;
                        if (msg.contains("result is saved in ")) {
                            outFilename = msg.substring(msg.lastIndexOf("result is saved in ") + "result is saved in ".length());
                        }
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
                            _popup(true, "Error, detected IO exception while running.", false);
                        }
                    });
                    return;
                }
                if (errFlag) {
                    errMsg = sb.toString();
                }

                final boolean errFlagUI = errFlag;
                final String errMsgUI = errMsg;
                final String stdMsgUI = stdMsg;
                final String outFilenameUI = outFilename;

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (errFlagUI) {
                            if (errMsgUI.equals("")) {
                                _popup(errFlagUI, "Unknown error.", false);
                            } else {
                                _popup(errFlagUI, errMsgUI, true);
                                _popupComplete(parser, outFilenameUI);
                            }
                        } else {
                            String lookForMsg = "Found no log for " + parser;
                            if ( (stdMsgUI.length() > 50 && stdMsgUI.substring(stdMsgUI.length() - 50).contains(lookForMsg)) ||
                                    stdMsgUI.contains(lookForMsg))
                            {
                                _popup(errFlagUI, "Found no " + parser.toUpperCase() + " register dump in [" + inFile + "].", false);
                            } else {
                                /*_popup(errFlagUI, "Completed parsing " + parser.toUpperCase() + " register dump for [" + inFile +
                                        "].\nResult saved in [" + outDir + "].");*/
                                _popupComplete(parser, outFilenameUI);
                            }
                        }
                    }
                });
            }
        };
        // run on new thread so that UI is not blocked
        new Thread(task).start();
    }

    /**
     * Copy file src to dir dirname with error handling
     * @param src file to be copied
     * @param dirname dest directory
     * @return dest filename
     */
    private String _copyFile(File src, String dirname){
        File directory = new File(dirname);
        if (! directory.exists()){
            if (!directory.mkdirs()){
                _updateConsole("Cannot make dir " + dirname);
                return "";
            }
        }
        File dest = new File(directory.toString() + "\\" + src.getName());
        try {
            Files.copy(src.toPath(), dest.toPath());
        } catch (IOException e) {
            _updateConsole("Cannot copy file from " + src.toString() + "to " + dest.toString());
            e.printStackTrace();
            return "";
        }
        return dest.getAbsolutePath();
    }

    private void _saveSettings(String filename){
        File directory = new File(filename).getParentFile();
        if (! directory.exists()){
            if (!directory.mkdirs()){
                System.out.println("Cannot make dir " + directory.getAbsolutePath());
                return;
            }
        }
        try {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            writer.println("inFile," + inFile);
            writer.println("outDir," + outDir);
            writer.println("workspace," + workspace);
            writer.println("debug," + debug);
            writer.println("background," + background);

            writer.close();

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    private  void _getSettings(String filename){
        try{
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            try {
                while ((line = reader.readLine()) != null) {
                    String sl[] = line.split(",");
                    if(sl.length == 2) {
                        switch (sl[0]) {
                            case "inFile":
                                if(new File(sl[1]).isFile()) {
                                    inFile = sl[1];
                                }
                                break;
                            case "outDir":
                                if(new File(sl[1]).isDirectory()) {
                                    outDir = sl[1];
                                }
                                break;
                            case "workspace":
                                if(new File(sl[1]).isDirectory()) {
                                    workspace = sl[1];
                                }
                                break;
                            case "debug":
                                debug = sl[1];
                                break;
                            case "background":
                                if(new File(sl[1]).isFile()) {
                                    background = sl[1];
                                }
                        }
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
