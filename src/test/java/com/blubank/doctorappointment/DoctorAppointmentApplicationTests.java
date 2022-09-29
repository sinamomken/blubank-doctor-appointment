package com.blubank.doctorappointment;

import com.blubank.doctorappointment.model.dto.AppointmentsAddRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
class DoctorAppointmentApplicationTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mvc;

	@BeforeEach
	public void setup() throws Exception {
		this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}

	@Test
	public void givenStartAndEnd_thenGetRightAppointments() throws Exception {
		mvc.perform(post("/api/appointment/add")
				.content(asJsonString(new AppointmentsAddRequestDto()
						.setDate(LocalDate.now())
						.setStartTime(LocalTime.of(16,00))
						.setEndTime(LocalTime.of(17,00))
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].startTime", is("16:00:00")))
				.andExpect(jsonPath("$[0].endTime", is("16:30:00")))
				.andExpect(jsonPath("$[0].date", is(LocalDate.now().toString())))
				.andExpect(jsonPath("$[1].startTime", is("16:30:00")))
				.andExpect(jsonPath("$[1].endTime", is("17:00:00")))
				.andExpect(jsonPath("$[1].date", is(LocalDate.now().toString())));
	}

	@Test
	public void givenStartLaterThanEnd_thenGetError() throws Exception {
		mvc.perform(post("/api/appointment/add")
				.content(asJsonString(new AppointmentsAddRequestDto()
						.setDate(LocalDate.now())
						.setStartTime(LocalTime.of(17,00))
						.setEndTime(LocalTime.of(16,00))
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.errorCode", is("101001")));
	}

	@Test
	public void givenShortStartAndEnd_thenGetEmptyList() throws Exception {
		mvc.perform(post("/api/appointment/add")
				.content(asJsonString(new AppointmentsAddRequestDto()
						.setDate(LocalDate.now())
						.setStartTime(LocalTime.of(16,40))
						.setEndTime(LocalTime.of(17,00))
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	private static String asJsonString(final Object obj) {
		try {
			ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
