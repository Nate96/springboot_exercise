package com.infy.infyinterns.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infy.infyinterns.dto.MentorDTO;
import com.infy.infyinterns.dto.ProjectDTO;
import com.infy.infyinterns.entity.Mentor;
import com.infy.infyinterns.entity.Project;
import com.infy.infyinterns.exception.InfyInternException;
import com.infy.infyinterns.repository.MentorRepository;
import com.infy.infyinterns.repository.ProjectRepository;

@Service(value = "projectService")
@Transactional
public class ProjectAllocationServiceImpl implements ProjectAllocationService {

	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private MentorRepository mentorRepository;
	
	@Override
	public Integer allocateProject(ProjectDTO project) throws InfyInternException {
		Optional<Mentor> optionalMentor=mentorRepository.findById(project.getMentorDTO().getMentorId());
		Mentor mentor=optionalMentor.orElseThrow(()->new InfyInternException("Service.MENTOR_NOT_FOUND"));
		if(mentor.getNumberOfProjectsMentored()>=3) {
			throw new InfyInternException("Service.CANNOT_ALLOCATE_PROJECT");
		}
		Project projectEntity=new Project();
		projectEntity.setProjectName(project.getProjectName());
		projectEntity.setIdeaOwner(project.getIdeaOwner());
		projectEntity.setReleaseDate(project.getReleaseDate());
		projectEntity.setMentor(mentor);
		mentor.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored()+1);
		projectEntity=projectRepository.save(projectEntity);
		return projectEntity.getProjectId();
	}

	
	@Override
	public List<MentorDTO> getMentors(Integer numberOfProjectsMentored) throws InfyInternException {
		List<Mentor> mentorList=mentorRepository.findByNumberOfProjectsMentored(numberOfProjectsMentored);
		if(mentorList.isEmpty()) {
			throw new InfyInternException("Service.MENTOR_NOT_FOUND");
		}
		return mentorList.stream()
				.map(e->new MentorDTO(e.getMentorId(), e.getMentorName(), e.getNumberOfProjectsMentored()))
				.collect(Collectors.toList());
	}


	@Override
	public void updateProjectMentor(Integer projectId, Integer mentorId) throws InfyInternException {
		Optional<Mentor> mentorOptional=mentorRepository.findById(mentorId);
		Mentor mentor=mentorOptional.orElseThrow(()->new InfyInternException("Service.MENTOR_NOT_FOUND"));
		if(mentor.getNumberOfProjectsMentored()>=3) {
			throw new InfyInternException("Service.CANNOT_ALLOCATE_PROJECT");
		}
		Optional<Project> projectOptional=projectRepository.findById(projectId);
		Project project=projectOptional.orElseThrow(()->new InfyInternException("Service.PROJECT_NOT_FOUND"));
		project.setMentor(mentor);
		mentor.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored()+1);
		
	}

	@Override
	public void deleteProject(Integer projectId) throws InfyInternException {
		Optional<Project> projectOptional=projectRepository.findById(projectId);
		Project project=projectOptional.orElseThrow(()->new InfyInternException("Service.PROJECT_NOT_FOUND"));
		Mentor mentor=project.getMentor();
		if(mentor!=null) {
			project.setMentor(null);
			mentor.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored()-1);
			
		}
		projectRepository.delete(project);
	}
}