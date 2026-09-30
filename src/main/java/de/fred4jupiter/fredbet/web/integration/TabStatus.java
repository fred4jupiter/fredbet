package de.fred4jupiter.fredbet.web.integration;

public class TabStatus {

    private boolean tab1Active;

    private boolean tab2Active;

    private boolean tab2Disabled;

    public TabStatus() {
        this.tab1Active = false;
        this.tab2Active = false;
    }

    public boolean isTab1Active() {
        return tab1Active;
    }

    public void setTab1Active(boolean tab1Active) {
        this.tab1Active = tab1Active;
    }

    public boolean isTab2Active() {
        return tab2Active;
    }

    public void setTab2Active(boolean tab2Active) {
        this.tab2Active = tab2Active;
    }

    public boolean isTab2Disabled() {
        return tab2Disabled;
    }

    public void setTab2Disabled(boolean tab2Disabled) {
        this.tab2Disabled = tab2Disabled;
    }
}
