package dev.jlynx.langcontrol.util;

public class Constraints {

    public static final String PWD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,}$";
    public static final String USERNAME_REGEX = "^(?!\\w*_{2,})\\w*$";
    public static final String FIRSTNAME_REGEX = "^[a-zA-Z]*$";

    public static final int PWD_MIN = 8;
    public static final int PWD_MAX = 50;

    public static final int USERNAME_MIN = 4;
    public static final int USERNAME_MAX = 30;

    public static final int FIRSTNAME_MAX = 50;
}
