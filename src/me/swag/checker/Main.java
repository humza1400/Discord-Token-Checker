package me.swag.checker;

import javafx.application.Application;
import me.swag.checker.core.Core;

public class Main {

    public static final String NAME = "Discord Token Checker";

    public static void main(String[] args)
    {
        Application.launch(Core.class, args);
    }
}
