import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.List;

public class Contact {
    private static int freeId = 0;

    private final int id;
    private final String name;
    private final String surname;
    private final String patronymic;
    private final String address;
    private final List<String> numbers;
    private final Calendar birthday;
    private final String email;

    public int getId() {
        return id;
    }

    public Contact(String surname, String name, String patronymic, String address,
                   List<String> numbers, Calendar birthday, String email) {
        id = freeId++;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.address = address;
        this.numbers = numbers;
        this.birthday = birthday;
        this.email = email;
    }

    public boolean ownsNumber(String number) {
        return numbers.stream().anyMatch(n -> n.equals(number));
    }

    public boolean matchesFioFragment(String fragment) {
        return (name.contains(fragment) || surname.contains(fragment) || patronymic.contains(fragment));
    }

    public boolean hasFio(String fio) {
        return String.format("%s %s %s", surname, name, patronymic).equals(fio);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("%s %s %s\n%s\n",
                StringUtils.capitalize(surname), StringUtils.capitalize(name),
                StringUtils.capitalize(patronymic), Utils.getNumbersStr(numbers)));
        if (birthday != null) sb.append("Дата рождения: ")
                .append(Utils.getDateStr(birthday)).append("\n");
        if (address != null) sb.append("Адрес: ").append(address).append("\n");
        if (email != null) sb.append("E-mail: ").append(email).append("\n");
        return sb.toString();
    }
}
