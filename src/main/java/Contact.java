import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

public class Contact {
    private static final Gson gson = new Gson();

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getAddress() {
        return address;
    }

    public List<String> getNumbers() {
        return numbers;
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

    public Calendar getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    private static int freeId = 0;

    private final int id;
    private final String name;
    private final String surname;
    private final String patronymic;
    private final String address;
    private final List<String> numbers;
    private final Calendar birthday;
    private final String email;

    public boolean ownsNumber(String number) {
        return numbers.stream().anyMatch(n -> n.equals(number));
    }

    public boolean matchesFioFragment(String fragment) {
        return (name.contains(fragment) || surname.contains(fragment) || patronymic.contains(fragment));
    }

    public boolean hasFio(String fio) {
        return String.format("%s %s %s", surname, name, patronymic).equals(fio);
    }

    public String getBirthdayStr() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(birthday.getTime());
    }

    public String getNumbersStr() {
        return numbers.stream().map(n -> "8" + n).reduce((n1, n2) -> n1 + ", " + n2).orElse(null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("%s %s %s\n%s\n",
                StringUtils.capitalize(surname), StringUtils.capitalize(name),
                StringUtils.capitalize(patronymic), getNumbersStr()));
        if (birthday != null) sb.append("Дата рождения: ").append(getBirthdayStr()).append("\n");
        if (address != null) sb.append("Адрес: ").append(address).append("\n");
        if (email != null) sb.append("E-mail: ").append(email).append("\n");
        return sb.toString();
//        return String.format("%s %s %s\n%s\nДата рождения: %s\nАдрес: %s\nE-mail: %s\n",
//                StringUtils.capitalize(surname), StringUtils.capitalize(name),
//                StringUtils.capitalize(patronymic), numbers.toString(), birthday, address, email);
    }

    public String serialize() {
        return gson.toJson(this);
    }

    ////// кышь отсюда
    public Contact deserialize(String json) {
        return gson.fromJson(json, Contact.class);
    }
}
