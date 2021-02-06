import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PhoneBook {
    private static final String dataPath = FileSystemView.getFileSystemView()
            .getDefaultDirectory().getPath() + File.separator + "MyPhoneBook.txt";

    private static List<Contact> contacts;
    private static PhoneBook book;
    private static final Gson gson = new Gson();

    public Collection<Contact> getContacts() {
        return Collections.unmodifiableCollection(new ArrayList<>(contacts));
    }

    public PhoneBook() {
        File file = new File(dataPath);
        if (!file.exists() || file.isDirectory()) {
            contacts = new ArrayList<>();
        } else {
            // Если книга уже существует, воссоздаем контактики из файла
            fillContacts();
        }
    }

    public static void fillContacts() {
        String json = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(dataPath), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = reader.readLine();
            }
            json = sb.toString();
        } catch (IOException e) {
            System.out.println("Проблемы с файлом телефонной книги!");
            System.exit(1); // я знаю это ужасно но не понимаю как еще,
                                    // не вешать же throws почти на каждый метод...
        }
        Type listType = new TypeToken<ArrayList<Contact>>() {
        }.getType();

        try {
            contacts = gson.fromJson(json, listType);
        } catch (JsonSyntaxException e) {
            System.out.println("Сломался json-файл!");
            System.exit(1); // здесь тоже ужасно
        }

        // Костыль на случай если в файлике было пусто
        if (contacts == null)
            contacts = new ArrayList<>();
    }

    public static PhoneBook getBook() {
        if (book == null) {
            book = new PhoneBook();
        }
        return book;
    }

    public void addContact(Contact cont) {
        contacts.add(cont);
    }

    public List<Contact> findMatches(String fragment) {
        return contacts.stream().filter(c -> c.matchesFioFragment(fragment))
                .collect(Collectors.toList());
    }

    public List<Contact> findByFio(String fio) {
        return contacts.stream().filter(c -> c.hasFio(fio))
                .collect(Collectors.toList());
    }

    public void deleteContact(int id) {
        contacts.remove(
                contacts.stream().filter(c -> c.getId() == id).findFirst().orElse(null));
    }

    public boolean validNumber(String number) {
        return contacts.stream().noneMatch(c -> c.ownsNumber(number));
    }

    public static void saveBook() {
        if (contacts.isEmpty())
            return;
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(dataPath), StandardCharsets.UTF_8))) {
            String json = gson.toJson(contacts);
            writer.write(json);
        } catch (IOException e) {
            System.out.println("Проблемы с файлом телефонной книги!");
            System.exit(1); // и здесь тоже...
        }
    }
}
