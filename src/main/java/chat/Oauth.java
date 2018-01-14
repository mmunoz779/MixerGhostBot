package chat;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import java.io.*;
import java.util.Scanner;

class Oauth implements Serializable {
    private String refreshToken = null;
    private String accessToken = null;
    private String expiresIn = null;
    private String currentStreamerUsername;
    private final String fileDirectory = String.format("%s\\bin", System.getProperty("user.dir"));
    private final String filepath = String.format("%s\\bin\\currentStreamer.txt", System.getProperty("user.dir"));

    public Oauth() throws IOException, OAuthProblemException, OAuthSystemException {
        Scanner userInput = new Scanner(System.in);
        Scanner input = null;
        //boolean useCurrentStreamer = false;
        if (!new File(fileDirectory).exists()) {
            (new File(fileDirectory)).mkdirs();
        }
        if (new File(filepath).createNewFile()) {
            System.out.println("Please enter the username for the streamer you want to authenticate: ");
            this.currentStreamerUsername = userInput.nextLine();
        } else {
            input = new Scanner(new File(filepath));
            if (input.hasNextLine()) {
                this.currentStreamerUsername = input.nextLine();
            }
//            System.out.println(String.format("Do you want to sign in as %s? (y/n)", this.currentStreamerUsername));
//            switch (userInput.next().toLowerCase()) {
//                case "y":
//                    useCurrentStreamer = true;
//                    break;
//                case "n":
//                    useCurrentStreamer = false;
//                    userInput = new Scanner(System.in);
//                    System.out.println("Please enter the username for the streamer you want to authenticate: ");
//                    this.currentStreamerUsername = userInput.nextLine();
//                    this.accessToken = null;
//                    this.expiresIn = null;
//                    break;
//            }
        }
        if (input.hasNextLine()) {
            this.accessToken = input.nextLine();
        }
        if (input.hasNextLine()) {
            this.refreshToken = input.nextLine();
        }
        if (input.hasNextLine()) {
            this.expiresIn = input.nextLine();
        }
        input.close();
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getExpiresIn() {
        return this.expiresIn;
    }

    public String getCurrentStreamerUsername() {
        return this.currentStreamerUsername;
    }

    public void refresh() throws OAuthSystemException, IOException, OAuthProblemException {
        PrintWriter pw = new PrintWriter(new File(filepath));
        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation("https://mixer.com/api/v1/oauth/token")
                .setGrantType(GrantType.REFRESH_TOKEN)
                .setClientId("630d7b67d18433a1eed514ae9b24134e6ab1ede6de58a896")
                .setClientSecret("d0fa8e508c2ebe177181b2c300115bb3dc9b9cf828a86db5f80112d028d9be73")
                .setRedirectURI("http://localhost/")
                .setRefreshToken(this.refreshToken)
                .buildBodyMessage();

        OAuthClient client = new OAuthClient(new org.apache.oltu.oauth2.client.URLConnectionClient());
        OAuthJSONAccessTokenResponse oauthResponse = client.accessToken(request);
        String refreshToken = oauthResponse.getRefreshToken();
        String accessToken = oauthResponse.getAccessToken();
        String expiresIn = String.valueOf(oauthResponse.getExpiresIn());
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        pw.println(this.currentStreamerUsername);
        pw.println(this.accessToken);
        pw.println(this.refreshToken);
        pw.println(this.expiresIn);
        pw.close();
    }

    public void authenticate() throws OAuthSystemException, IOException, OAuthProblemException {
        PrintWriter pw = new PrintWriter(new File(filepath));
        OAuthClientRequest request = OAuthClientRequest
                .authorizationLocation("https://mixer.com/oauth/authorize")
                .setResponseType("code")
                .setScope("chat:connect channel:analytics:self chat:whisper chat:remove_message chat:clear_messages chat:bypass_slowchat chat:bypass_links chat:poll_start") //space separated scopes
                .setClientId("630d7b67d18433a1eed514ae9b24134e6ab1ede6de58a896")
                .setRedirectURI("http://localhost/")
                .buildQueryMessage();
        //in web application you make redirection to uri:
        System.out.println("Visit: " + request.getLocationUri() + "\nand grant permission");

        System.out.print("Now enter the OAuth code you have received in redirect uri: \n");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String code = br.readLine();

        request = OAuthClientRequest
                .tokenLocation("https://mixer.com/api/v1/oauth/token")
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId("630d7b67d18433a1eed514ae9b24134e6ab1ede6de58a896")
                .setClientSecret("d0fa8e508c2ebe177181b2c300115bb3dc9b9cf828a86db5f80112d028d9be73")
                .setRedirectURI("http://localhost/")
                .setCode(code)
                .buildBodyMessage();

        OAuthClient client = new OAuthClient(new org.apache.oltu.oauth2.client.URLConnectionClient());
        OAuthJSONAccessTokenResponse oauthResponse = client.accessToken(request);
        String refreshToken = oauthResponse.getRefreshToken();
        String accessToken = oauthResponse.getAccessToken();
        String expiresIn = String.valueOf(oauthResponse.getExpiresIn());
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        pw.println(this.currentStreamerUsername);
        pw.println(this.accessToken);
        pw.println(this.refreshToken);
        pw.println(this.expiresIn);
        pw.close();
    }

}
