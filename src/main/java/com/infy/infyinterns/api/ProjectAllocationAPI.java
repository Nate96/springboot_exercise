package com.infy.infyinterns.api;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.infyinterns.dto.MentorDTO;
import com.infy.infyinterns.dto.ProjectDTO;
import com.infy.infyinterns.exception.InfyInternException;
import com.infy.infyinterns.service.ProjectAllocationService;

@RestController
@Validated
@RequestMapping(value = "infyinterns")
public class ProjectAllocationAPI {
	@Autowired
	private ProjectAllocationService projectService;
	@Autowired
	private Environment environment;

	// add new project along with mentor details
	@PostMapping(value = "/project")
	public ResponseEntity<String> allocateProject(@Valid @RequestBody ProjectDTO project) throws InfyInternException {
		Integer projectId = projectService.allocateProject(project);
		String message=environment.getProperty("API.ALLOCATION_SUCCESS")+":"+projectId;
		return new ResponseEntity<String>(message, HttpStatus.CREATED);
	}

	// get mentors based on idea owner
	@GetMapping(value = "mentor/{numberOfProjectsMentored}")
	public ResponseEntity<List<MentorDTO>> getMentors(@PathVariable Integer numberOfProjectsMentored) throws InfyInternException {
		List<MentorDTO> mentorList=projectService.getMentors(numberOfProjectsMentored);
		return new ResponseEntity<>(mentorList, HttpStatus.OK);
	}

	// update the mentor of a project
	@PutMapping(value = "project/{projectId}/{mentorId}")
	public ResponseEntity<String> updateProjectMentor(@PathVariable Integer projectId, @PathVariable @Min(value = 1000,message = "{mentor.mentorid.invalid}") @Max(value = 9999,message = "{mentor.mentorid.invalid}") Integer mentorId) throws InfyInternException {
		projectService.updateProjectMentor(projectId, mentorId);
		String message=environment.getProperty("API.PROJECT_UPDATE_SUCCESS");
		
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	// delete a project
	@DeleteMapping(value = "project/{projectId}")
	public ResponseEntity<String> deleteProject(@PathVariable Integer projectId) throws InfyInternException {
		projectService.deleteProject(projectId);
		String message=environment.getProperty("API.PROJECT_DELETE_SUCCESS");
		ResponseEntity<String> response=new ResponseEntity<>(message, HttpStatus.OK);
		return response;
	}

}
