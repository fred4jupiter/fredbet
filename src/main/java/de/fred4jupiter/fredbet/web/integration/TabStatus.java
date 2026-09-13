package de.fred4jupiter.fredbet.web.integration;

public class TabStatus {

    private boolean tab1Active;
    private boolean tab2Active;
    private boolean tab3Active;

    public TabStatus() {
        this.tab1Active = true;
        this.tab2Active = false;
        this.tab3Active = false;
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

    public boolean isTab3Active() {
        return tab3Active;
    }

    public void setTab3Active(boolean tab3Active) {
        this.tab3Active = tab3Active;
    }
}
