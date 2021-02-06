import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PhoneBook {
    String dataPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath()
            + "MyPhoneBook.txt";
    private static List<Contact> contacts;
    private static PhoneBook book;
    private final Gson gson = new Gson();

    public Collection<Contact> getContacts() {
        return Collections.unmodifiableCollection(new ArrayList<>(contacts));
    }

    public PhoneBook() {
        Path path = Paths.get(dataPath);
        File file = new File(dataPath);
        try {
            if (!file.exists() || file.isDirectory()) {
                Files.write(path, Collections.singleton(""), StandardCharsets.UTF_8);
                contacts = new ArrayList<>();
            } else {
                List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                String json = String.join("", lines);
                Type listType = new TypeToken<ArrayList<Contact>>() { }.getType();
                contacts = gson.fromJson(json, listType); // TODO: эксепшоны
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
//        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
//                new FileOutputStream("filename.txt"), StandardCharsets.UTF_8))) {
//            writer.write("");
//        }
//        try {
//            Reader reader = Files.newBufferedReader(path);
//        } catch (FileNotFoundException e) {
////            System.out.println("Файл телефонной книги не был найден в директории по умолчанию. " +
////                    "Она либо была перемещена, либо еще не была создана. Создать новую книгу? (да/нет) ");
//            Files.write(path, Collections.singleton(""), StandardCharsets.UTF_8);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static PhoneBook getBook() {
        if (book == null) {
            book = new PhoneBook();
            contacts = new ArrayList<>();
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
//        Contact toDelete = contacts.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
        contacts.remove(
                contacts.stream().filter(c -> c.getId() == id).findFirst().orElse(null));
    }

    public boolean validNumber(String number) {
        return contacts.stream().noneMatch(c -> c.ownsNumber(number));
    }

}
