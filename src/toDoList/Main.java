package toDoList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import toDoList.dataModel.ToDoData;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void init() throws Exception{
        try{
            ToDoData.getInstance().loadToDoItems();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        primaryStage.setTitle("My Journal");
        primaryStage.setScene(new Scene(root, 720 , 480));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void stop() throws Exception{
        try{
            ToDoData.getInstance().storeToDoItem();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
