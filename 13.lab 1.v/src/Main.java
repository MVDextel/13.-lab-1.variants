//241RDB023 Maksims Visockis 10.grupa

import java.io.*;
import java.util.*;

public class Main {
    static final Scanner sc = new Scanner(System.in);
    static final String FILE_NAME = "db.csv";

    public static void main(String[] args) {
        while (true) {
            System.out.println("add");
            System.out.println("del");
            System.out.println("edit");
            System.out.println("print");
            System.out.println("sort");
            System.out.println("find");
            System.out.println("avg");
            System.out.println("exit");
            System.out.println("Write command:");
            String input = sc.nextLine().trim();

            String[] commandParts = input.split("\\s+", 2);
            String command = commandParts[0].toLowerCase();

            switch (command) {
                case "add":
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
                    System.out.println("Good Bye!");
                    return;
                default:
                    System.out.println("wrong command");
            }
        }
    }

    public static void print() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-4s%-21s%-11s%6s%10s%8s\n",
                    "ID", "City", "Date", "Days", "Price", "Vehicle");
            System.out.println("------------------------------------------------------------");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(";");
                System.out.printf("%-4s%-21s%-11s%6s%10s%8s\n",
                        fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]);
            }

            System.out.println("------------------------------------------------------------");
        } catch (Exception ex) {
            System.out.println("Error in print(): " + ex.getMessage());
        }
    }



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

        if (idExists(fields[0])) {
            System.out.println("wrong id");
            return;
        }

        if (!fields[2].matches("\\d{2}/\\d{2}/\\d{4}")) {
            System.out.println("wrong date");
            return;
        }
        if (!isValidDate(fields[2])) {
            System.out.println("wrong date");
            return;
        }

        try {
            Integer.parseInt(fields[3]);
        } catch (Exception ex) {
            System.out.println("wrong day count");
            return;
        }

        if (!fields[4].matches("\\d+(\\.\\d{1,2})?")) {
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
        } catch (Exception ex) {
            System.out.println("Error in add(): " + ex.getMessage());
        }
    }

    public static void del(String parameter) {
        if (!parameter.matches("\\d{3}")) {
            System.out.println("wrong id");
            return;
        }
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(parameter + ";")) {
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error " + ex.getMessage());
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
        } catch (Exception ex) {
            System.out.println("Error in del(): " + ex.getMessage());
        }
    }

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
        } catch (Exception ex) {
            System.out.println("Error " + ex.getMessage());
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
        } catch (Exception ex) {
            System.out.println("Error " + ex.getMessage());
        }
    }

    public static void sort() {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception ex) {
            System.out.println("Error " + ex.getMessage());
            return;
        }

        lines.sort(Comparator.comparing(o -> o.split(";")[2]));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("sorted");
        } catch (Exception ex) {
            System.out.println("Error " + ex.getMessage());
        }
    }

    public static void find(String params) {
        try {
            double maxPrice = Double.parseDouble(params);
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                System.out.println("------------------------------------------------------------");
                System.out.printf("%-4s%-21s%-11s%6s%10s %-8s\n",
                        "ID", "City", "Date", "Days", "Price", "Vehicle");
                System.out.println("------------------------------------------------------------");

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split(";");

                    if (Double.parseDouble(fields[4]) <= maxPrice) {
                        System.out.printf("%-4s%-21s%-11s%6s%10s %-8s\n",
                                fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]);
                    }
                }

                System.out.println("------------------------------------------------------------");
            }
        } catch (Exception ex) {
            System.out.println("wrong price");
        }
    }

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
        } catch (Exception ex) {
            System.out.println("Error " + ex.getMessage());
            return;
        }
        if (count == 0) {
            System.out.println("average=0.00");
        } else {
            System.out.printf("average=%.2f\n", sum / count);
        }
    }

    public static boolean idExists(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(id + ";")) {
                    return true;
                }
            }
        } catch (Exception ex) {
            System.out.println("Error " + ex.getMessage());
        }
        return false;
    }

    public static boolean isValidDate(String date) {
        try {
            String[] parts = date.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            if (month < 1 || month > 12) {
                return false;
            }

            int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

            if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
                daysInMonth[1] = 29;
            }

            return day >= 1 && day <= daysInMonth[month - 1];
        } catch (Exception ex) {
            return false;
        }
    }
    public static String formatCity(String city) {
        String[] words = city.toLowerCase().split("\\s+");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
        }
        return String.join(" ", words);
    }
}
