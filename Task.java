import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Task {

  static void people_writ(String text, ArrayList<Person> people) throws Exception {
    Scanner input = new Scanner(System.in);
    System.out.printf("Записать список людей в файл? (0-да, 1-нет):");
    int a = input.nextInt();
    if (a == 0) {
      FileWriter pw = new FileWriter(text, false);
      for (Person human : people) {
        pw.write(human.getFullName() + "\n");
        System.out.println(human);
      }
      pw.flush();
      pw.close();
    }
    // input.close();
  }

  static void people_read(String text, ArrayList<Person> people) throws Exception {
    Scanner input = new Scanner(System.in);
    System.out.printf("Загрузить список людей из файла? (0-да, 1-нет):");
    int a = input.nextInt();
    if (a == 0) {
      FileReader pr = new FileReader(text);
      Scanner scan = new Scanner(pr);
      while (scan.hasNextLine()) {
        String[] fullName = scan.nextLine().split(" ");
        int id = Integer.parseInt(fullName[0]);
        String first_Name = fullName[1];
        String Last_Name = fullName[2];
        int year_of_birth = Integer.parseInt(fullName[3]);
        people.add(new Person(id, first_Name, Last_Name, year_of_birth));
      }
      scan.close();
      pr.close();
    } // input.close();
  }

  static String[] input_fullName() {
    while (true) {
      Scanner input = new Scanner(System.in);
      System.out.printf("Введите имя, фамилию и год рождения через пробелы: ");
      String[] str = input.nextLine().split(" ");

      if (Integer.parseInt(str[2]) > 2022) {
        System.out.printf("Год рождения %s введен не коректно (человек еще не родился)\n", str[2]);
        continue;
      }
      // input.close();
      return str;
    }
  }

  static void print_people(ArrayList<Person> people) {
    Scanner input = new Scanner(System.in);
    System.out.printf("Вывести на экран список людей? (0-да, 1-нет):");
    int a = input.nextInt();
    if (a == 0) {
      for (Person human : people) {
        System.out.println(human);
      }
    } // input.close();
  }

  public static void main(String[] args) throws Exception {
    ArrayList<Person> people = new ArrayList<>();
    people_read("people.txt", people);

    Scanner input = new Scanner(System.in);
    System.out.printf("Введите количество дополнительныйх записей (можно 0):");
    int amount = input.nextInt();
    // input.close();
    for (int i = 0; i < amount; i++) {
      String[] fullName = input_fullName();
      String first_Name = fullName[0];
      String Last_Name = fullName[1];
      int year_of_birth = Integer.parseInt(fullName[2]);
      people.add(new Person(i, first_Name, Last_Name, year_of_birth));
    }

    print_people(people);

    people_writ("people.txt", people);

    GeoTree gt = new GeoTree();
    System.out.println("\nРаспределите детей:");
    for (Person person : people) {
      System.out.printf("У %s есть дети?\n", person.getFullName());
      System.out.println("Введите id детей через пробел или Enter, если детей нет");
      print_people(people);
      Scanner child = new Scanner(System.in);
      String[] id_child = child.nextLine().split(" ");
      if (id_child[0] == "") {
        continue;
      } else {
        for (int i = 0; i < id_child.length; i++) {
          int id_chil = Integer.parseInt(id_child[i]);
          int result = new Research(gt).search(people.get(id_chil));
          if (result == 1) {
            System.out.println(people.get(id_chil) + " - Уже чей-то ребенок");
            continue;
          }
          if (person.getYear_of_birth() < people.get(id_chil).getYear_of_birth() - 15) {
            gt.append(person, people.get(id_chil));
          } else {
            System.out.println("Родитель должен быть старше ребенка минимум на 15 лет");
          }
        }
        continue;
      }
    }

    System.out.println("Поиск детей");
    System.out.println("Введите id родителя для поиска детей:");
    print_people(people);

    Scanner parent = new Scanner(System.in);
    int id_parent = parent.nextInt();
    System.out.printf("У %s дети:\n", people.get(id_parent).get_f_l_Name());
    ArrayList<Person> result = new ArrayList<>();
    result = new Research(gt).spend(people.get(id_parent), Relationship.parent);
    for (Person person : result) {
      System.out.println(person);
    }

    // new Research(gt).view_tree();

  }
}

enum Relationship {
  parent,
  children
}

class Person {
  private Integer id;
  private String first_Name;
  private String last_Name;
  private Integer year_of_birth;

  public String getFullName() {
    return id + " " + first_Name + " " + last_Name + " " + year_of_birth + " года рождения";
  }

  public String get_f_l_Name() {
    return first_Name + " " + last_Name;
  }

  public Integer getIdName() {
    return id;
  }

  public Integer getYear_of_birth() {
    return year_of_birth;
  }

  public Person(int id, String first_Name, String last_Name, int year_of_birth) {
    this.id = id;
    this.first_Name = first_Name;
    this.last_Name = last_Name;
    this.year_of_birth = year_of_birth;
  }

  @Override
  public String toString() {
    return String.format("%d %s %s %d года рождения", this.id, this.first_Name, this.last_Name, this.year_of_birth);
  }
}

class Node {
  public Node(Person p1, Relationship re, Person p2) {

    this.p1 = p1;
    this.re = re;
    this.p2 = p2;
  }

  Person p1;
  Relationship re;
  Person p2;

  @Override
  public String toString() {
    return String.format("<%s %s %s>", p1, re, p2);
  }
}

class GeoTree {
  public ArrayList<Node> tree = new ArrayList<>();

  public ArrayList<Node> getTree() {
    return tree;
  }

  public void append(Person parent, Person children) {
    tree.add(new Node(parent, Relationship.parent, children));
    tree.add(new Node(children, Relationship.children, parent));
  }
}

class Research {
  ArrayList<Node> tree;

  public Research(GeoTree geoTree) {
    tree = geoTree.getTree();
  }

  public void view_tree() {
    for (Node t : tree) {
      System.out.println((t.toString()));
    }
  }

  public int search(Person p) {
    for (Node t : tree) {
      if (t.p1.getIdName() == p.getIdName() && t.re == Relationship.children) {
        return 1;
      }
    }
    return 0;
  }

  public ArrayList<Person> spend(Person p, Relationship re) {
    ArrayList<Person> result = new ArrayList<>();
    for (Node t : tree) {
      if (t.p1.getIdName() == p.getIdName() && t.re == re) {
        result.add(t.p2);
      }
    }
    return result;
  }
}

class Reserch2 {

}
