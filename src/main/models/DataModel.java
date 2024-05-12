package main.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DataModel {
    private final StringProperty test1;
    private final StringProperty test2;
    private final StringProperty test3;
    private final StringProperty test4;

    public DataModel(String test1, String test2, String test3, String test4) {
        this.test1 = new SimpleStringProperty(test1);
        this.test2 = new SimpleStringProperty(test2);
        this.test3 = new SimpleStringProperty(test3);
        this.test4 = new SimpleStringProperty(test4);
    }

    public StringProperty test1Property() {
        return test1;
    }

    public String getTest1() {
        return test1.get();
    }

    public void setTest1(String test1) {
        this.test1.set(test1);
    }

    public StringProperty test2Property() {
        return test2;
    }

    public String getTest2() {
        return test2.get();
    }

    public void setTest2(String test2) {
        this.test2.set(test2);
    }

    public StringProperty test3Property() {
        return test3;
    }

    public String getTest3() {
        return test3.get();
    }

    public void setTest3(String test3) {
        this.test3.set(test3);
    }

    public StringProperty test4Property() {
        return test4;
    }

    public String getTest4() {
        return test4.get();
    }

    public void setTest4(String test4) {
        this.test4.set(test4);
    }
}
