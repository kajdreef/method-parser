package com.kajdreef.method_counter.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class DirectoryExplorer {

    public DirectoryExplorer() {}

    public List<File> get(String directory) {

        List<File> java_files = new LinkedList<>();
        try {
            Files.walk(Paths.get(directory))
                .filter(Files::isRegularFile)
                .filter((f) -> {
                    return f.toString().endsWith(".java");
                }).map((f) -> {
                    return f.toFile();
                })
                .forEach((f) -> {
                    java_files.add(f);
                });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return java_files;
    }
}