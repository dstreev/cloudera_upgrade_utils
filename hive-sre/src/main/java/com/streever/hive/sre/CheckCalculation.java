package com.streever.hive.sre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.PrintStream;
import java.util.Arrays;

@JsonIgnoreProperties({"scriptEngine"})
public class CheckCalculation {

    // Needs to return a boolean.
    private String test;
    // On Pass, use Success Message
    private String pass;
    // On Fail, use Fail Message
    private String fail;
    // Include external values in calculations.
    // These parameters are added to the end of the record set
    //   passed to the engine.
    private String[] params;

    /**
     * allows stdout to be captured if necessary
     */
    public PrintStream successStream = System.out;
    /**
     * allows stderr to be captured if necessary
     */
    public PrintStream errorStream = System.err;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getFail() {
        return fail;
    }

    public void setFail(String fail) {
        this.fail = fail;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public PrintStream getSuccessStream() {
        return successStream;
    }

    public void setSuccessStream(PrintStream successStream) {
        this.successStream = successStream;
    }

    public PrintStream getErrorStream() {
        return errorStream;
    }

    public void setErrorStream(PrintStream errorStream) {
        this.errorStream = errorStream;
    }

//    @Override
//    protected Object clone() throws CloneNotSupportedException {
//        CheckCalculation rtn = (CheckCalculation) super.clone();
//        rtn.setFail(this.fail);
//        rtn.setTest(this.test);
//        rtn.setPass(this.pass);
//        return super.clone();
//    }
//
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CheckCalculation that = (CheckCalculation) o;

        if (test != null ? !test.equals(that.test) : that.test != null) return false;
        if (pass != null ? !pass.equals(that.pass) : that.pass != null) return false;
        if (!fail.equals(that.fail)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        int result = test != null ? test.hashCode() : 0;
        result = 31 * result + (pass != null ? pass.hashCode() : 0);
        result = 31 * result + fail.hashCode();
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }

    @Override
    public String toString() {
        return "CheckCalculation{}";
    }
}
