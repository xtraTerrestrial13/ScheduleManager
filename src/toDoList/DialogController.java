package toDoList;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import toDoList.dataModel.ToDoData;
import toDoList.dataModel.ToDoItem;

import java.time.LocalDate;

public class DialogController {
@FXML
private TextField shortDescriptionField;
@FXML
private TextArea detailArea;
@FXML
private DatePicker deadlinePicker;

public ToDoItem processResults(){
    String shortDescription=shortDescriptionField.getText().trim();
    String details=detailArea.getText().trim();
    LocalDate deadline= deadlinePicker.getValue();
    ToDoItem item= new ToDoItem(shortDescription,details,deadline);
    ToDoData.getInstance().addToItem(item);
    return item;
}
}
