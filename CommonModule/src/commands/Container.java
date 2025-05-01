package commands;

import data.User;
import utility.ExecutionResponse;

import java.io.Serializable;

public class Container implements Serializable {
    private static final long serialVersionUID = 2L;
    private CommandTypes commandType;
    private String args;
    private ExecutionResponse answer;
    private User user;
    public Container(CommandTypes commandType, User user) {
        this.commandType = commandType;
        this.user = user;
    }
    public Container(User user){
        this.user = user;
    }
    public Container(CommandTypes commandType, String args, User user) {
        this.commandType = commandType;
        this.args = args;
        this.user=user;
    }
    public Container(ExecutionResponse answer){
        this.answer = answer;
    }

    public ExecutionResponse getAnswer(){
        return answer;
    }
    public CommandTypes getCommandType() {
        return commandType;
    }
    public String getArgs() {
        return args;
    }
    public User getUser(){return user;}
    public void setUser(User user){this.user = user;}
}