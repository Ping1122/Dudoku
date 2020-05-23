package com.pingxin.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class FillCellRequest {

    @Min(value = 0, message = "Position index cannot less than 0")
    @Max(value = 8, message = "Position index cannot greater than 8")
    private int x;

    @Min(value = 0, message = "Position index cannot less than 0")
    @Max(value = 8, message = "Position index cannot greater than 8")
    private int y;

    @Min(value = 1, message = "Value cannot less than 1")
    @Max(value = 9, message = "Value cannot greater than 9")
    private int value;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
