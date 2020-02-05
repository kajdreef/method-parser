package com.kajdreef.method_counter.parsers;

import java.util.LinkedList;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import com.kajdreef.method_counter.components.MethodSignature;
import com.kajdreef.method_counter.components.Component;

public class MethodSignaturesParser implements JavaFileParser { 

    private File rootDirectory;

    public MethodSignaturesParser(){
        this.rootDirectory = null;
    }

    public void setRootFolder(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
    

    private boolean isTest(MethodDeclaration method) {
        NodeList<AnnotationExpr> annotations = method.getAnnotations();
        
        for (AnnotationExpr annotation : annotations) {
            if (annotation.getNameAsString().equals("Test")) {
                return true;
            }
        }
        return false;
    }

    private boolean isTest(ClassOrInterfaceDeclaration parent, MethodDeclaration method) {
        NodeList<ClassOrInterfaceType> extendTypes = parent.getExtendedTypes();
        
        if (! method.getName().getIdentifier().startsWith("test")) {
            return false;
        }

        for (ClassOrInterfaceType extend : extendTypes) {
            if (extend.getName().getIdentifier().equals("TestCase")) {
                return true;
            }
            else {
                System.out.println(extend.getName());
            }
        }
        return false;
    }

    public List<Component> parse(File file) throws FileNotFoundException {
        // Parse the code you want to inspect:
        CompilationUnit cu = null;

        try { 
            cu = StaticJavaParser.parse(file);
        } catch(Exception e) {
            System.err.println("failed to parse File: " + file.toString());
            return new LinkedList<Component>();
        }

        // Optional<PackageDeclaration> packageDecl = cu.getPackageDeclaration();
        // final String packageName;

        // if (packageDecl.isPresent()) {
        //     packageName = packageDecl.get().getNameAsString();
        // }
        // else {
        //     packageName = "";
        // }

        List<Component> method_list = new LinkedList<>();
        
        cu.findAll(MethodDeclaration.class).stream().forEach((MethodDeclaration method) -> {
            Type returnType = method.getType();
            
            Node node = method.getParentNode().get();

            if (node instanceof ClassOrInterfaceDeclaration){
                ClassOrInterfaceDeclaration parentNode = (ClassOrInterfaceDeclaration) method.getParentNode().get();
                
                method_list.add(
                    new MethodSignature(
                        this.rootDirectory.toPath().relativize(file.toPath()).toString(),
                        parentNode.getNameAsString(),
                        method.getSignature().asString(),
                        returnType.asString(),
                        method.getRange().get().begin.line,
                        method.getRange().get().end.line,
                        isTest(method) || isTest(parentNode, method)
                    )
                );
            } 
            else if (node instanceof EnumDeclaration){
                EnumDeclaration parentNode = (EnumDeclaration) method.getParentNode().get();
                method_list.add(
                    new MethodSignature(
                        this.rootDirectory.toPath().relativize(file.toPath()).toString(),
                        parentNode.getNameAsString(),
                        method.getSignature().asString(),
                        returnType.asString(),
                        method.getRange().get().begin.line,
                        method.getRange().get().end.line,
                        false
                    )
                );
            }
        });
        
        return method_list;
    }
}
