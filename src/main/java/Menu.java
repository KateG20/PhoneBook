import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.*;

public class Menu {
    private static final Scanner sc = new Scanner(System.in);
    private static final PhoneBook book = PhoneBook.getBook();

    public static void start() {
        while (true) {
            System.out.println("Выберите действие (напишите число от 1 до 5):\n" +
                    "1) Поиск контакта по имени\n2) Добавить новый контакт\n3) Удалить контакт\n4) " +
                    "Вывести телефонную книгу на экран\n5) Выйти из приложения");
            int inp = Utils.inputMenuNumber(5);
            switch (inp) {
                case 1:
                    findContactsByName();
                    break;
                case 2:
                    addContact();
                    break;
                case 3:
                    deleteContact();
                    break;
                case 4:
                    printAll();
                    break;
                case 5:
                    return;
            }
        }
    }

    private static void printAll() {
        Collection<Contact> allContacts = book.getContacts();

        if (allContacts.size() != 0) {
            System.out.println("Вот список ваших контактов:\n");
            allContacts.forEach(System.out::println);
        }
        else System.out.println("Телефонная книга пуста!\n");
    }

    private static void deleteContact() {
        System.out.println("Введите полное имя человека, контакт которого хотите удалить:");
        String fio = sc.nextLine().toLowerCase().trim();
        while (!fio.matches("^\\p{L}+ \\p{L}+ \\p{L}+$")) {
            System.out.println("Неверный ввод, попробуйте еще раз: ");
            fio = sc.nextLine().toLowerCase().trim();
        }
        List<Contact> matches = book.findByFio(fio);
        if (matches.size() == 0) {
            System.out.println("Контакт с таким именем не найден!\n");
        } else if (matches.size() > 1) {
            System.out.println("Найдено несколько совпадений:");
            for (int i = 0; i < matches.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, matches.get(i).toString());
            }
            System.out.print("Введите номер контакта, который хотите удалить: ");
            int toDelete = Utils.inputMenuNumber(matches.size());
            book.deleteContact(matches.get(toDelete - 1).getId());
            System.out.println("Контакт удален.\n");
        } else {
            System.out.println(matches.get(0).toString());
            book.deleteContact(matches.get(0).getId());
            System.out.println("Контакт удален.\n");
        }
    }

    private static void findContactsByName() {
        System.out.println("Введите фрагмент имени человека, контакт которого хотите найти: ");
        sc.nextLine();
        String fragment = sc.nextLine().toLowerCase().trim();
        List<Contact> matches = book.findMatches(fragment);

        if (matches.size() != 0) {
            System.out.println("Вот что нашлось: ");
            matches.forEach(System.out::println);
        }
        else System.out.println("Ничего не нашлось!");
    }

    private static void addContact() {
        book.addContact(createContact());
        System.out.println("Контакт добавлен!\n");
    }

    private static Contact createContact() {
        System.out.println("Звездочками будут помечены поля, обязательные для ввода. " +
                "Чтобы оставить необязательное поле пустым, нажмите Enter.\n" +
                "(имена контактов лучше писать латиницей!)");
        String[] name = createName();
        List<String> numbers = createNumberList();
        String address = createAddress();
        Calendar date = createDateOfBirth();
        String email = createEmail();

        return new Contact(name[0], name[1], name[2], address, numbers, date, email);
    }

    private static String[] createName() {
        System.out.println("* Введите фамилию, имя и отчество через пробел: ");
        String name = sc.nextLine().toLowerCase().trim();

        // Почему-то не работает на русские буквы Я НЕ ЗНАЮ ПОЧЕМУ ОНО ДОЛЖНО.
        while (!name.matches("\\p{L}+ \\p{L}+ \\p{L}+")) {
            System.out.println("Неверный ввод, попробуйте еще раз: ");
            name = sc.nextLine().toLowerCase().trim();
        }
        return name.split(" ");
    }

    private static String createEmail() {
        System.out.println("Введите e-mail:");
        String email = sc.nextLine().trim();
        if (email.length() == 0)
            return null;
        while (true) {
            try {
                InternetAddress emailAddr = new InternetAddress(email);
                emailAddr.validate();
            } catch (AddressException ex) {
                System.out.println("Некорректный e-mail, попробуйте еще раз:");
                email = sc.nextLine().trim();
                continue;
            }
            break;
        }
        return email;
    }

    private static Calendar createDateOfBirth() {
        String datePattern = "^(?:(?:31(\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\.)(?:0?[13-9]|1[0-2])\\2))" +
                "(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]" +
                "|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\.)(?:(?:0?[1-9])|" +
                "(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
        System.out.println("Введите дату рождения в формате дд.мм.гггг: ");
        String dateStr = sc.nextLine().trim();
        if (dateStr.length() == 0)
            return null;
        while (!dateStr.matches(datePattern)) {
            System.out.println("Некорректная дата, попробуйте еще раз: ");
            dateStr = sc.nextLine().trim();
        }
        return new GregorianCalendar(Integer.parseInt(dateStr.substring(6)),
                Integer.parseInt(dateStr.substring(3, 5)) - 1,
                Integer.parseInt(dateStr.substring(0, 2)));
    }

    private static List<String> createNumberList() {
        List<String> numbers = new ArrayList<>();
        System.out.println("* Введите номер телефона без дефисов и пробелов, в российском формате: ");
        String number = Utils.scanNumber();
        do {
            while (!number.matches("\\d{10}") || numbers.contains(number) ||
                    !book.validNumber(number)) {
                System.out.println("Номер телефона некорректный или уже записан другому " +
                        "контакту, попробуйте еще раз:");
                number = Utils.scanNumber();
            }
            numbers.add(number);
            System.out.println("Если вы хотите добавить еще один номер телефона, введите его, " +
                    "иначе нажмите Enter.");
            number = Utils.scanNumber();
        } while (number.length() != 0);
        return numbers;
    }

    private static String createAddress() {
        System.out.println("Введите адрес проживания этого человека (любой формат):");
        String address = sc.nextLine().trim();
        if (address.length() == 0)
            return null;
        return address;
    }
}
