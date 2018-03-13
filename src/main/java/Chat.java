import com.mixer.api.MixerAPI;
import com.mixer.api.http.HttpBadResponseException;
import com.mixer.api.resource.MixerUser;
import com.mixer.api.resource.chat.MixerChat;
import com.mixer.api.resource.chat.events.IncomingMessageEvent;
import com.mixer.api.resource.chat.methods.AuthenticateMessage;
import com.mixer.api.resource.chat.methods.ChatSendMethod;
import com.mixer.api.resource.chat.methods.WhisperMethod;
import com.mixer.api.resource.chat.replies.AuthenticationReply;
import com.mixer.api.resource.chat.replies.ReplyHandler;
import com.mixer.api.resource.chat.ws.MixerChatConnectable;
import com.mixer.api.services.impl.ChatService;
import com.mixer.api.services.impl.UsersService;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import javax.security.auth.Refreshable;
import javax.sound.sampled.Mixer;
import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;


public class Chat {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ArrayList<MixerChatConnectable> chatConnectable = new ArrayList<>();
        Commands commands = new Commands();


        // Oauth 2.0
        try {
            Oauth streamerAuthentication = new Oauth();
            if (streamerAuthentication.getAccessToken() == null)
                streamerAuthentication.authenticate();
            try {
                MixerAPI testAuth = new MixerAPI(streamerAuthentication.getAccessToken());
                testAuth.use(UsersService.class).getCurrent().get();
            } catch (ExecutionException e) {
                streamerAuthentication.refresh();
            }

            //Create mixer API connection for both bot and streamer

            MixerAPI botMixer = new MixerAPI("9jxYKH6us0RtC8p0BsH7LdcGFbt6IO08nmRvUt5MRgYPJCitMWrXEgBvvuyPkCPe"); // Ghostbot's auth: 9jxYKH6us0RtC8p0BsH7LdcGFbt6IO08nmRvUt5MRgYPJCitMWrXEgBvvuyPkCPe
            MixerAPI streamerMixer = new MixerAPI(streamerAuthentication.getAccessToken());

            MixerUser bot = botMixer.use(UsersService.class).getCurrent().get();
            MixerUser streamer = streamerMixer.use(UsersService.class).getCurrent().get();

            MixerChat chat = botMixer.use(ChatService.class).findOne(streamer.channel.id).get();
            chatConnectable.add(chat.connectable(streamerMixer));
            if (chatConnectable.get(0).connect()) {
                chatConnectable.get(0).send(AuthenticateMessage.from(streamer.channel, bot, chat.authkey), new ReplyHandler<AuthenticationReply>() {
                    public void onSuccess(AuthenticationReply reply) {
                        //chatConnectable.get(0).send(WhisperMethod.builder().send("Bot is online").to(streamerMixer.use(UsersService.class).findOne(590964).get()).build());
                        chatConnectable.get(0).send(WhisperMethod.builder().send("Bot is online").to(streamer).build());
                        System.out.println("Bot is online");
                        class ShutDownHook extends Thread {
                            @Override
                            public void run() {
                                synchronized (chatConnectable) {
                                    try {
                                        chatConnectable.get(0).send(WhisperMethod.builder().send("Bot shutting down").to(streamer).build());
                                        //chatConnectable.get(0).send(WhisperMethod.builder().send("Bot is shutting down").to(streamerMixer.use(UsersService.class).findOne(590964).get()).build());
                                        System.out.println("Bot shutting down");
                                        chatConnectable.wait(5);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        ShutDownHook shutDownHook = new ShutDownHook();
                        Runtime.getRuntime().addShutdownHook(shutDownHook);
                    }

                    public void onFailure(Throwable var1) {
                        var1.printStackTrace();
                    }
                });
            }

            chatConnectable.get(0).on(IncomingMessageEvent.class, event -> {
                String[] message = {""};
                String[] commandName = {""};
                String[] target = {""};

                String command = event.data.message.message.get(0).text;

                event.data.message.message.stream().forEach(e -> message[0] += e.text);
                System.out.print(String.format("%s: %s\n", event.data.userName, message[0]));
                if (!event.data.userName.equals("GhostBot779")) {
                    if (event.data.userRoles.contains(MixerUser.Role.MOD) || event.data.userRoles.contains(MixerUser.Role.OWNER)) {
                        if (command.toLowerCase().startsWith("!shutdown")) {
                            System.exit(0);
                        }
                        if (event.data.message.message.get(0).text.startsWith("!command add ")) {
                            message[0] = message[0].substring(13);
                            for (int i = 0; message[0].charAt(i) != ' '; i++) {
                                commandName[0] += message[0].charAt(i);
                            }
                            message[0] = message[0].substring(commandName[0].length() + 1);
                            try {
                                if (commands.createCommand(commandName[0], message[0])) {
                                    //chatConnectable.get(0).send(WhisperMethod.builder().send("Command created successfully").to(streamerMixer.use(UsersService.class).findOne(590964).get()).build());
                                    chatConnectable.get(0).send(WhisperMethod.builder().send("Command created successfully").to(streamerMixer.use(UsersService.class).findOne(event.data.userId).get()).build());
                                } else {
                                    //chatConnectable.get(0).send(WhisperMethod.builder().send("Command already exists").to(streamerMixer.use(UsersService.class).findOne(590964).get()).build());
                                    chatConnectable.get(0).send(WhisperMethod.builder().send("Command already exists").to(streamerMixer.use(UsersService.class).findOne(event.data.userId).get()).build());
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                        if (event.data.message.message.get(0).text.startsWith("!command remove ")) {
                            String[] messageSplit = message[0].split(" ");
                            commandName[0] = messageSplit[2];
                            commands.removeCommand(commandName[0]);
                        }
                        if (event.data.message.message.get(0).text.startsWith("!command edit ")) {
                            String[] messageSplit = message[0].split(" ");
                            commandName[0] = messageSplit[2];
                            message[0] = "";
                            for (int i = 3; i < messageSplit.length; i++) {
                                message[0] += messageSplit[i] + " ";
                            }
                            commands.removeCommand(commandName[0]);
                            commands.createCommand(messageSplit[2], message[0]);
                        }
                    }
                    if (commands.getCommandNamesList().contains(event.data.message.message.get(0).text.split(" ")[0])) {
                        for (Command aCommand : commands.getCommandsList()) {
                            if (aCommand.getCommandName().equals(event.data.message.message.get(0).text.split(" ")[0])) {
                                target[0] = event.data.message.message.get(0).text.split(" ")[1];
                                chatConnectable.get(0).send(ChatSendMethod.of(aCommand.getCommandMessageWithData(event, streamer)));
                                break;
                            }
                        }
                    }
                    if (command.toLowerCase().startsWith("!ghostcommands")) {
                        try {
                            chatConnectable.get(0).send(WhisperMethod.builder().send((commands.getCommandsAsString())).to(streamerMixer.use(UsersService.class).findOne(event.data.userId).get()).build());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    if (command.toLowerCase().startsWith("!ghostbot")) {
                        chatConnectable.get(0).send(ChatSendMethod.of((String.format("Hello @%s! I am ghostbot, a bot created by GhostITM. Feel free to ask him more about me!", event.data.userName))));
                    }
                    if (command.split(" ")[0].toLowerCase().equals("!followers")) {
                        try {
                            URL url = new URL(String.format("https://mixer.com/api/v1/channels/%s", event.data.channel));
                            StringBuilder response = new StringBuilder();
                            Scanner input = new Scanner(url.openStream());
                            input.useDelimiter("\\{|:|}|\"|,");
                            while (input.hasNext()) {
                                String temp = input.next();
                                if (temp.contains("numFollowers")) {
                                    input.next();
                                    chatConnectable.get(0).send(ChatSendMethod.of(String.format("%s currently has %s followers", streamer.username, input.next())));
                                }
                            }
                            chatConnectable.get(0).send(ChatSendMethod.of(response.toString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


        } catch (IOException | OAuthProblemException | OAuthSystemException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    private PriorityQueue<String> createQueue() {
        return new PriorityQueue<>();
    }


}