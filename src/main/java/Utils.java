import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class Utils {
    private static final Scanner sc = new Scanner(System.in);

    static String scanNumber() {
        String number = sc.nextLine().trim();
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

    static String getDateStr(Calendar date) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(date.getTime());
    }

    static String getNumbersStr(List<String> numbers) {
        return numbers.stream().map(n -> "8" + n).reduce((n1, n2) -> n1 + ", " + n2).orElse(null);
    }
}
