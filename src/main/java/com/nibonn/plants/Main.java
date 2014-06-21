package com.nibonn.plants;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        PreProcess pp = new PreProcess(args[0], args[1]);
        pp.process();
    }
}
