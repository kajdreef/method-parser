package com.kajdreef.method_counter.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import com.kajdreef.method_counter.components.Component;

import java.util.List;

public interface JavaFileParser {
    public List<Component> parse(File file) throws FileNotFoundException;
}