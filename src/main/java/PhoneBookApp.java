import java.io.IOException;

public class PhoneBookApp {
    public static void main(String[] args) {
        PhoneBook book = PhoneBook.getBook();

        Thread shutdownHook = new Thread(PhoneBook::saveBook);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
//        try {
            Menu.start();
//        } catch (IOException e) {
//            System.out.println("Возникли какие-то проблемы с файлом телефонной книги!");
//            return;
//        }
    }
}
