package com.infy.infyinterns;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.infy.infyinterns.dto.MentorDTO;
import com.infy.infyinterns.dto.ProjectDTO;
import com.infy.infyinterns.entity.Mentor;
import com.infy.infyinterns.exception.InfyInternException;
import com.infy.infyinterns.repository.MentorRepository;
import com.infy.infyinterns.service.ProjectAllocationService;
import com.infy.infyinterns.service.ProjectAllocationServiceImpl;

@SpringBootTest
public class InfyInternsApplicationTests {

	@Mock
	private MentorRepository mentorRepository;

	@InjectMocks
	private ProjectAllocationService projectAllocationService = new ProjectAllocationServiceImpl();


	@Test
	public void allocateProjectCannotAllocateTest() throws Exception {
		Mentor mentorFromMockRepo=new Mentor();
		mentorFromMockRepo.setMentorId(1234);
		mentorFromMockRepo.setMentorName("Andrew");
		mentorFromMockRepo.setNumberOfProjectsMentored(3);
		Mockito.when(mentorRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(mentorFromMockRepo));
		
		ProjectDTO projectDTO=new ProjectDTO();
		projectDTO.setIdeaOwner(1111);
		projectDTO.setProjectName("My Shopping App");
		projectDTO.setReleaseDate(LocalDate.of(2020, Month.JANUARY, 5));
		MentorDTO mentorDTO=new MentorDTO();
		mentorDTO.setMentorId(1234);
		projectDTO.setMentorDTO(mentorDTO);
		
		InfyInternException exception=Assertions.assertThrows(InfyInternException.class, 
				()->projectAllocationService.allocateProject(projectDTO));
		Assertions.assertEquals("Service.CANNOT_ALLOCATE_PROJECT", exception.getMessage());
	}

	@Test
	public void allocateProjectMentorNotFoundTest() throws Exception {
	
		Mockito.when(mentorRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(null));
		
		ProjectDTO projectDTO=new ProjectDTO();
		projectDTO.setIdeaOwner(1111);
		projectDTO.setProjectName("My Shopping App");
		projectDTO.setReleaseDate(LocalDate.of(2020, Month.JANUARY, 5));
		MentorDTO mentorDTO=new MentorDTO();
		mentorDTO.setMentorId(1234);
		projectDTO.setMentorDTO(mentorDTO);
		
		InfyInternException exception=Assertions.assertThrows(InfyInternException.class, 
				()->projectAllocationService.allocateProject(projectDTO));
		Assertions.assertEquals("Service.MENTOR_NOT_FOUND", exception.getMessage());

	}
}