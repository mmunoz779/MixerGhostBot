package chat;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class UIController {

    @FXML
    public BorderPane borderPane;
    @FXML
    public VBox loginVBox;
    @FXML
    public TextArea chatTextArea;
    @FXML
    public WebView webView;
    @FXML
    public Button streamerLoginButton;
    @FXML
    public TextArea usernameField;

    public void initialize() {
        WebEngine webEngine = webView.getEngine();
        webEngine.load("https://google.com");
        streamerLoginButton.setText("Login");
    }

    @FXML
    public void handleLoginButtonClick() throws Exception {
        loadAuthentication();
    }

    @FXML
    public void handleLoginTextboxEnterPressed() throws Exception {
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER))
                loadAuthentication();
        });
    }

    private void loadAuthentication() {
        chatTextArea.appendText("new username:\t" + usernameField.getText() + "\n");
        webView.setVisible(true);
        WebEngine webEngine = webView.getEngine();
        webEngine.load("https://mixer.com");
        usernameField.setVisible(false);
        streamerLoginButton.setVisible(false);
    }
}
