package com.proper.enterprise.isj.exception;

public class DelayException extends AbstractIHosException {

    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = 8992766393049854581L;

    private int position;

    public DelayException(int position) {
        this.position = position;

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
