package telran.spring.students;

import java.net.IDN;
import java.time.LocalDate;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static telran.spring.students.TestDbCreation.*;
import telran.spring.students.docs.StudentDoc;
import telran.spring.students.dto.IdName;
import telran.spring.students.dto.IdNameMarks;
import telran.spring.students.dto.Mark;
import telran.spring.students.dto.MarksBucket;
import telran.spring.students.dto.Student;
import telran.spring.students.dto.SubjectMark;
import telran.spring.students.repo.StudentRepository;
import telran.spring.students.service.StudentsService;

@SpringBootTest

class StudentsServiceTests {
	@Autowired
	StudentsService studentsService;
	@Autowired
	TestDbCreation testDbCreation;
	@Autowired
	StudentRepository studentRepo;
	@BeforeEach
	void setUp() {
		testDbCreation.createDb();
	}
	@Test
	void studentSubjectMarksTest() {
		List<Mark> marks = studentsService.getMarksStudentSubject(ID1, SUBJECT1);
		assertEquals(2,marks.size());
		assertEquals(80, marks.get(0).score());
		assertEquals(90, marks.get(1).score());
	}
	@Test
	void studentDatesMarksTest() {
		List<Mark> marks = studentsService.getMarksStudentDates(ID1, DATE2, DATE3);
		assertEquals(2, marks.size());
		assertEquals(70, marks.get(0).score());
		assertEquals(90, marks.get(1).score());
		marks = studentsService.getMarksStudentDates(ID4, DATE2, DATE3);
		assertTrue(marks.isEmpty());
	}
//	@Test
//	void studentsPhonePrefixTest() {
//		List<Student> students = studentsService.getStudentsPhonePrefix("050");
//		
//		assertEquals(3, students.size());
//		StudentDoc student2 = students.get(0);
//		assertNull(student2.getMarks());
//		assertEquals(ID2, student2.getId());
//		students.forEach(s -> assertTrue(s.getPhone().startsWith("050"))); 
//	}
	@Test
	void studentsAllMarksGreaterTest() {
		List<IdName> students = studentsService.getStudentsAllScoresGreater(70);
		assertEquals(2, students.size());
		IdName studentDoc = students.get(0);
		
		assertEquals(ID3, studentDoc.getId());
		assertEquals("name3", studentDoc.getName());
		assertEquals(ID5, students.get(1).getId());
		
	}
	@Test
	void studentsFewMarksTest() {
		List<Long>ids = studentsService.removeStudentsWithFewMarks(2);
		assertEquals(2, ids.size());
		assertEquals(ID4, ids.get(0));
		assertEquals(ID6, ids.get(1));
		assertNull(studentRepo.findById(ID4).orElse(null));
		assertNull(studentRepo.findById(ID6).orElse(null));
	}
	@Test
	void StudentsScoresSubjectGreaterTest() {
		List<IdName> students = studentsService.getStudentsScoresSubjectGreater(70, SUBJECT1);
		assertEquals(3, students.size());
		IdName studentDoc = students.get(0);
		assertEquals(ID1, studentDoc.getId());
		assertEquals("name1", studentDoc.getName());
		assertEquals(ID3, students.get(1).getId());
		
	}
	@Test
	void StudentsNoLowMarksTest() {
		List<Long>ids = studentsService.removeStudentsNoLowMarks(71);
		assertEquals(2, ids.size());
		assertEquals(ID4, ids.get(0));
		assertEquals(ID6, ids.get(1));
		assertNull(studentRepo.findById(ID4).orElse(null));
		assertNull(studentRepo.findById(ID6).orElse(null));
	}
	@Test
	void getAvgMarkTest() {
		assertEquals(testDbCreation.getAvgMark(), studentsService.getStudentsAvgScore(), 0.1);
	}
	
	@Test
	void getStudentsAvgMarkeGreaterTset() {
		List<IdName> idNamesGood = studentsService.getGoodStudents();
		List<IdName> idNamesGreater = studentsService.getStudentsAvgMarkGreater(75);
		assertEquals(3, idNamesGood.size());
		assertEquals(ID3, idNamesGood.get(0).getId());
		idNamesGood.forEach(in -> assertTrue(testDbCreation.getAvgMarksStudent(in.getId()) > 75));
		assertEquals("name3", idNamesGood.get(0).getName());
		assertEquals(ID1, idNamesGood.get(1).getId());
		assertEquals("name1", idNamesGood.get(1).getName());
		assertEquals(ID5, idNamesGood.get(2).getId());
		assertEquals("name5", idNamesGood.get(2).getName());
		assertEquals(idNamesGood.size(), idNamesGreater.size());
	}
	@Test
	void findQueryTest() {
		List<IdNameMarks> actualRes = studentsService.findStudents("{phone:{$regex:/^050/}}");
		List<Student> expectedRes = studentsService.getStudentsPhonePrefix("050");
		assertEquals(expectedRes.size(), actualRes.size());
		IdNameMarks actual1 = actualRes.get(0);
		Student expected1 = expectedRes.get(0);
		assertEquals(expected1.id(), actual1.getId());
	}
	@Test
	void getBestStudentsTest() {
		List<IdNameMarks> bestSudentsList = studentsService.getBestStudents(2);
		assertEquals(2, bestSudentsList.size());
		IdNameMarks bestStudent1 = bestSudentsList.get(0);
		assertEquals(ID3, bestStudent1.getId());
				
	}
	@Test
	void getWorstStudentsTest() {
		List<IdNameMarks> worstSudentsList = studentsService.getworstStudents(2);
		assertEquals(2, worstSudentsList.size());
		IdNameMarks bestStudent1 = worstSudentsList.get(0);
		assertEquals(ID6, bestStudent1.getId());
				
	}
	@Test
	void getBestStudentsSubjectTest() {
		List<IdNameMarks> bestSudentsSubjectList = studentsService.getBestStudentsSubject(2, SUBJECT1);
		assertEquals(2, bestSudentsSubjectList.size());
		IdNameMarks bestStudentSubject1 = bestSudentsSubjectList.get(0);
		assertEquals(ID1, bestStudentSubject1.getId());
	}
	@Test
	void scoresDistributionTest() {
		List<MarksBucket> scoreDistr = studentsService.scoresDistribution(3);
		assertEquals(3, scoreDistr.size());
		assertEquals(70,scoreDistr.get(0).min());
	}
	
}