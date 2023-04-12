package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.Course;

@Path(COURSE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {
	
	private static final Logger LOG = LogManager.getLogger();
	
	@EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getCourses() {
        LOG.debug("retrieving all courses ...");
        List<Course> courses = service.getAllCourses();
        Response response = Response.ok(courses).build();
        return response;
    }
    
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
    	 LOG.debug("retrieving course  ..." + id);
    	 Course course = service.getById(Course.class,Course.COURSES_BY_ID_QUERY, id);
         Response response = Response.ok(course).build();
         return response;
    }
    
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addCourse(Course course) {
        Response response = null;
        Course newCourseWithIdTimestamps = service.persist(course);
        response = Response.ok(newCourseWithIdTimestamps).build();
        return response;
    }
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{id}")
    public Response deleteCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id ) {
    	service.delete(Course.class,Course.COURSES_BY_ID_QUERY, id);
    	return Response.ok().build();
    }

}
