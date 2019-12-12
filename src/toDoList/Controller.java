package toDoList;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import toDoList.dataModel.ToDoData;
import toDoList.dataModel.ToDoItem;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;

public class Controller {

    @FXML
    private ListView<ToDoItem> toDoListView;
    @FXML
    private TextArea textArea1;
    @FXML
    private Label label1;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ToggleButton filterToggleButton;
    @FXML
    private FilteredList<ToDoItem> filteredList;


    private Predicate<ToDoItem> wantAllItems;
    private Predicate<ToDoItem> wantTodayItems;

    public void initialize(){

        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem=new MenuItem("Delete");

        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ToDoItem item = toDoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });

        listContextMenu.getItems().addAll(deleteMenuItem);

//        ToDoItem item1 = new ToDoItem("Mail Birthday Card","Buy a 21st Birthday Card for Sister",
//                LocalDate.of(2019,11,29));
//
//        ToDoItem item2 = new ToDoItem("Doctor's Appointment","Visit your ophthalmologist ",
//                LocalDate.of(2019,11,24));
//
//        ToDoItem item3 = new ToDoItem("Finish Tutorial","Complete Tutorial for Java FX",
//                LocalDate.of(2019,11,21));
//
//        ToDoItem item4 = new ToDoItem("Spring MVC","Start Spring tutorial by 23th",
//                LocalDate.of(2019,11 ,24 ));
//
//        ToDoItem item5 = new ToDoItem("DB basics","Learn DB basics by 23th",
//                LocalDate.of(2019,11,23 ));
//
//        toDoList = new ArrayList<>();
//        toDoList.add(item1);
//        toDoList.add(item2);
//        toDoList.add(item3);
//        toDoList.add(item4);
//        toDoList.add(item5);
//
//        ToDoData.getInstance().setToDoItems(toDoList);

        toDoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observableValue, ToDoItem toDoItem, ToDoItem newValue) {
                if(newValue!=null){
                    ToDoItem item=toDoListView.getSelectionModel().getSelectedItem();
                    textArea1.setText(item.getDetails());
                    DateTimeFormatter df=DateTimeFormatter.ofPattern("d MMMM yyyy");
                    label1.setText(df.format(item.getDeadLine()));
                }
            }
        });

        wantAllItems=new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem item) {
                return true;
            }
        };

        wantTodayItems=new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem item) {
                return (item.getDeadLine().equals(LocalDate.now()));
            }
        };


        filteredList=new FilteredList<>(ToDoData.getInstance().getToDoItems(),wantAllItems);

        SortedList<ToDoItem> sortedList= new SortedList<>(filteredList,
                new Comparator<ToDoItem>() {
                    @Override
                    public int compare(ToDoItem o1, ToDoItem o2) {
                        return o1.getDeadLine().compareTo(o2.getDeadLine());
                    }
                });

//        toDoListView.setItems(ToDoData.getInstance().getToDoItems());

        toDoListView.setItems(sortedList);
        toDoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        toDoListView.getSelectionModel().selectFirst();

        toDoListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView) {
                ListCell<ToDoItem> cell = new ListCell<>(){
                    @Override
                    protected void updateItem(ToDoItem item, boolean empty) {
                        super.updateItem(item, empty);

                        if(empty)
                            setText(null);
                        else{

                            setText(item.getShortDescription());

                            if(item.getDeadLine().isBefore(LocalDate.now()))
                                setTextFill(Color.RED);
                            else if(item.getDeadLine().isEqual(LocalDate.now()))
                                setTextFill(Color.GREEN);
                            else if(item.getDeadLine().equals(LocalDate.now().plusDays(1)))
                                setTextFill(Color.CYAN);

                        }
                    }
                };

                cell.emptyProperty().addListener(
                        (obs,wasEmpty,isNowEmpty)-> {
                            if (isNowEmpty){
                                cell.setContextMenu(null);
                            }else{
                                cell.setContextMenu(listContextMenu);
                            }
                        });

                return cell;
            }

        });
    }

    @FXML
    public void handleClickListView(){
        ToDoItem item=toDoListView.getSelectionModel().getSelectedItem();
//        String sb = item.getDetails() + "\n\n\n\n\n" +
//                "Due: " + item.getDeadLine().toString();
        textArea1.setText(item.getDetails());
        label1.setText(item.getDeadLine().toString());

    }



    @FXML
    public void showDialog(){
        Dialog<ButtonType> dialog=new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Note");
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("toDoDialog.fxml"));
        try {

            dialog.getDialogPane().setContent(fxmlLoader.load());

            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK){
                DialogController controller = fxmlLoader.getController();
                ToDoItem newItem = controller.processResults();
//              toDoListView.getItems().setAll(ToDoData.getInstance().getToDoItems());
                toDoListView.getSelectionModel().select(newItem);
            }


        }
        catch (IOException e){
            System.out.println("Couldn't Load");
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteItem(ToDoItem item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete ToDo Item");
        alert.setHeaderText("Delete Item: " + item.getShortDescription());
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to Back out.");

        Optional<ButtonType> result= alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            ToDoData.getInstance().deleteTodoItem(item);
        }
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent){
        ToDoItem selectedItem = toDoListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null ){
            if(keyEvent.getCode().equals(KeyCode.DELETE)){
                deleteItem(selectedItem);
            }
        }
    }

    @FXML
    public void handleFilterButton(){

        if(filterToggleButton.isSelected()){
            filteredList.setPredicate(wantTodayItems);
            if(filteredList.isEmpty())
                textArea1.setText("No Tasks for Today");
            toDoListView.getSelectionModel().selectFirst();
        }
        else
            filteredList.setPredicate(wantAllItems);

    }

    @FXML
    public void handleExit(){
        Platform.exit();
    }

    @FXML
    public void openFile(){
        FileChooser chooser=new FileChooser();
        chooser.setTitle("Open File" +
                "");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text","*.txt"),
                new FileChooser.ExtensionFilter("PDF","*.pdf"),
                new FileChooser.ExtensionFilter("Image File","*.jpeg","*.jpg","*.png","*.gif"),
                new FileChooser.ExtensionFilter("All Files","*.*")
        );
        chooser.showOpenDialog(mainBorderPane.getScene().getWindow());
        //Select multiple file at once.
//        chooser.showOpenMultipleDialog(mainBorderPane.getScene().getWindow());
    }

    @FXML
    public void saveFile(){
        FileChooser chooser=new FileChooser();
        chooser.setTitle("Save Application File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text","*.txt"),
                new FileChooser.ExtensionFilter("PDF","*.pdf")
        );
        chooser.showSaveDialog(mainBorderPane.getScene().getWindow());
    }

}