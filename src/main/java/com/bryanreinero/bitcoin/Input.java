package com.bryanreinero.bitcoin;

/**
 * Created by brein on 6/5/2016.
 */
public class Input {

    Long sequence;
    Output prev_out;
    String script;

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public Output getPrev_out() {
        return prev_out;
    }

    public void setPrev_out(Output prev_out) {
        this.prev_out = prev_out;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
