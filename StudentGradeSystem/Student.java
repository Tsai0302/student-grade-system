import java.util.*;

public class Student extends User {
    protected HashMap<String, Integer> grades;

    public Student(String username, String password) {
        super(username, password);
        this.grades = new HashMap<>();
    }

    public void addGrade(String subject, int grade) {
        grades.put(subject, grade);
    }

    public Integer getGrade(String subject) {
        return grades.get(subject);
    }
}
