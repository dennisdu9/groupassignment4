/**
 * File:  TestACMECollegeSystem.java
 * Course materials (23W) CST 8277
 * Teddy Yap
 * (Original Author) Mike Norman
 *
 *
 **/

package acmecollege;

import static acmecollege.utility.MyConstants.APPLICATION_API_VERSION;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.equalTo;
import acmecollege.entity.CourseRegistration;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.Professor;
import acmecollege.entity.Student;

@SuppressWarnings("unused")

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestACMECollegeSystem {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // Test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);
    }

    @Test
    public void test01_all_students_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Student> students = response.readEntity(new GenericType<List<Student>>(){});
        assertThat(students, is(not(empty())));
    
    }
    
    @Test
    public void test02_add_course_adminrole() throws JsonMappingException, JsonProcessingException{
    	Course course = new Course("CST8334", "Software", 2022, "FALL",2, (byte)1);
    	Response response = webTarget.register(adminAuth)
    						.path(COURSE_RESOURCE_NAME)
    						.request().post(Entity.entity(course, MediaType.APPLICATION_JSON_TYPE));
    	
    	assertThat(response.getStatus(), is(200));
    	Course returnedCourse = response.readEntity(new GenericType<Course>() {});
    	assertThat(returnedCourse.getCourseCode(), is(equalToIgnoringCase("CST8334")));
    	
    	
    }
    
    @Test
    public void test03_add_course_userrole_unauthorized() throws JsonMappingException, JsonProcessingException{
    	Course course = new Course("CST8336", "Java", 2022, "FALL",2, (byte)1);
    	Response response = webTarget.register(userAuth)
    						.path(COURSE_RESOURCE_NAME)
    						.request().post(Entity.entity(course, MediaType.APPLICATION_JSON_TYPE));
    	
    	assertThat(response.getStatus(), is(403));
    	
    }
    
    @Test
    public void test04_get_all_course_adminrole() throws JsonMappingException, JsonProcessingException{
    	Response response = webTarget.register(adminAuth)
    						.path(COURSE_RESOURCE_NAME)
    						.request()
    						.get();
    	assertThat(response.getStatus(), is(200));
    	List<Course> courseList = response.readEntity(new GenericType<List<Course>>() {});
    	assertThat(courseList, is(not(empty())));

    }
    
    @Test
    public void test05_get_all_course_userrole() throws JsonMappingException, JsonProcessingException{
    	Response response = webTarget.register(userAuth)
    						.path(COURSE_RESOURCE_NAME)
    						.request()
    						.get();
    	assertThat(response.getStatus(), is(200));
    	List<Course> courseList = response.readEntity(new GenericType<List<Course>>() {});
    	assertThat(courseList, is(not(empty())));
    	
    }
    
    @Test
    public void test06_get_courseById_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(COURSE_RESOURCE_NAME+"/{id}").resolveTemplate(RESOURCE_PATH_ID_ELEMENT, 1)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Course returnedCourse = response.readEntity(new GenericType<Course>(){});
        assertThat(returnedCourse.getCourseTitle(), is(equalToIgnoringCase("Enterprise Application Programming")));
    }
    
    @Test
    public void test07_get_courseById_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME+"/{id}").resolveTemplate(RESOURCE_PATH_ID_ELEMENT, 1)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Course returnedCourse = response.readEntity(new GenericType<Course>(){});
        assertThat(returnedCourse.getCourseTitle(), is(equalToIgnoringCase("Enterprise Application Programming")));
    }
    
    @Test
    public void test08_delete_courseById_userrole_forbidden() throws JsonMappingException, JsonProcessingException{
        Response response = webTarget
                .register(userAuth)
                .path(COURSE_RESOURCE_NAME+"/{id}").resolveTemplate(RESOURCE_PATH_ID_ELEMENT, 1)
                .request()
                .delete();
            assertThat(response.getStatus(), is(403));
            
        
    }
    
    @Test
    public void test09_delete_courseById_adminrole() throws JsonMappingException, JsonProcessingException{
        Response response = webTarget
                .register(adminAuth)
                .path(COURSE_RESOURCE_NAME+"/{id}").resolveTemplate(RESOURCE_PATH_ID_ELEMENT, 1)
                .request()
                .delete();
            assertThat(response.getStatus(), is(200));
            
        
    }
    
    @Test
    public void test10_all_professors_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path("professor")
           // .request(APPLICATION_JSON)
            .request() 
            .get();
        assertThat(response.getStatus(), is(200));
        List<Professor> professors = response.readEntity(new GenericType<List<Professor>>(){});
        assertThat(professors, is(not(empty())));
        assertThat(professors, hasSize(1));
    }
    
    @Test
    public void test11_professorById_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path("professor" + "/" + 1)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Professor professor = response.readEntity(new GenericType<Professor>(){});
        assertThat(professor.getId(), equalTo(1));
    }
    
    @Test
    public void test12_professorById_with_userRole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path("professor" + "/" + 1)
            .request()
            .get();
        
        assertThat(response.getStatus(), is(200));
        Professor professor = response.readEntity(new GenericType<Professor>(){});
        assertThat(professor.getId(), equalTo(1));
    }
    
    @Test
    public void test13_addProfessor_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Set<CourseRegistration> courseRegistrations = new HashSet<>();
    	 Professor professor = new Professor("Minda", "Tilda", "IT",courseRegistrations);
    	
    		Entity<Professor> add = Entity.json(professor);
    	
    		Response response = webTarget
             .register(adminAuth)
             .path("professor")
             .request()
             .header("Content-Type", "application/json")
             .post(add);
            // .post(Entity.entity(professor, MediaType.APPLICATION_JSON_TYPE));
         assertThat(response.getStatus(), is(200));
         
         Professor pro = response.readEntity(new GenericType<Professor>(){});
         
         assertThat(pro.getFirstName(), equalTo("Minda"));
         assertThat(pro.getLastName(), equalTo("Tilda"));
         assertThat(pro.getDepartment(), equalTo("IT"));
     }
    
    
    @Test
    public void test14_addInvalidProfessor_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Professor pro = new Professor();
    		
 	   Entity<Professor> entity = Entity.json(pro);
    	
    		Response response = webTarget
             .register(adminAuth)
             .path("professor")
             .request()
             .post(entity);
    		
         assertThat(response.getStatus(), is(500));
     }
    
    @Test
    public void test15_all_students_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            //.register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403));
        
    }

    @Test
     public void test16_studentById_with_userRole() throws JsonMappingException, JsonProcessingException {
         Response response = webTarget
             .register(userAuth)
             .path(STUDENT_RESOURCE_NAME+ "/" + 1)
             .request()
             .get();
         
         assertThat(response.getStatus(), is(200));
         
     }
    
    @Test
    public void test17_studentById_with_adminRole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME+ "/" + 1)
            .request()
            .get();
        
        assertThat(response.getStatus(), is(200));
        
    }
    
    @Test
    public void test18_studentById_with_adminRole_notfound() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME+ "/" + 10)
            .request()
            .get();
        
        assertThat(response.getStatus(), is(404));
        
    }
    
    @Test
    public void test19_studentById_with_userRole_notfound() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME+ "/" + 10)
            .request()
            .get();
        
        assertThat(response.getStatus(), is(403));
        
    }
    
    @Test
    public void test20_addStudent_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Set<CourseRegistration> courseRegistrations = new HashSet<>();
    	Student student = new Student();
    	student.setFirstName("Radha");
    	student.setLastName("Dey");
    	student.setMembershipCards(null);
    	student.setCourseRegistrations(courseRegistrations);
    	
    		Entity<Student> add = Entity.json(student);
    	
    		Response response = webTarget
             .register(adminAuth)
             .path(STUDENT_RESOURCE_NAME)
             .request()
             .header("Content-Type", "application/json")
             .post(add);
            // .post(Entity.entity(professor, MediaType.APPLICATION_JSON_TYPE));
         assertThat(response.getStatus(), is(200));
         
         Student stu = response.readEntity(new GenericType<Student>(){});
         
         assertThat(stu.getFirstName(), equalTo("Radha"));
         assertThat(stu.getLastName(), equalTo("Dey"));
        
     }
    
    @Test
    public void test21_addStudent_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Set<CourseRegistration> courseRegistrations = new HashSet<>();
    	Student student = new Student();
    	student.setFirstName("test");
    	student.setLastName("test");
    	student.setMembershipCards(null);
    	student.setCourseRegistrations(courseRegistrations);
    	
    		Entity<Student> add = Entity.json(student);
    	
    		Response response = webTarget
             .register(userAuth)
             .path(STUDENT_RESOURCE_NAME)
             .request()
             .header("Content-Type", "application/json")
             .post(add);
            // .post(Entity.entity(professor, MediaType.APPLICATION_JSON_TYPE));
         assertThat(response.getStatus(), is(403));
         
        
     }
    
    @Test
    public void test22_addStudent_with_adminrole_invalid() throws JsonMappingException, JsonProcessingException {
    	Set<CourseRegistration> courseRegistrations = new HashSet<>();
    	Student student = new Student();
    	
    	student.setLastName("invalid");
    	student.setMembershipCards(null);
    	student.setCourseRegistrations(courseRegistrations);
    	
    		Entity<Student> add = Entity.json(student);
    	
    		Response response = webTarget
             .register(adminAuth)
             .path(STUDENT_RESOURCE_NAME)
             .request()
             .header("Content-Type", "application/json")
             .post(add);
            // .post(Entity.entity(professor, MediaType.APPLICATION_JSON_TYPE));
         assertThat(response.getStatus(), is(500));
         
        
        
     }
    
    @Test
    public void test23_addStudent_with_userrole_invalid() throws JsonMappingException, JsonProcessingException {
    	Set<CourseRegistration> courseRegistrations = new HashSet<>();
    	Student student = new Student();
    	
    	student.setLastName("invalid");
    	student.setMembershipCards(null);
    	student.setCourseRegistrations(courseRegistrations);
    	
    		Entity<Student> add = Entity.json(student);
    	
    		Response response = webTarget
             .register(userAuth)
             .path(STUDENT_RESOURCE_NAME)
             .request()
             .header("Content-Type", "application/json")
             .post(add);
            // .post(Entity.entity(professor, MediaType.APPLICATION_JSON_TYPE));
         assertThat(response.getStatus(), is(403));
         
        
        
     }
    
    @Test
    public void test24_all_club_membership_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        
    }
    
    @Test
    public void test25_all_club_membership_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            //.register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403));
        
    }
    
    @Test
    public void test26_get_clubmembershipById_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME+"/{id}").resolveTemplate(RESOURCE_PATH_ID_ELEMENT, 1)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
    }
    
    @Test
    public void test27_get_clubmembershipById_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME+"/{id}").resolveTemplate(RESOURCE_PATH_ID_ELEMENT, 1)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
    }
    
    @Test
    public void test28_get_clubmembershipById_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME+"/{id}").resolveTemplate(RESOURCE_PATH_ID_ELEMENT, 42)
            .request()
            .get();
        assertThat(response.getStatus(), is(500));
    }
    
    @Test
    public void test29_all_membershipcard_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEMBERSHIP_CARD_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        
    }
    
    @Test
    public void test30_all_membershipcard_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEMBERSHIP_CARD_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403));
        
    }
    
    @Test
     public void test31_membershipcard_id_with_userrole() throws JsonMappingException, JsonProcessingException {
         Response response = webTarget
             .register(userAuth)
             .path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + 1)
             .request()
             .get();
         
         assertThat(response.getStatus(), is(200));
     }
    
    
    @Test
     public void test32_membershipcard_id_with_adminRole() throws JsonMappingException, JsonProcessingException {
         Response response = webTarget
             .register(adminAuth)
             .path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + 1)
             .request()
             .get();
         
         assertThat(response.getStatus(), is(200));
         
     }
    
}