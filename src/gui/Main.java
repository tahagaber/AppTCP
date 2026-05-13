package gui;

public class Main {
    public static void main(String[] args) {
        // This bypasses the Java module system restrictions for JavaFX
        // by placing the main method in a class that does NOT extend Application.
        ChatClientGUI.main(args);
    }
}
