import java.io.*;
import java.util.*;
import java.util.Scanner;

public class StudentGradeSystem {
    private static String USERS_FILE = "users.txt";
    private static String GRADES_FILE = "grades.txt";
    private static ArrayList<Student> students = new ArrayList<>();
    private static Teacher teacher;

    public static void main(String args[]) {
        loadUsers();
		loadGrades();
		Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n歡迎使用學生成績系統");
            System.out.println("1. 登入");
            System.out.println("2. 退出");
            System.out.print("請選擇操作：");

            int choice = scanner.nextInt();
            scanner.nextLine(); // 捕捉上面nextInt後的換行符號

            switch (choice) {
                case 1:
                    loginMenu(scanner);
                    break;
				case 2:
					isRunning = false;
					break;
                default:
                    System.out.println("請輸入有效選項！");
            }
        }
        scanner.close();
    }

    private static void loginMenu(Scanner scanner) {
        System.out.println("\n登入");
        System.out.print("請輸入帳號：");
        String username = scanner.nextLine();
        System.out.print("請輸入密碼：");
        String password = scanner.nextLine();

        User currentUser = authenticateUser(username, password);
        if (currentUser != null) {
            if (currentUser instanceof Teacher) {
                teacherMenu(scanner);
            } else if (currentUser instanceof Student) {
                studentMenu((Student) currentUser, scanner);
            }
        } else {
            System.out.println("帳號或密碼錯誤！");
        }
    }

    private static User authenticateUser(String username, String password) {
        for (Student student : students) {
            if (student.getUsername().equals(username) && student.checkPassword(password)) {
                return student;
            }
        }

        if (teacher != null && teacher.getUsername().equals(username) && teacher.checkPassword(password)) {
            return teacher;
        }

        return null;
    }

    private static void teacherMenu(Scanner scanner) {
        System.out.println("\n歡迎老師 " + teacher.getUsername());
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("1. 登記學生成績");
            System.out.println("2. 註冊新學生");
			System.out.println("3. 登出");
            System.out.print("請選擇操作：");

            int choice = scanner.nextInt();
            scanner.nextLine(); // 捕捉上面nextInt後的換行符號

            switch (choice) {
                case 1:
                    recordStudentGrade(scanner);
                    break;
                case 2:
					registerStudent(scanner); // 加這行
					break;
				case 3:
					loggedIn = false;
					break;
                default:
                    System.out.println("請輸入有效選項！");
            }
        }
    }

    private static void recordStudentGrade(Scanner scanner) {
        System.out.println("\n登記學生成績");
        System.out.print("請輸入學生帳號：");
        String studentUsername = scanner.nextLine();

        Student student = findStudent(studentUsername);
        if (student != null) {
            System.out.print("請輸入科目（Chinese、Math、English）：");
            String subject = scanner.nextLine();
            System.out.print("請輸入成績：");
            int grade = scanner.nextInt();
            scanner.nextLine(); // 捕捉上面nextInt後的換行符號

            teacher.recordGrade(student, subject, grade);
            saveGrades(); // 儲存成績
            System.out.println("成績已登記！\n");
        } else {
            System.out.println("找不到該學生！\n");
        }
    }

    private static Student findStudent(String username) {
        for (Student student : students) {
            if (student.getUsername().equals(username)) {
                return student;
            }
        }
        return null;
    }

    private static void studentMenu(Student student, Scanner scanner) {
        System.out.println("\n歡迎同學 " + student.getUsername());
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("1. 查詢成績");
            System.out.println("2. 登出");
            System.out.print("請選擇操作：");

            int choice = scanner.nextInt();
            scanner.nextLine(); // 捕捉上面nextInt後的換行符號

            switch (choice) {
                case 1:
                    displayStudentGrades(student);
                    break;
                case 2:
                    loggedIn = false;
                    break;
                default:
                    System.out.println("請輸入有效選項！");
            }
        }
    }

    private static void displayStudentGrades(Student student) {
        System.out.println("國文成績：" + student.getGrade("Chinese"));
        System.out.println("數學成績：" + student.getGrade("Math"));
        System.out.println("英文成績：" + student.getGrade("English"));
		System.out.println();
    }

    private static void loadUsers() {
        try (Scanner scanner = new Scanner(new File(USERS_FILE))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String userType = parts[0];
                String username = parts[1];
                String password = parts[2];

                if (userType.equals("Teacher")) {
                    teacher = new Teacher(username, password);
                } else if (userType.equals("Student")) {
                    students.add(new Student(username, password));
                    }
			}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void loadGrades() {
        try (Scanner scanner = new Scanner(new File(GRADES_FILE))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String studentName = parts[0];
                String subject = parts[1];
                int grad = Integer.parseInt(parts[2]);
				for (Student student : students) {
					if (student.getUsername().equals(studentName)) {
						student.addGrade(subject, grad);
						break;						
					}
				}
			}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



    private static void saveGrades() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(GRADES_FILE))) {
            for (Student student : students) {
                HashMap<String, Integer> hm=student.grades;
				for (String key : hm.keySet()) 
                  writer.println(student.getUsername() + "," + key + "," + hm.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	private static void registerStudent(Scanner scanner) {
		System.out.println("\n註冊新學生");

		System.out.print("請輸入學生帳號：");
		String username = scanner.nextLine();

		// 檢查是否已存在相同帳號
		if (findStudent(username) != null) {
			System.out.println("該帳號已存在，請使用其他帳號！");
			return;
		}

		System.out.print("請輸入密碼：");
		String password = scanner.nextLine();

		// 建立新學生物件
		Student newStudent = new Student(username, password);
		students.add(newStudent);

		// 儲存到 users.txt
		try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE, true))) {
			writer.print(System.lineSeparator());
			writer.println("Student," + username + "," + password);
			System.out.println("註冊成功！");
		} catch (IOException e) {
			System.out.println("寫入 users.txt 時發生錯誤！");
			e.printStackTrace();
		}
}

	
}     


