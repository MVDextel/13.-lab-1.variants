//241RDB023 Maksims Visockis 10.grupa

import java.io.*;
import java.util.*;

public class Main {
    static final Scanner sc = new Scanner(System.in);
    static final String FILE_NAME = "db.csv";

    public static void main(String[] args) {
        while (true) {
            System.out.println("Введите команду:");
            String input = sc.nextLine().trim();

            // Разделяем команду и параметры
            String[] commandParts = input.split("\\s+", 2);
            String command = commandParts[0].toLowerCase();

            switch (command) {
                case "add":
                    // Если есть параметры, передаём их, иначе пустую строку
                    add(commandParts.length > 1 ? commandParts[1] : "");
                    break;
                case "del":
                    del(commandParts.length > 1 ? commandParts[1] : "");
                    break;
                case "edit":
                    edit(commandParts.length > 1 ? commandParts[1] : "");
                    break;
                case "print":
                    print();
                    break;
                case "sort":
                    sort();
                    break;
                case "find":
                    find(commandParts.length > 1 ? commandParts[1] : "");
                    break;
                case "avg":
                    avg();
                    break;
                case "exit":
                    System.out.println("Программа завершена.");
                    return;
                default:
                    System.out.println("wrong command");
            }
        }
    }

    // Вывод содержимого файла в табличном виде
    public static void print() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-4s %-20s %-10s %6s %10s %-8s\n",
                    "ID", "City", "Date", "Days", "Price", "Vehicle");
            System.out.println("------------------------------------------------------------");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(";");
                System.out.printf("%-4s %-20s %-10s %6s %10s %-8s\n",
                        fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]);
            }
            System.out.println("------------------------------------------------------------");
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
    }

    // Добавление записи в файл
    public static void add(String params) {
        String[] fields = params.split(";");
        if (fields.length != 6) {
            System.out.println("wrong field count");
            return;
        }
        if (!fields[0].matches("\\d{3}")) {
            System.out.println("wrong id");
            return;
        }
        if (!fields[2].matches("\\d{2}/\\d{2}/\\d{4}")) {
            System.out.println("wrong date");
            return;
        }
        try {
            Integer.parseInt(fields[3]); // Проверка числа дней
        } catch (NumberFormatException e) {
            System.out.println("wrong day count");
            return;
        }
        try {
            Double.parseDouble(fields[4]); // Проверка цены
        } catch (NumberFormatException e) {
            System.out.println("wrong price");
            return;
        }
        String vehicle = fields[5].toUpperCase();
        if (!List.of("PLANE", "BUS", "TRAIN", "BOAT").contains(vehicle)) {
            System.out.println("wrong vehicle");
            return;
        }
        fields[1] = formatCity(fields[1]);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(String.join(";", fields));
            writer.newLine();
            System.out.println("added");
        } catch (IOException e) {
            System.out.println("Ошибка записи в файл: " + e.getMessage());
        }
    }

    // Удаление записи по ID
    public static void del(String params) {
        if (!params.matches("\\d{3}")) {
            System.out.println("wrong id");
            return;
        }
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(params + ";")) {
                    found = true; // Найдено совпадение
                } else {
                    lines.add(line); // Добавляем строку обратно в список
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
            return;
        }

        if (!found) {
            System.out.println("wrong id");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("deleted");
        } catch (IOException e) {
            System.out.println("Ошибка записи в файл: " + e.getMessage());
        }
    }

    // Редактирование записи
    public static void edit(String params) {
        String[] fields = params.split(";");
        if (fields.length < 6) {
            System.out.println("wrong field count");
            return;
        }
        if (!fields[0].matches("\\d{3}")) {
            System.out.println("wrong id");
            return;
        }
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(fields[0] + ";")) {
                    found = true;
                    String[] originalFields = line.split(";");
                    for (int i = 1; i < fields.length; i++) {
                        if (!fields[i].isEmpty()) {
                            originalFields[i] = fields[i];
                        }
                    }
                    lines.add(String.join(";", originalFields));
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
            return;
        }

        if (!found) {
            System.out.println("wrong id");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("changed");
        } catch (IOException e) {
            System.out.println("Ошибка записи в файл: " + e.getMessage());
        }
    }

    // Сортировка записей по дате
    public static void sort() {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
            return;
        }

        lines.sort(Comparator.comparing(o -> o.split(";")[2])); // Сортировка по дате
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("sorted");
        } catch (IOException e) {
            System.out.println("Ошибка записи в файл: " + e.getMessage());
        }
    }

    // Поиск записей с ценой <= заданной
    public static void find(String params) {
        try {
            double maxPrice = Double.parseDouble(params); // Парсим введённую цену
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                System.out.println("------------------------------------------------------------");
                System.out.printf("%-4s %-20s %-10s %6s %10s %-8s\n",
                        "ID", "City", "Date", "Days", "Price", "Vehicle");
                System.out.println("------------------------------------------------------------");
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split(";");
                    if (Double.parseDouble(fields[4]) <= maxPrice) {
                        System.out.printf("%-4s %-20s %-10s %6s %10s %-8s\n",
                                fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]);
                    }
                }
                System.out.println("------------------------------------------------------------");
            }
        } catch (NumberFormatException e) {
            System.out.println("wrong price");
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
    }

    // Расчёт средней цены
    public static void avg() {
        double sum = 0;
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(";");
                sum += Double.parseDouble(fields[4]);
                count++;
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
            return;
        }
        if (count == 0) {
            System.out.println("average=0.00");
        } else {
            System.out.printf("average=%.2f\n", sum / count);
        }
    }

    // Форматирование города: первые буквы заглавные
    public static String formatCity(String city) {
        String[] words = city.toLowerCase().split("\\s+");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
        }
        return String.join(" ", words);
    }
}
