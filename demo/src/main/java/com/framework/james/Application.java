package com.framework.james;

import com.framework.james.starter.MiniApplication;

public class Application {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        MiniApplication.run(Application.class, args);
    }
}
