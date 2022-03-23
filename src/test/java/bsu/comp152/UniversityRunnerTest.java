package bsu.comp152;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

class UniversityRunnerTest {

    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private University emptyUniversity;
    private University professorUniversity;
    private University fullUniversity;
    private int firstID = 2000;

    // thanks to https://stackoverflow.com/a/50721326
    @BeforeEach
    private void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    private void provideInput(String input) {
        testIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }

    @AfterEach
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    @BeforeEach
    public void setup() {
        emptyUniversity = new University();

        // A university with professors and no students to test admitting a student
        professorUniversity = new University();
        professorUniversity.hireProfessor(new Professor("Laura Gross", "Computer Science"));
        professorUniversity.hireProfessor(new Professor("Indiana Jones", "Archaeology"));
        professorUniversity.hireProfessor(new Professor("Jeff Adler", "Math"));

        // A university with professors and students to test looking up students and modifying them
        fullUniversity = new University();
        fullUniversity.hireProfessor(new Professor("Laura Gross", "Computer Science"));
        fullUniversity.hireProfessor(new Professor("Indiana Jones", "Archaeology"));
        fullUniversity.hireProfessor(new Professor("Jeff Adler", "Math"));
        fullUniversity.admitStudent(new Student("Student Number One", fullUniversity.getFaculty().get(2), firstID));
        fullUniversity.admitStudent(new Student("Student Number Two", fullUniversity.getFaculty().get(1), firstID+1));

    }

    // hireProfessorAddsProfessorToUniversity
    @Test
    public void hireProfessorMenuOptionAddsProfToFacultyList() {
        provideInput("1\nLaura Gross\nComputer Science\n6\n\u001a");
        new UniversityRunner(emptyUniversity).runUniversity();
        assertThat(emptyUniversity.getFaculty().get(0).getName(), equalTo("Laura Gross"));
    }

    // admitStudentAddsStudentToUniversity
    @Test
    public void admitStudentMenuOptionAddsStudentToUniversity() {
        provideInput("2\nA Student\n6\n\u001a");
        new UniversityRunner(professorUniversity).runUniversity();
        // find the student by that ID and make sure the name matches
        assertThat(professorUniversity.findStudent(firstID).get(0).getName(),
                   equalTo("A Student"));
    }

    // looking up a student and adding a class to the transcript updates the GPA
    @Test
    public void lookupStudentAddClassUpdatesGPA() {
        provideInput("4\n2000\n1\n4\n2.1\n6\n\u001a");
        new UniversityRunner(fullUniversity).runUniversity();
        assertThat(fullUniversity.findStudent(2000).get(0).getGPA(),
                   closeTo(2.1, .0001));
    }

    // looking up a student and changing the advisor updates the Student object's advisor
    @Test
    public void lookupStudentChangeAdvisorUpdatesStudentAdvisor() {
        provideInput("4\n2000\n2\nLaura Gross\n6\n\u001a");
        new UniversityRunner(fullUniversity).runUniversity();
        assertThat(fullUniversity.findStudent(2000).get(0).getAdvisor(),
                   equalTo(fullUniversity.getFaculty().get(0)));
    }

    // looking up a student and changing the advisor updates the Professor object's advisee list
    @Test
    public void lookupStudentChangeAdvisorUpdatesProfessorAdvisee() {
        provideInput("4\n2000\n2\nLaura Gross\n6\n\u001a");
        new UniversityRunner(fullUniversity).runUniversity();
        assertThat(fullUniversity.getFaculty().get(0).getAdviseeList().get(0),
                   equalTo(fullUniversity.findStudent(2000).get(0)));
    }

    // an invalid professor name is ignored and does not change the student's advisor
    @Test
    public void lookUpStudentChangeAdvisorInvalidInputDoesntChangeAdvisor() {
        provideInput("4\n2000\n2\nNot a Professor Name\n6\n\u001a");
        Professor initialAdvisor = fullUniversity.findStudent(2000).get(0).getAdvisor();
        new UniversityRunner(fullUniversity).runUniversity();
        assertThat(fullUniversity.findStudent(2000).get(0).getAdvisor(), equalTo(initialAdvisor));
    }

    // an invalid professor name is ignored and does not change the student's advisor's advisee list
    @Test
    public void lookUpStudentChangeAdvisorInvalidInputDoesntChangeAdviseeList() {
        provideInput("4\n2000\n2\nNot a Professor Name\n6\n\u001a");
        // this faculty member is student 2000's initial advisor
        ArrayList<Student> initialAdviseeList = fullUniversity.getFaculty().get(2).getAdviseeList();
        new UniversityRunner(fullUniversity).runUniversity();
        assertThat(fullUniversity.getFaculty().get(2).getAdviseeList(), equalTo(initialAdviseeList));
    }

    @Test
    public void showFacultyMembersPrintsFacultyNames() {
        provideInput("5\n6\n\u001a");
        new UniversityRunner(professorUniversity).runUniversity();
        String output = getOutput();
        assert(output.contains("Laura Gross") &&
                output.contains("Indiana Jones") &&
                output.contains("Jeff Adler"));
    }

    @Test
    public void option6Exits() {
        provideInput("6\n\u001a");
        new UniversityRunner(emptyUniversity).runUniversity();
    }

}