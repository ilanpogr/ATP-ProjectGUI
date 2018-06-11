package View;

import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Aviadjo on 3/9/2017.
 */
public class MazeDisplayer extends Canvas {

    private int[][] maze;
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;
    private int characterGoalPositionRow = 1;
    private int characterGoalPositionColumn = 1;
    private int CharacterStratPositionRow = 1;
    private int CharacterStratPositionColumn = 1;

    private boolean finished = false;

    public void setMaze(int[][] maze) {
        this.maze = maze;
//        redraw();
    }

    public void setFinished(boolean value) {
        finished = value;
    }

    public boolean getFinished() {
        return finished;
    }

    public void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
//        redraw();
    }

    public void setCharacterStratPosition(int row, int column) {
        CharacterStratPositionRow = row;
        CharacterStratPositionColumn = column;
    }


    public void setCharacterGoalPosition(int characterGoalRow, int characterGoalColumn) {
        characterGoalPositionRow = characterGoalRow;
        characterGoalPositionColumn = characterGoalColumn;
        redraw();
    }

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    public void redraw() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.length;
            double cellWidth = canvasWidth / maze[0].length;

            try {
                /**
                 * ADD STARTING POINT - FLAG picture
                 */
                Image characterGoalImage = new Image(new FileInputStream(ImageFileNameGoal.get()));
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                /**
                 * NEED TO BE CHECKED START POINT
                 */
//                Image startPointImage = new Image(new FileInputStream(ImageFileStartPoint.get()));


                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                //Draw Maze
                for (int i = 0; i < maze.length; i++) {
                    for (int j = 0; j < maze[i].length; j++) {
                        if (maze[i][j] == 1) {
                            gc.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                    }
                }

                //Draw Character
//                gc.drawImage(startPointImage,CharacterStratPositionColumn * cellWidth, CharacterStratPositionRow * cellHeight ,cellWidth, cellHeight);
                gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
                gc.drawImage(characterGoalImage, characterGoalPositionColumn * cellWidth, characterGoalPositionRow * cellHeight, cellWidth, cellHeight);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    //region Properties
//    private StringProperty ImageFileStartPoint = new SimpleStringProperty();
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private StringProperty ImageFileNameGoal = new SimpleStringProperty();
    private StringProperty ImageFileNameSolution = new SimpleStringProperty();

    public void drawSolution(Solution mazeSolution) {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.length;
            double cellWidth = canvasWidth / maze[0].length;

            try {
                Image solutionImage = new Image(new FileInputStream(ImageFileNameSolution.get()));

                GraphicsContext gc = getGraphicsContext2D();

                ArrayList<AState> solutionPath = mazeSolution.getSolutionPath();
                //Draw Solution
                for (int i = 1; i < mazeSolution.getSolutionPath().size() - 1; i++) {
                    gc.drawImage(solutionImage, ((MazeState) solutionPath.get(i)).getPosition().getColumnIndex() * cellWidth, ((MazeState) solutionPath.get(i)).getPosition().getRowIndex() * cellHeight, cellWidth, cellHeight);

                }

            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }
        }
    }

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    public String getImageFileNameGoal() {
        return ImageFileNameGoal.get();
    }

    public void setImageFileNameGoal(String ImageFileNameGoal) {
        this.ImageFileNameGoal.set(ImageFileNameGoal);
    }

    public String getImageFileNameSolution() {
        return ImageFileNameSolution.get();
    }

//    public String getImageFileStartPoint() {
//        return ImageFileStartPoint.get();
//    }

    public void setImageFileStartPoint(String ImageFileStartPoint) {
        this.ImageFileNameGoal.set(ImageFileStartPoint);
    }

    public void setImageFileNameSolution(String imageFileNameSolution) {
        this.ImageFileNameSolution.set(imageFileNameSolution);
    }

    //endregion

}