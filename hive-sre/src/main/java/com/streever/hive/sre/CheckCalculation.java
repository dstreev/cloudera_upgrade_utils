package com.streever.hive.sre;

public class CheckCalculation implements Cloneable {

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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        CheckCalculation rtn = (CheckCalculation)super.clone();
        rtn.setFail(this.fail);
        rtn.setTest(this.test);
        rtn.setPass(this.pass);
        return super.clone();
    }

    @Override
    public String toString() {
        return "CheckCalculation{}";
    }
}
