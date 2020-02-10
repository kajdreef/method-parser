package com.kajdreef.method_counter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import java.sql.*;

import com.kajdreef.method_counter.support.DirectoryExplorer;
import com.kajdreef.method_counter.components.Component;
import com.kajdreef.method_counter.components.MethodSignature;
import com.kajdreef.method_counter.parsers.*;

public class Launcher {
    
    DirectoryExplorer explorer;
    MethodSignaturesParser parser;

    public Launcher() {
        this.explorer = new DirectoryExplorer();
        this.parser = new MethodSignaturesParser();
    }

    public void storeInDatabase(String project, List<Component> list, String databasePath) {
        System.out.println("Store in Database...");
        Connection c = null;

        try {
            Class.forName("org.sqlite.JDBC");
            String database_path = String.format("jdbc:sqlite:%s", databasePath);
            
            c = DriverManager.getConnection(database_path);
            c.setAutoCommit(false);

            Statement statement = c.createStatement();
            String sql_create_table = (
                "CREATE TABLE IF NOT EXISTS methods ("
                + "project text NOT NULL,"
                + "file_path text NOT NULL,"
                + "class_name text NOT NULL,"
                + "method_name text NOT NULL,"
                + "rtype text NOT NULL,"
                + "line_start int NOT NULL,"
                + "line_end int NOT NULL," 
                + "is_test int NOT NULL,"
                + "PRIMARY KEY (project, file_path, class_name, method_name, rtype, line_start, line_end)"
                + ");"
            );

            statement.executeUpdate(sql_create_table);
            statement.close();
            

            String sql_insert_table = (
                "INSERT INTO methods (project, file_path, class_name, method_name, rtype, line_start, line_end, is_test) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );
            
            System.out.println(list.size());
            PreparedStatement preparedStatement;

            for (Component item :  list) {
                preparedStatement = c.prepareStatement(sql_insert_table);
                MethodSignature method = (MethodSignature) item;
                try {
                    preparedStatement.setString(1, project);
                    preparedStatement.setString(2, method.file_path);
                    preparedStatement.setString(3, method.cname);
                    preparedStatement.setString(4, method.mname);
                    preparedStatement.setString(5, method.rtype);
                    preparedStatement.setInt(6, method.line_start);
                    preparedStatement.setInt(7, method.line_end);
                    preparedStatement.setBoolean(8, method.is_test);
                    preparedStatement.execute();
                } catch(Exception e) {
                    System.err.println(String.format("%s - %s", e.getMessage(), method.asString()));
                }
                finally {
                    preparedStatement.close();
                }
            }
            
            c.commit();
            
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            
            try{
                c.setAutoCommit(true);
                c.close();
            } catch(SQLException e) {
                System.err.println(e);
            }
        }
    }

    public void start(String directory, String databasePath) {
        List<File> java_files = this.explorer.get(directory);
        List<Component> list = new LinkedList<>();
        File rootDirectory = new File(directory);

        parser.setRootFolder(rootDirectory);
        for (File file: java_files) {
            try {
                list.addAll(this.parser.parse(file));
            } catch (FileNotFoundException e) {
                System.out.println("File Not Found: " + file.toString());
            }
        }

        String[] path = directory.split("/");
        this.storeInDatabase(path[path.length - 1], list, databasePath);
    }

    public static void main(String[] args) {
        String rootDirectoryProject;
        String databasePath;

        if (args.length == 1) {
            rootDirectoryProject = args[0]; 
            databasePath = "/Users/spideruci/shared/data/experiment_1.db";
        }
        else {
            System.exit(0);
            return;
        }

        System.out.println(String.format("%s - %s", rootDirectoryProject, databasePath));
        
        new Launcher().start(rootDirectoryProject, databasePath);
    }
}
