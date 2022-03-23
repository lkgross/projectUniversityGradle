package bsu.comp152;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static org.mockito.Mockito.*;

public class UniversityTest {
    private University testUniversity;
    private Student mockStudent;
    private Student otherMockStudent;
    private Professor mockProfessor;
    private String studentName;
    private String profName;
    private String deptName;


    @BeforeEach
    public void setup() {
        this.studentName = "Student One";
        this.profName = "Professor Name";
        this.deptName = "Department Name";
        testUniversity = new University();

        mockProfessor = mock(Professor.class);
        mockStudent = mock(Student.class);
        when(mockStudent.getStudentID()).thenReturn(2000);
        when(mockStudent.getAdvisor()).thenReturn(mockProfessor);
        otherMockStudent = mock(Student.class);
        when(otherMockStudent.getStudentID()).thenReturn(2001);
        when(otherMockStudent.getAdvisor()).thenReturn(mockProfessor);
    }

    @Test
    public void findStudentReturnsCorrectStudentWhenOneStudentAdded() {
        testUniversity.admitStudent(mockStudent);
        ArrayList<Student> foundStudent = testUniversity.findStudent(mockStudent.getStudentID());
        assertThat(foundStudent, contains(mockStudent));
    }

    @Test
    public void findStudentReturnsOnlyCorrectStudentWhenMultipleStudentsWithSameNameAdded() {
        when(mockStudent.getName()).thenReturn(studentName);
        when(otherMockStudent.getName()).thenReturn(studentName);
        testUniversity.admitStudent(mockStudent);
        testUniversity.admitStudent(otherMockStudent);
        ArrayList<Student> foundStudent = testUniversity.findStudent(otherMockStudent.getStudentID());
        assertThat(foundStudent, contains(otherMockStudent));
        assertThat(foundStudent, not(contains(mockStudent)));
    }

    @Test
    public void findStudentOnlyReturnsOneStudent() {
        testUniversity.admitStudent(mockStudent);
        testUniversity.admitStudent(otherMockStudent);
        ArrayList<Student> foundStudent = testUniversity.findStudent(otherMockStudent.getStudentID());
        assertThat(foundStudent.size(), equalTo(1));
    }

    @Test
    public void graduateStudentsRemovesEligibleStudentFromStudentList() {
        testUniversity.admitStudent(mockStudent);
        when(mockStudent.getGPA()).thenReturn(3.0);
        when(mockStudent.getCreditHours()).thenReturn(25);
        testUniversity.graduateStudents();
        assertThat(new ArrayList<>(), equalTo(testUniversity.findStudent(mockStudent.getStudentID())));
    }

    @Test
    public void graduateStudentsRemovesStudentFromAdvisorsList() {
        // set up a professor to be an advisor
        // this could be a mock, but that added a lot of complexity
        Professor testProfessor = new Professor("Test Professor", "Department of Quality Assurance");

        // set up a mock student with that advisor and a graduatable transcript
        when(mockStudent.getAdvisor()).thenReturn(testProfessor);
        when(mockStudent.getCreditHours()).thenReturn(30);
        when(mockStudent.getGPA()).thenReturn(4.0);

        testUniversity.hireProfessor(testProfessor);
        testUniversity.admitStudent(mockStudent);
        testProfessor.addAdvisee(mockStudent);
        // make sure the student is actually in the advisee list first
        assertThat(testProfessor.getAdviseeList(), contains(mockStudent));
        testUniversity.graduateStudents();
        // then that it gets removed upon graduation
        assertThat(testProfessor.getAdviseeList(), not(contains(mockStudent)));
    }

    @Test
    public void graduateStudentDoesntGraduateStudentIfGPAIs1() {
        testUniversity.admitStudent(mockStudent);
        when(mockStudent.getGPA()).thenReturn(1.0);
        ArrayList<Student> beforeGrad = testUniversity.findStudent(mockStudent.getStudentID());
        testUniversity.graduateStudents();
        assertThat(beforeGrad, equalTo(testUniversity.findStudent(mockStudent.getStudentID())));
    }

    @Test
    public void graduateStudentsDoesntGraduateStudentIf19Credits() {
        testUniversity.admitStudent(mockStudent);
        when(mockStudent.getCreditHours()).thenReturn(19);
        ArrayList<Student> beforeGrad = testUniversity.findStudent(mockStudent.getStudentID());
        testUniversity.graduateStudents();
        assertThat(beforeGrad, equalTo(testUniversity.findStudent(mockStudent.getStudentID())));
    }

    @Test
    public void graduateWithNoStudentsReturnsEmptyList() {
        List<Student> grads = testUniversity.graduateStudents();
        assert(grads.isEmpty());
    }

    @Test
    public void graduateTwoStudentsReturnsListOfThoseStudents() {
        when(mockStudent.getCreditHours()).thenReturn(30);
        when(mockStudent.getGPA()).thenReturn(4.0);
        when(otherMockStudent.getCreditHours()).thenReturn(30);
        when(otherMockStudent.getGPA()).thenReturn(4.0);
        testUniversity.admitStudent(mockStudent);
        testUniversity.admitStudent(otherMockStudent);
        List<Student> grads = testUniversity.graduateStudents();
        assertThat(grads, containsInAnyOrder(mockStudent, otherMockStudent));
    }

    @Test
    public void admitStudentIncrementsNextStudentID() {
        University u = new University();
        int firstID = u.getNextStudentID();
        u.admitStudent(mockStudent);
        assertThat(firstID + 1, equalTo(u.getNextStudentID()));
    }


}
