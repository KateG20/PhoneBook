import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.*;

public class Menu {
    private static final Scanner sc = new Scanner(System.in);
    private static final PhoneBook book = PhoneBook.getBook();

    public static void start() {
//        try {
//        book = PhoneBook.getBook();}
//        catch (FileNotFoundException e) {
//            System.out.println("Файл телефонной книги не был найден в директории по умолчанию. " +
//                    "Она либо была перемещена, либо еще не была создана. Создать новую книгу? (да/нет) ");
//            if (sc.nextLine().toLowerCase().equals("да"))
//
//        }

        System.out.println("Выберите действие (напишите число от 1 до 4):\n" +
                "1) Поиск контакта\n2) Добавить новый контакт\n3) Удалить контакт\n4) " +
                "Вывести телефонную книгу на экран");
        int inp = inputMenuNumber(4);
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
        }
    }

    private static void printAll() {
        book.getContacts().forEach(System.out::println);
    }

    private static void deleteContact() {
        System.out.println("Введите полное имя человека, контакт которого хотите удалить:");
        String fio = sc.nextLine().toLowerCase();//.trim();
        while (!fio.matches("^\\p{L}+ \\p{L}+ \\p{L}+$")) {
            System.out.println("Неверный ввод, попробуйте еще раз: ");
            fio = sc.nextLine().toLowerCase().trim();
        }
        List<Contact> matches = book.findByFio(fio);
        if (matches.size() == 0) {
            System.out.println("Контакт с таким именем не найден!");
        }
        else if (matches.size() > 1) {
            System.out.println("Найдено несколько совпадений:");
            for (int i = 0; i < matches.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, matches.get(i).toString());
            }
            System.out.print("Введите номер контакта, который хотите удалить: ");
            int toDelete = inputMenuNumber(matches.size());
            book.deleteContact(matches.get(toDelete - 1).getId());
            System.out.println("Контакт удален.");
        }
        else {
            System.out.println(matches.get(0).toString());
            book.deleteContact(matches.get(0).getId());
            System.out.println("Контакт удален.");
        }
    }

    private static void findContactsByName() {
        System.out.println("Введите фрагмент имени человека, контакт которого хотите найти: ");
        String fragment = sc.nextLine().toLowerCase();//.trim();
        book.findMatches(fragment).forEach(System.out::println);
    }

    private static void addContact() {
        book.addContact(createContact());
    }

    private static Contact createContact() {
        System.out.println("Звездочками будут помечены поля, обязательные для ввода. " +
                "Чтобы оставить необязательное поле пустым, нажмите Enter.");
        String[] name = createName();
        List<String> numbers = createNumberList();
        String address = createAddress();
        Calendar date = createDateOfBirth();
        String email = createEmail();
        return new Contact(name[0], name[1], name[2], address, numbers, date, email);
    }

    private static String[] createName() {
        System.out.println("* Введите фамилию, имя и отчество через пробел: ");
        sc.nextLine();
        String name = sc.nextLine();//.toLowerCase();//.trim();
        while (!name.matches("^\\p{L}+ \\p{L}+ \\p{L}+$")) {
            System.out.println("Неверный ввод, попробуйте еще раз: ");
            name = sc.nextLine().toLowerCase();//.trim();
        }
        String[] nameParts = name.split(" ");
//        for (int i = 0; i < 3; i++) {
//            nameParts[i] = nameParts[i].substring(0, 1).toUpperCase() +
//                    nameParts[i].substring(1).toLowerCase();
//        }
//        System.out.printf("Новый контакт: %s %s %s\n", nameParts[0], nameParts[1], nameParts[2]);
        return nameParts;
    }

    private static String createEmail() {
        System.out.println("Введите e-mail:");
        String email = sc.nextLine();//.trim();
        if (email.length() == 0)
            return null;
        while (true) {
            try {
                InternetAddress emailAddr = new InternetAddress(email);
                emailAddr.validate();
            } catch (AddressException ex) {
                System.out.println("Некорректный e-mail, попробуйте еще раз:");
                email = sc.nextLine();//.trim();
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
        String dateStr = sc.nextLine();//.trim();
        if (dateStr.length() == 0)
            return null;
        while (!dateStr.matches(datePattern)) {
            System.out.println("Некорректная дата, попробуйте еще раз: ");
            dateStr = sc.nextLine();//.trim();
        }
        return new GregorianCalendar(Integer.parseInt(dateStr.substring(6)),
                Integer.parseInt(dateStr.substring(3, 5)) - 1,
                Integer.parseInt(dateStr.substring(0, 2)));
    }

    private static List<String> createNumberList() {
        List<String> numbers = new ArrayList<>();
        System.out.println("* Введите номер телефона без дефисов и пробелов, в российском формате: ");
//        sc.nextLine();
        String number = scanNumber();
        int a = 0;
        do {
            while (!number.matches("\\d{10}") || numbers.contains(number) ||
                    !book.validNumber(number)) {
                System.out.println("Номер телефона некорректный или уже записан другому " +
                        "контакту, попробуйте еще раз:");
                number = scanNumber();
            }
            numbers.add(number);
            System.out.println("Если вы хотите добавить еще один номер телефона, введите его, " +
                    "иначе нажмите Enter.");
            number = scanNumber();
        } while (number.length() != 0);
        return numbers;
    }

    private static String createAddress() {
        System.out.println("Введите адрес проживания этого человека (любой формат):");
        String address = sc.nextLine();//.trim();
        if (address.length() == 0)
            return null;
        return address;
    }

    private static String scanNumber() {
        String number = sc.nextLine();//.trim();
        if (number.startsWith("+7"))
            return number.substring(2);
        if (number.startsWith("8"))
            return number.substring(1);
        return number;
    }

    static int inputMenuNumber(int maxNumber) {
        int inp;
        do {
            if (sc.hasNextInt()) {
                inp = sc.nextInt();
                if (inp >= 1 && inp <= maxNumber) {
                    return inp;
                } else
                    System.out.print("Такого пункта нет! ");
            } else {
                sc.next();
                System.out.print("Это не число! ");
            }
            System.out.print("Попробуйте еще раз: ");
        } while (true);
    }
}