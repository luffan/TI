package sample;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class ReportController {

    public void setFieldKey(String fieldKey) {
        this.fieldKey.setText(fieldKey.trim());
    }

    public void setFieldText(String fieldText) {
        this.fieldText.setText(fieldText.trim());
    }

    public void setFieldResult(String fieldResult) {
        this.fieldResult.setText(fieldResult.trim());
    }

    @FXML
    private Text fieldKey;

    @FXML
    private Text fieldText;

    @FXML
    private Text fieldResult;


}
