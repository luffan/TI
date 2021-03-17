package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BasicController {

    private Stage dialogStage;

    private Character[] englishAlphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u',
            'v', 'w', 'x', 'y', 'z'};

    private Character[] russianAlphabet = {'а', 'б', 'в', 'г', 'д', 'е', 'ё',
            'ж', 'з', 'и', 'й', 'к', 'л', 'м',
            'н', 'о', 'п', 'р', 'с', 'т', 'у',
            'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ',
            'ы', 'ь', 'э', 'ю', 'я',
    };


    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private RadioButton realFenceButton;

    @FXML
    private ToggleGroup groupChiper;

    @FXML
    private RadioButton columnMethodButton;

    @FXML
    private RadioButton vigenereButton;

    @FXML
    private TextField keyTextField;

    @FXML
    private TextArea inputTextField;

    @FXML
    private RadioButton encryptButton;

    @FXML
    private ToggleGroup encryptionGroup;

    @FXML
    private RadioButton decryptButton;

    private HashMap<RadioButton, Cipher> map = new HashMap<>();

    @FXML
    void initialize() {
        assert realFenceButton != null : "fx:id=\"realFenceButton\" was not injected: check your FXML file 'basicWindow.fxml'.";
        assert groupChiper != null : "fx:id=\"groupChiper\" was not injected: check your FXML file 'basicWindow.fxml'.";
        assert columnMethodButton != null : "fx:id=\"columnMethodButton\" was not injected: check your FXML file 'basicWindow.fxml'.";
        assert vigenereButton != null : "fx:id=\"vigenereButton\" was not injected: check your FXML file 'basicWindow.fxml'.";
        assert keyTextField != null : "fx:id=\"keyTextField\" was not injected: check your FXML file 'basicWindow.fxml'.";
        assert inputTextField != null : "fx:id=\"inputTextField\" was not injected: check your FXML file 'basicWindow.fxml'.";
        assert encryptButton != null : "fx:id=\"encryptButton\" was not injected: check your FXML file 'basicWindow.fxml'.";
        assert encryptionGroup != null : "fx:id=\"encryptionGroup\" was not injected: check your FXML file 'basicWindow.fxml'.";
        assert decryptButton != null : "fx:id=\"decryptButton\" was not injected: check your FXML file 'basicWindow.fxml'.";

        map.put(realFenceButton, new RealFence(new ArrayList<>(Arrays.asList(englishAlphabet))));
        map.put(columnMethodButton, new Column(new ArrayList<>(Arrays.asList(englishAlphabet))));
        map.put(vigenereButton, new Vigenera(new ArrayList<>(Arrays.asList(russianAlphabet))));

    }


    @FXML
    private void onDownloadText(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        // Задаём фильтр расширений
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "txt files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Показываем диалог загрузки файла
        File file = fileChooser.showOpenDialog(dialogStage);

        if (file != null) {
            String data = readData(file);
            inputTextField.setText(data);
        }
    }

    @FXML
    private void onShowReport(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("report_window.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Report");
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        ReportController controller = loader.getController();
        controller.setFieldKey(keyTextField.getText());
        controller.setFieldText(inputTextField.getText());
        controller.setFieldResult(readData(new File("output.txt")));
        stage.show();
    }

    @FXML
    private void onStart(ActionEvent event) {
        String errorMessage;
        if ((errorMessage = isInputValid()).length() != 0) {
            showAlert(errorMessage);
            return;
        }
        Cipher cipher = map.get((RadioButton) groupChiper.getSelectedToggle());
        cipher.setKey(keyTextField.getText());
        cipher.setText(inputTextField.getText());
        cipher.transform();

        if ((errorMessage = cipher.isValidDate()).length() != 0) {
            showAlert(errorMessage);
            return;
        }

        String result;
        if (encryptButton.isSelected()) {
            result = cipher.encrypt();
        } else {
            result = cipher.decrypt();
        }

        writeData(new File("output.txt"), result);
    }

    private String isInputValid() {
        StringBuilder errorMessage = new StringBuilder();
        if (groupChiper.getSelectedToggle() == null) {
            errorMessage.append("Не был выбран алгоритм шифрования/дешифрования\n");
        }
        if (encryptionGroup.getSelectedToggle() == null) {
            errorMessage.append("Не было выбрано, что делать с текстом: шифровать/дешифровать\n");
        }
        if (keyTextField.getText() == null || keyTextField.getText().length() == 0) {
            errorMessage.append("Поле ключа не должно быть пустым\n");
        }
        if (inputTextField.getText() == null || inputTextField.getText().length() == 0) {
            errorMessage.append("Поле шифрируемого/дешифрируемого текста не должно быть пустым\n");
        }
        return errorMessage.toString();
    }

    private void showAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setTitle("Invalid Fields");
        alert.setHeaderText("Please correct invalid fields");
        TextArea textArea = new TextArea(errorMessage);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(new Label("Exception:"), 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    private String readData(File file) {
        String result = "";
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(file));
            String temp;
            while ((temp = buffer.readLine()) != null) {
                result += temp + "\n";
            }
            buffer.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void writeData(File file, String text) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(text);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
