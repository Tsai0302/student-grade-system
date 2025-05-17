public class Teacher extends User {
    public Teacher(String username, String password) {
        super(username, password);
    }

    public void recordGrade(Student student, String subject, int grade) {
        student.addGrade(subject, grade);
    }
}