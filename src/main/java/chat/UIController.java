package chat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import java.io.IOException;

public class UIController {

    @FXML
    public BorderPane borderPane;
    @FXML
    public VBox loginVBox;
    @FXML
    public TextArea chatTextArea;
    @FXML
    public WebView authWebView;
    @FXML
    public Button streamerLoginButton;
    @FXML
    public TextArea usernameField;

    private Oauth oauth = new Oauth();

    public UIController() throws OAuthSystemException, OAuthProblemException, IOException {
    }

    public void initialize() {
    }

    @FXML
    public void handleLoginButtonClick() throws Exception {
        oauth.setUsername(usernameField.getText());
        oauth.authenticate();
    }

    @FXML
    public void handleLoginTextboxEnterPressed() {
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                oauth.setUsername(usernameField.getText());
                try {
                    loadAuthentication(new Oauth().generateAuthCode());
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (OAuthSystemException e1) {
                    e1.printStackTrace();
                } catch (OAuthProblemException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void loadAuthentication(String url) throws IOException {
        chatTextArea.setText("now logged in as " + usernameField.getText() + "\n");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("chat/authenticationUI.fxml"));
        Stage authStage = new Stage();
        authStage.setTitle("Authenticate GhostBot");
        Scene scene = new Scene(loader.load(), 1920, 1080);
        authStage.setScene(scene);
        authStage.show();
        AuthController controller = loader.getController();
        WebEngine webEngine = controller.loadWebPage(url);
        webEngine.setOnStatusChanged(e -> {
            if (e.getData().contains("localhost")) {
                System.out.println("redirected");
            }
        });
    }

    void appendChatTextArea(String text) {
        chatTextArea.appendText(text + "\n");
    }

    void bindScene(Scene scene) {
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        borderPane.prefHeightProperty().bind(scene.heightProperty());
    }

}
