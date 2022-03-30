package bsu.comp152;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ProfessorTest {
    private Professor testProf;
    private Student mockStudent;
    private Student otherMockStudent;

    private String profName;
    private String deptName;
    private String studentName;

    @BeforeEach
    public void setup() {
        profName = "Professor Name";
        studentName = "Student Name";
        deptName = "Department Name";

        // make a test student and test professor
        testProf = new Professor(profName, deptName);
        mockStudent = mock(Student.class);
        when(mockStudent.getName()).thenReturn("Mock Student One");
        otherMockStudent = mock(Student.class);
        when(otherMockStudent.getName()).thenReturn("Mock Student Two");

    }

    @Test
    public void getNameReturnsProfessorName() {
        assertThat(profName, equalTo(testProf.getName()));
    }

    @Test
    public void profToStringContainsName(){
        assert (testProf.toString().contains(profName));
    }

    @Test
    public void profToStringContainsDept(){
        assert (testProf.toString().contains(deptName));
    }

    @Test
    public void profToStringContainsOneAdviseeName() {
        testProf.addAdvisee(mockStudent);
        assert(testProf.toString().contains(mockStudent.getName()));
    }

    @Test
    public void profToStringContainsTwoAdviseeNames() {
        testProf.addAdvisee(mockStudent);
        testProf.addAdvisee(otherMockStudent);
        assert(testProf.toString().contains(mockStudent.getName()));
        assert(testProf.toString().contains(otherMockStudent.getName()));
    }


    @Test
    public void adviseeListIsInitiallyAnEmptyList() {
        assert (testProf.getAdviseeList().equals(new ArrayList<Student>()));
    }

    @Test
    public void addAdviseeAddsAdviseeToEmptyList() {
        testProf.addAdvisee(mockStudent);
        assertThat(testProf.getAdviseeList(), contains(mockStudent));
    }

    @Test
    public void addAdviseeAddsMultipleAdviseesToList() {
        testProf.addAdvisee(mockStudent);
        testProf.addAdvisee(otherMockStudent);
        assertThat(testProf.getAdviseeList(), containsInAnyOrder(mockStudent, otherMockStudent));
    }

    @Test
    public void removeAdviseeRemovesOneAdvisee() {
        testProf.addAdvisee(mockStudent);
        assertThat(testProf.getAdviseeList(), contains(mockStudent));
        testProf.removeAdvisee(mockStudent);
        assertThat(testProf.getAdviseeList(), not(contains(mockStudent)));
    }

    @Test
    public void removeAdviseeLeavesOtherAdviseeInList() {
        testProf.addAdvisee(mockStudent);
        testProf.addAdvisee(otherMockStudent);
        testProf.removeAdvisee(mockStudent);
        assertThat(testProf.getAdviseeList(), not(contains(mockStudent)));
        assertThat(testProf.getAdviseeList(), contains(otherMockStudent));
    }

}
