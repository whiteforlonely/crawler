package com.ake.ckey;

public class ColorRegexTest {

    public static void main(String[] args) {
        System.out.println("122 222 122".matches("([0-9]{1,3}\\s){2}[0-9]{1,3}(\\s0\\.[0-9]+)?"));
    }
}
