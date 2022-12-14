package code.me;

public class Main {
    public static void main(String[] args) {
        new ChatClient("localhost" , 5000).start();
    }
}
