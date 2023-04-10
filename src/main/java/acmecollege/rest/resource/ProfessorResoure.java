package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;
import static acmecollege.utility.MyConstants.PROFESSOR_SUBRESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;

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
import acmecollege.entity.Professor;

@Path(PROFESSOR_SUBRESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfessorResoure {
	private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;
    
    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getProfessors() {
        LOG.debug("retrieving all professors ...");
        List<Professor> professors = service.getAll(Professor.class,"Professor.findAll");
        Response response = Response.ok(professors).build();
        return response;
    }
    
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getProfessor(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("retrieving professor  ..." + id);
        Professor professor = service.getById(Professor.class,"Professor.findById", id);
        Response response = Response.ok(professor).build();
        return response;
    }
    
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addProfessor(Professor newProfessor) {
        Response response = null;
        Professor newProfessorWithIdTimestamps = service.persist(newProfessor);
        response = Response.ok(newProfessorWithIdTimestamps).build();
        return response;
    }
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    public Response deleteProfessor(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id ) {
    	service.delete(Professor.class,"Professor.findById", id);
    	return Response.ok().build();
    }
}
