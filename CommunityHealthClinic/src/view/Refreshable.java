package view;

/** Implemented by every panel that must reload data when shown. */
public interface Refreshable {
    void refreshData();
}