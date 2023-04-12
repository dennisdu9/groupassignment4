package acmecollege.rest.resource;

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
import acmecollege.entity.CourseRegistration;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.COURSE_REGISTRATION_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;
@Path(COURSE_REGISTRATION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseRegistrationResource {
	private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;
    
    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getCourseRegistrations() {
        LOG.debug("retrieving all course registrations ...");
        List<CourseRegistration> courseRegistrations = service.getAll(CourseRegistration.class,"CourseRegistration.findAll");
        Response response = Response.ok(courseRegistrations).build();
        return response;
    }
    
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getCourseRegistration(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("retrieving course registration  ..." + id);
        List<CourseRegistration> cr = service.getListById(CourseRegistration.class,"CourseRegistration.findByStudentId", id);
        Response response = Response.ok(cr).build();
        return response;
    }
    
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addCourseRegistration(CourseRegistration cr) {
        Response response = null;
        CourseRegistration newCrWithIdTimestamps = service.persist(cr);
        response = Response.ok(newCrWithIdTimestamps).build();
        return response;
    }
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{id}")
    public Response deleteCourseRegistration(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id ) {
    	service.delete(CourseRegistration.class,"CourseRegistration.findById", id);
    	return Response.ok().build();
    }
}
