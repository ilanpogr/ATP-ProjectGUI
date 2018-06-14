package View;

import ViewModel.ViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewMazeController implements Initializable {
    public TextField txt_columns;
    public TextField txt_rows;
    public Button btn_play;
    public Button btn_cancel;
    public ChoiceBox choice_character;
    public ImageView character_image;

    private MazeDisplayer mazeDisplayer;
    private ViewController viewController;
    private ViewModel viewModel;
    private Stage stage;


    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
//        bindProperties(viewModel);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMazeDisplayer(MazeDisplayer mazeDisplayer) {
        this.mazeDisplayer = mazeDisplayer;
    }

    public void setViewController(ViewController viewController) {
        this.viewController = viewController;
    }


    public void generateMaze() {
        try {
//            mazeDisplayer.setMaze(null);
            int rows = Integer.valueOf(txt_rows.getText());
            int cols = Integer.valueOf(txt_columns.getText());
            if (rows <= 1 || cols <= 1) {
                viewController.showAlert("Error", "Wrong Input", "Height and width should be greater than 1");
            } else {
                viewController.btn_solveMaze.setDisable(false);
//                btn_generateMaze.setDisable(true);
                mazeDisplayer.setFinished(false);
                viewModel.generateMaze(rows, cols);
                stage.close();
            }
        } catch (Exception e) {
            viewController.showAlert("Error", "Wrong Input", "Check input parameters");
        }
    }



    public void cancel(ActionEvent actionEvent) {
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        choice_character.getItems().removeAll(choice_character.getItems());
        choice_character.getItems().addAll("Pickle Rick", "EyeHole Man");
        choice_character.getSelectionModel().select("Pickle Rick");
        choice_character.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue ov, Number value, Number new_value){
//                setCharacter();
                System.out.println("changed");
            }
        });
        choice_character.setTooltip(new Tooltip("Select your character"));
    }
//
//    public void setCharacter(){
//        String newCharacter = (String)choice_character.getValue();
//        mazeDisplayer.setImageFileNameCharacter("Resources/Characters/"+ newCharacter+ ".png");
//    }


}
