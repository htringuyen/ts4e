package io.javaside.ts4e.core;

import org.junit.jupiter.api.Test;

public class DummyTest {

    // ANSI escape codes for text colors
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";

    @Test
    void printWithColors() {
        System.out.print(RED + "This ");
        System.out.print(GREEN + "is ");
        System.out.print(YELLOW + "a ");
        System.out.print(BLUE + "colorful ");
        System.out.print(PURPLE + "sentence." + RESET + "\n");
    }
}
