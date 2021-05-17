package de.uni_marburg.sp21.filter;

public class CheckItem {
    private boolean isChecked;
    private String text;

    public CheckItem(String text){
        this.text = text;
        isChecked = false;
    }

    public void check() {
        isChecked = true;
    }

    public void unCheck() {
        isChecked = false;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getText() {
        return text;
    }
}
