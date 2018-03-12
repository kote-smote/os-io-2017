import java.io.*;
import java.util.*;

/**
 * Created by martin on 3/2/17.
 */
public class HW01_5 {

    public static void main(String[] args) throws IOException {
        String path = "/home/martin/Dropbox/Projects/IntelliJ Projects/Operating_Systems_Finki_2017/data/rezultati.csv";
        proccessFileData(path);
        convertToTsvFile(path);
    }

    public static void proccessFileData(String path) throws IOException {
        Map<String, List<Integer>> courseAverages = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String[] courses = reader.readLine().split(",");
        String line = null;
        List<Integer> grades = null;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            double average = 0;
            for (int i = 1; i < parts.length; i++) {
                int grade = Integer.parseInt(parts[i]);
                average += grade;
                String course = courses[i];

                if (!courseAverages.containsKey(course)) {
                    grades = new ArrayList<>();
                    courseAverages.put(courses[i], grades);
                }
                grades = courseAverages.get(courses[i]);
                grades.add(grade);
            }
            average /= courses.length - 1;
            System.out.format("%s %.2f\n", parts[0], average);
        }
        for (Map.Entry<String, List<Integer>> entry : courseAverages.entrySet()) {
            String grade = entry.getKey();
            double avg = entry.getValue().stream().mapToInt(Integer::intValue).average().getAsDouble();
            System.out.format("%s %.2f\n", grade, avg);
        }
    }

    public static void convertToTsvFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        BufferedWriter writer = new BufferedWriter(new FileWriter("rezultati.tsv"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            line = line.replaceAll(",", "\t");
            writer.write(line+"\n");
        }
        reader.close();
        writer.flush();
        writer.close();
    }
}
