package chat;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class AuthController {
    @FXML
    public WebView authWebView;

    public void initialize() {
    }

    public WebEngine loadWebPage(String url) {
        authWebView.autosize();
        WebEngine webEngine = authWebView.getEngine();
        webEngine.load(url);
        return webEngine;
    }
}
