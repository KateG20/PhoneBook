public class PhoneBookApp {
    public static void main(String[] args) {
        Thread shutdownHook = new Thread(PhoneBook::saveBook);
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        Menu.start();
    }
}
