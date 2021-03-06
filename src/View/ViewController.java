package View;

import ViewModel.ViewModel;
import algorithms.search.Solution;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;

public class ViewController implements Observer, IView, Initializable {


    @FXML
    private BorderPane root;
    @FXML
    public Button btn_zoomBack;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public MenuItem loadMenu;
    @FXML
    public MenuItem saveMenu;
    @FXML
    public MenuItem newMenu;
    @FXML
    public ToggleButton speakerImage;
    @FXML
    public Slider volumeSlider;
    @FXML
    public MenuItem propertiesMenu;
    @FXML
    public MenuItem mnu_About;
    @FXML
    public MenuItem mnu_Help;
    @FXML
    public MenuItem mnu_Close;
    @FXML
    public MazeDisplayer mazeDisplayer;
    @FXML
    public javafx.scene.control.Button btn_solveMaze;

    private ViewModel viewModel;
    private Media song;
    private MediaPlayer media;


    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    private void displaySolvedMaze(Solution mazeSolution) {
        mazeDisplayer.drawSolution(mazeSolution);
    }

    @Override
    public void displayMaze(int[][] maze) {
        mazeDisplayer.setMaze(maze);
        scrollPane.setVisible(true);
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        int characterStratPositionRow = viewModel.getCharacterStartPositionRow();
        int characterStartPointColumn = viewModel.getCharacterStartPositionColumn();
        mazeDisplayer.setCharacterStratPosition(characterStratPositionRow, characterStartPointColumn);
        int characterLastPosion = viewModel.getCharacterLastPosition();
        mazeDisplayer.setCharacterDirection(characterLastPosion);
        int characterGoalRow = viewModel.getCharacterGoalPositionRow();
        int characterGoalColumn = viewModel.getCharacterGoalPositionColumn();
        mazeDisplayer.setCharacterGoalPosition(characterGoalRow, characterGoalColumn);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            if (arg.equals("solution")) {
                displaySolvedMaze(viewModel.getMazeSolution());
            }
            if (arg.equals("mazeGenerator")) {
                displayMaze(viewModel.getMaze());
            }
            if (arg.equals("movement and endGame")) {
                displayMaze(viewModel.getMaze());
                mazeDisplayer.setFinished(true);
                finished();
            }
        }
    }

    private void finished() {
        media.setMute(true);
        String path = new File("src/View/Resources/Sounds/WubaDubaLubLub.mp3").getPath();
        mazeDisplayer.drawEnding();
        Media sound = new Media(new File(path).toURI().toString());
        MediaPlayer player = new MediaPlayer(sound);
        player.play();
        media.setMute(false);
        btn_solveMaze.setDisable(true);
        saveMenu.setDisable(true);

    }

    public void solveMaze(ActionEvent actionEvent) {
        if (!mazeDisplayer.getFinished()) {
            try {
                viewModel.getMaze();
                btn_solveMaze.setDisable(true);
                viewModel.solveMaze();
            } catch (NullPointerException e) {
                showAlert("Error", "No maze to solve", "Don't click on every button you see...\nGenerate a maze if you wish to solve");
            }
        }
    }

    public void showAlert(String title, String information, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(information);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        try {
            viewModel.getMaze();
            if (!mazeDisplayer.getFinished())
                viewModel.moveCharacter(keyEvent.getCode());
            keyEvent.consume();
            btn_solveMaze.setDisable(false);
        } catch (NullPointerException e) {
            keyEvent.consume();
        }
    }

    public void setResizeEvent(Scene scene) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                root.setPrefWidth(newSceneWidth.doubleValue());
                mazeDisplayer.setWidth(newSceneWidth.doubleValue());
                mazeDisplayer.redraw();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                root.setPrefHeight(newSceneHeight.doubleValue());
                mazeDisplayer.setHeight(newSceneHeight.doubleValue() - root.getTop().getLayoutBounds().getHeight() - root.getBottom().getLayoutBounds().getHeight());
                mazeDisplayer.redraw();
            }
        });
    }

    public void About(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root);
            String path = new File("src/View/Resources/Sounds/original-morty.mp3").getPath();
            song = new Media(new File(path).toURI().toString());
            media = new MediaPlayer(song);
            media.setAutoPlay(true);
            media.setVolume(0.99);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            actionEvent.consume();
        }
    }

    public void volumeSwitch(ActionEvent actionEvent) {
        if (speakerImage.isSelected()) {
            media.setMute(true);
            speakerImage.setTooltip(new Tooltip("Volume ON"));
            Image image = new Image(getClass().getResourceAsStream("Resources/Images/mute.png"));
            speakerImage.setGraphic(new ImageView(image));
        } else {
            media.setMute(false);
            speakerImage.setTooltip(new Tooltip("Mute"));
            Image image = new Image(getClass().getResourceAsStream("Resources/Images/speakerOn.png"));
            speakerImage.setGraphic(new ImageView(image));
        }

    }

    public void Help(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Game Rules");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Help.fxml").openStream());
            Scene scene = new Scene(root, 400, 400);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            actionEvent.consume();
        }
    }

    public void CloseGame(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?");
        alert.setTitle("Confirm Exit");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        } else {
            actionEvent.consume();
        }
    }

    public void loadGame(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Maze");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("dat files", "*.dat"));
        fc.setInitialDirectory(new File("src/View/Resources/savedGames"));
        //showing the file chooser
        File file = fc.showOpenDialog(null);
        mazeDisplayer.setFinished(false);

        // checking that a file was
        // chosen by the user
        if (file != null) {
            viewModel.loadGame(file);
            // enabling saveMI
            saveMenu.setDisable(false);
            btn_solveMaze.setDisable(false);
        }
    }

    public void saveMaze(ActionEvent actionEvent) {
        try {
            viewModel.getMaze();
            viewModel.saveMaze();
        } catch (NullPointerException e) {
            showAlert("Error", "No maze to save", "Don't click on every button you see...\nGenerate a maze if you wish to save one");
        }
    }

    public void newMaze(ActionEvent actionEvent) throws IOException {
        try {
            Stage stage = new Stage();
            stage.setTitle("New Maze");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("NewMaze.fxml").openStream());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("NewMazeStyle.css").toExternalForm());
            stage.setScene(scene);
            NewMazeController newMazeController = fxmlLoader.getController();
            newMazeController.setStage(stage);
            newMazeController.setViewController(this);
            newMazeController.setViewModel(viewModel);
            newMazeController.setMazeDisplayer(mazeDisplayer);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            mazeDisplayer.setFinished(false);
            saveMenu.setDisable(false);
            btn_solveMaze.setDisable(false);
        } catch (Exception e) {
            actionEvent.consume();
        }
    }

    public void mouseMovement(MouseEvent mouseEvent) {
        try {
            viewModel.getMaze();
            btn_solveMaze.setDisable(false);
            int mouseX = (int) ((mouseEvent.getX()) / (mazeDisplayer.getWidth() / viewModel.getMaze()[0].length));
            int mouseY = (int) ((mouseEvent.getY()) / (mazeDisplayer.getHeight() / viewModel.getMaze().length));
            if (!mazeDisplayer.getFinished()) {
                if (mouseY < viewModel.getCharacterPositionRow()) {
                    viewModel.moveCharacter(KeyCode.UP);
                }
                if (mouseY > viewModel.getCharacterPositionRow()) {
                    viewModel.moveCharacter(KeyCode.DOWN);
                }
                if (mouseX < viewModel.getCharacterPositionColumn()) {
                    viewModel.moveCharacter(KeyCode.LEFT);
                }
                if (mouseX > viewModel.getCharacterPositionColumn()) {
                    viewModel.moveCharacter(KeyCode.RIGHT);
                }
            }

        } catch (NullPointerException e) {
            mouseEvent.consume();
        }
    }


    public void zoomInOut(ScrollEvent scrollEvent) {
        try {
            btn_zoomBack.setDisable(false);
            btn_zoomBack.setVisible(true);
            btn_zoomBack.setEffect(new Glow(1));
            viewModel.getMaze();
            AnimatedZoomOperator zoomOperator = new AnimatedZoomOperator();
            double zoomFactor;
            if (scrollEvent.isControlDown()) {
                zoomFactor = 1.5;
                double deltaY = scrollEvent.getDeltaY();
                if (deltaY < 0) {
                    zoomFactor = 1 / zoomFactor;
                }
                zoomOperator.zoom(scrollPane, zoomFactor, scrollEvent.getSceneX(), scrollEvent.getSceneY());
                scrollEvent.consume();
            }
        } catch (NullPointerException e) {
            scrollEvent.consume();
        }
    }


    public void zoomBack(ActionEvent actionEvent) {
        btn_zoomBack.setDisable(true);
        Timeline timeline = new Timeline(60);
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(100), new KeyValue(scrollPane.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(100), new KeyValue(scrollPane.translateYProperty(), 0)),
                new KeyFrame(Duration.millis(100), new KeyValue(scrollPane.scaleXProperty(), 1)),
                new KeyFrame(Duration.millis(100), new KeyValue(scrollPane.scaleYProperty(), 1)));
        timeline.play();
        btn_zoomBack.setVisible(false);
    }

    public void configurations(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Properties");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("Properties.fxml").openStream());
        Scene scene = new Scene(root, 400, 370);
        stage.setScene(scene);
        PropertiesController propertiesViewController = fxmlLoader.getController();
        propertiesViewController.setStage(stage);
        if (btn_solveMaze.isDisable())
            btn_solveMaze.setDisable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btn_zoomBack.setTooltip(new Tooltip("Resize"));
        btn_zoomBack.setDisable(true);
        btn_zoomBack.setVisible(false);
        scrollPane.setVisible(false);
        btn_zoomBack.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("Resources/Images/resize.png"))));
        Image image = new Image(getClass().getResourceAsStream("Resources/Images/speakerOn.png"));
        speakerImage.setGraphic(new ImageView(image));
        speakerImage.setTooltip(new Tooltip("Mute"));

        String path = new File("src/View/Resources/Sounds/Puzzle-Game.mp3").getAbsolutePath();
        song = new Media(new File(path).toURI().toString());
        media = new MediaPlayer(song);
        media.setAutoPlay(true);
        media.setCycleCount(MediaPlayer.INDEFINITE);
        media.setVolume(0.3);

        volumeSlider.setValue(media.getVolume() * 100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable observable) {
                if (speakerImage.isSelected()) {
                    speakerImage.setSelected(false);
                    Image image = new Image(getClass().getResourceAsStream("Resources/Images/speakerOn.png"));
                    speakerImage.setGraphic(new ImageView(image));
                }
                media.setVolume(volumeSlider.getValue() / 100);
            }
        });
    }
}
