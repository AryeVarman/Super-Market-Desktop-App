package progressTask;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;

public class ProgressTaskController {

    @FXML private Text fileNameText;
    @FXML private Text taskMassageText;
    @FXML private ProgressBar progressBar;
    @FXML private Text taskPercentText;
    @FXML private ScrollPane scrollPane;
    @FXML private Label fileLabel;

    public ProgressBar getProgressBar() { return progressBar; }

    public Text getFileNameText() { return fileNameText; }

    public Text getTaskMassageText() { return taskMassageText; }

    public Text getTaskPercentText() { return taskPercentText; }

    public ScrollPane getScrollPane() { return scrollPane; }

    public Label getFileLabel() { return fileLabel; }
}
