package com.kajdreef.method_counter.components;

public class MethodSignature implements Component {

    public final String file_path;
    public final String cname;
    public final String mname;
    public final String rtype;
    public final int line_start;
    public final int line_end;
    public final boolean is_test;

    public MethodSignature(String file_path, String cname, String mname, String rtype, int line_start, int line_end, boolean is_test){
        this.file_path = file_path;
        this.cname = cname;
        this.mname = mname;
        this.rtype = rtype;
        this.line_start = line_start;
        this.line_end = line_end;
        this.is_test = is_test;
    }

    @Override
    public String asString(){
        return String.format(
            "MethodSignature(%s %s %s %s - %d,%d - test=%b)",
            this.file_path,
            this.rtype,
            this.cname,
            this.mname,
            this.line_start,
            this.line_end,
            this.is_test
        );
    }
}