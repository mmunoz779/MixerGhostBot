package chat;

import com.mixer.api.MixerAPI;
import com.mixer.api.resource.chat.events.IncomingMessageEvent;
import com.mixer.api.services.impl.UsersService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

class Commands {
    private static final ArrayList<Command> commandsList = new ArrayList<>();
    private static final ArrayList<String> commandNamesList = new ArrayList<>();
    private final String fileDirectory = String.format("%s\\bin", System.getProperty("user.dir"));
    private final String filepath = String.format("%s\\bin\\commandList.txt", System.getProperty("user.dir"));

    public Commands() {
        Scanner input;
        try {
            if (!new File(fileDirectory).exists()) {
                new File(fileDirectory).mkdirs();
            }
            new File(filepath).createNewFile();
            input = new Scanner(new File(filepath));
            while (input.hasNextLine()) {
                String temp = input.nextLine();
                String[] tempSplit = temp.split(" : ");
                String commandName = tempSplit[0];
                String commandMessage = tempSplit[1];
                initializeCommandList(commandName, commandMessage);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean createCommand(String commandName, String commandMessage) {
        for (Command aCommandsList : commandsList) {
            if (aCommandsList.equals(new Command(commandName, commandMessage))) {
                return false;
            }
        }
        Command newCommand = new Command(commandName, commandMessage);
        commandsList.add(newCommand);
        PrintWriter pw;
        try {
            if (!new File(fileDirectory).exists()) {
                new File(fileDirectory).mkdirs();
            }
            new File(filepath).createNewFile();
            pw = new PrintWriter(new FileOutputStream(new File(filepath), true));
            pw.write(newCommand.toString() + "\n");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void removeCommand(String commandToRemove) {
        try {
            for (Command command : commandsList) {
                if (commandToRemove.equals(command.getCommandName())) {
                    commandsList.remove(command);
                    break;
                }
            }
            if (!new File(fileDirectory).exists()) {
                new File(fileDirectory).mkdirs();
            }
            new File(filepath).createNewFile();
            StringBuffer commandsOutput = new StringBuffer();
            for (Command command : commandsList) {
                commandsOutput.append(command.toString()).append("\n");
            }
            PrintWriter pw = new PrintWriter(new File(filepath));
            pw.write(commandsOutput.toString());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeCommandList(String commandName, String commandMessage) {
        for (Command aCommandsList : commandsList) {
            if (aCommandsList.equals(new Command(commandName, commandMessage))) {
                return;
            }
        }
        Command newCommand = new Command(commandName, commandMessage);
        commandsList.add(newCommand);
    }

    public ArrayList<String> getCommandNamesList() {
        commandNamesList.clear();
        for (Command aCommandsList : commandsList) {
            commandNamesList.add(aCommandsList.getCommandName());
        }
        return commandNamesList;
    }

    public String getCommandsAsString() {
        commandNamesList.clear();
        for (Command aCommandsList : commandsList) {
            commandNamesList.add(aCommandsList.getCommandName());
        }
        return commandNamesList.toString().substring(1, commandNamesList.toString().length() - 1);
    }

    public ArrayList<Command> getCommandsList() {
        return commandsList;
    }
}

class Command implements Comparable<Command> {
    private String commandName = null;
    private String commandMessage = null;

    public boolean equals(Command comparedTo) {
        return this.commandName.equals(comparedTo.commandName);
    }

    public String toString() {
        return this.commandName + " : " + this.commandMessage;
    }

    public Command(String commandName, String commandMessage) {
        this.commandName = commandName;
        this.commandMessage = commandMessage;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public String getCommandMessage() {
        return this.commandMessage;
    }

    public String getCommandMessageWithData(IncomingMessageEvent event, MixerAPI streamer) {
        try {
            System.out.println(this.commandMessage);
            String commandMessageWithEventData = this.commandMessage.replaceAll("@", "");
            if (commandMessageWithEventData.contains("$target"))
                commandMessageWithEventData = commandMessageWithEventData.replaceAll("\\$target", event.data.message.message.get(1).text);
            if (commandMessageWithEventData.contains("$user"))
                commandMessageWithEventData = commandMessageWithEventData.replaceAll("\\$user", event.data.userName);
            if (commandMessageWithEventData.contains("$caster"))
                commandMessageWithEventData = commandMessageWithEventData.replaceAll("\\$caster", streamer.use(UsersService.class).findOne(event.data.channel).get().username);
            System.out.println(commandMessageWithEventData);
            return commandMessageWithEventData;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public int compareTo(Command o) {
        return this.commandName.charAt(0) > o.commandName.charAt(0) ? 1 : this.commandName.charAt(0) == o.commandName.charAt(0) ? 0 : -1;
    }
}
