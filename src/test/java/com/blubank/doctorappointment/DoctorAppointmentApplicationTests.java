package com.blubank.doctorappointment;

import com.blubank.doctorappointment.model.dto.AppointmentTakeRequestDto;
import com.blubank.doctorappointment.model.dto.AppointmentsAddRequestDto;
import com.blubank.doctorappointment.model.entity.Appointment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
				.andExpect(status().isBadRequest())
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

	@Test
	public void givenStartAndEnd_thenViewAllAppointments() throws Exception {
		mvc.perform(post("/api/appointment/add")
				.content(asJsonString(new AppointmentsAddRequestDto()
						.setDate(LocalDate.now())
						.setStartTime(LocalTime.of(16,00))
						.setEndTime(LocalTime.of(17,00))
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print());
		mvc.perform(get("/api/appointment/view-all")
				.contentType(MediaType.APPLICATION_JSON)
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
	public void givenReset_thenViewEmptyList() throws Exception {
		mvc.perform(delete("/api/appointment/reset")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNoContent());
		mvc.perform(get("/api/appointment/view-all")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	public void givenStartAndEnd_thenTake1stAppointment_thenViewItAsTaken() throws Exception {
		ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
		String contentStr = mvc.perform(post("/api/appointment/add")
				.content(asJsonString(new AppointmentsAddRequestDto()
						.setDate(LocalDate.now())
						.setStartTime(LocalTime.of(16, 00))
						.setEndTime(LocalTime.of(17, 00))
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		List<Appointment> appointmentList = objectMapper.readValue(contentStr, new TypeReference<List<Appointment>>(){});

		mvc.perform(post("/api/appointment/take")
				.content(asJsonString(new AppointmentTakeRequestDto()
						.setId(appointmentList.get(0).getId())
						.setPatientName("Sina Momken")
						.setPatientPhone("09124574396")
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.isTaken", is(true)))
				.andExpect(jsonPath("$.patientName", is("Sina Momken")))
				.andExpect(jsonPath("$.patientPhone", is("09124574396")));

		mvc.perform(get("/api/appointment/view-all")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].isTaken", is(true)))
				.andExpect(jsonPath("$[0].patientName", is("Sina Momken")))
				.andExpect(jsonPath("$[0].patientPhone", is("09124574396")))
				.andExpect(jsonPath("$[0].startTime", is("16:00:00")))
				.andExpect(jsonPath("$[0].endTime", is("16:30:00")))
				.andExpect(jsonPath("$[0].date", is(LocalDate.now().toString())))
				.andExpect(jsonPath("$[1].isTaken", is(false)))
				.andExpect(jsonPath("$[1].patientName").value(IsNull.nullValue()))
				.andExpect(jsonPath("$[1].patientPhone").value(IsNull.nullValue()))
				.andExpect(jsonPath("$[1].startTime", is("16:30:00")))
				.andExpect(jsonPath("$[1].endTime", is("17:00:00")))
				.andExpect(jsonPath("$[1].date", is(LocalDate.now().toString())));
	}

	@Test
	public void givenStartAndEnd_thenDeleteFirstAppointment_thenGetRestOfAppointments() throws Exception {
		ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
		String contentStr = mvc.perform(post("/api/appointment/add")
				.content(asJsonString(new AppointmentsAddRequestDto()
						.setDate(LocalDate.now())
						.setStartTime(LocalTime.of(16, 00))
						.setEndTime(LocalTime.of(17, 00))
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		List<Appointment> appointmentList = objectMapper.readValue(contentStr, new TypeReference<List<Appointment>>(){});
		mvc.perform(delete("/api/appointment/delete/{id}", appointmentList.get(0).getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNoContent());
		mvc.perform(get("/api/appointment/view-all")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void givenInvalidId_thenGet404WhenDeleting() throws Exception {
		mvc.perform(delete("/api/appointment/delete/{id}", 1000000)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errorCode", is("101002")));
	}

	@Test
	public void givenStartAndEnd_thenTake1stAppointment_thenGet406WhenDeleting() throws Exception {
		ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
		String contentStr = mvc.perform(post("/api/appointment/add")
				.content(asJsonString(new AppointmentsAddRequestDto()
						.setDate(LocalDate.now())
						.setStartTime(LocalTime.of(16, 00))
						.setEndTime(LocalTime.of(17, 00))
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		List<Appointment> appointmentList = objectMapper.readValue(contentStr, new TypeReference<List<Appointment>>(){});

		mvc.perform(post("/api/appointment/take")
				.content(asJsonString(new AppointmentTakeRequestDto()
						.setId(appointmentList.get(0).getId())
						.setPatientName("Sina Momken")
						.setPatientPhone("09124574396")
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.isTaken", is(true)))
				.andExpect(jsonPath("$.patientName", is("Sina Momken")))
				.andExpect(jsonPath("$.patientPhone", is("09124574396")));

		mvc.perform(delete("/api/appointment/delete/{id}", appointmentList.get(0).getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotAcceptable())
				.andExpect(jsonPath("$.errorCode", is("101003")));
	}

	@Test
	public void givenStartAndEnd_thenViewOpenAppointments() throws Exception {
		mvc.perform(post("/api/appointment/add")
				.content(asJsonString(new AppointmentsAddRequestDto()
						.setDate(LocalDate.now())
						.setStartTime(LocalTime.of(16,00))
						.setEndTime(LocalTime.of(17,00))
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print());
		mvc.perform(get("/api/appointment/view-opens")
				.param("date", LocalDate.now().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].startTime", is("16:00:00")))
				.andExpect(jsonPath("$[0].endTime", is("16:30:00")))
				.andExpect(jsonPath("$[0].date", is(LocalDate.now().toString())))
				.andExpect(jsonPath("$[0].isTaken", is(false)))
				.andExpect(jsonPath("$[1].startTime", is("16:30:00")))
				.andExpect(jsonPath("$[1].endTime", is("17:00:00")))
				.andExpect(jsonPath("$[1].date", is(LocalDate.now().toString())))
				.andExpect(jsonPath("$[1].isTaken", is(false)));
	}

	@Test
	public void givenReset_thenViewEmptyOpens() throws Exception {
		mvc.perform(delete("/api/appointment/reset")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNoContent());
		mvc.perform(get("/api/appointment/view-opens")
				.param("date", LocalDate.now().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	public void givenStartAndEnd_thenTake1stAppointment_thenItShouldBeTaken() throws Exception {
		ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
		String contentStr = mvc.perform(post("/api/appointment/add")
				.content(asJsonString(new AppointmentsAddRequestDto()
						.setDate(LocalDate.now())
						.setStartTime(LocalTime.of(16, 00))
						.setEndTime(LocalTime.of(17, 00))
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		List<Appointment> appointmentList = objectMapper.readValue(contentStr, new TypeReference<List<Appointment>>(){});

		mvc.perform(post("/api/appointment/take")
				.content(asJsonString(new AppointmentTakeRequestDto()
						.setId(appointmentList.get(0).getId())
						.setPatientName("Sina Momken")
						.setPatientPhone("09124574396")
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.isTaken", is(true)))
				.andExpect(jsonPath("$.patientName", is("Sina Momken")))
				.andExpect(jsonPath("$.patientPhone", is("09124574396")));
	}

	@Test
	public void givenNullIdToTake_thenGetError101004() throws Exception {
		mvc.perform(post("/api/appointment/take")
				.content(asJsonString(new AppointmentTakeRequestDto()
						.setId(null)
						.setPatientName("Sina Momken")
						.setPatientPhone("09124574396")
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errorCode", is("101004")));
	}

	@Test
	public void givenEmptyPatientNameToTake_thenGetError101005() throws Exception {
		mvc.perform(post("/api/appointment/take")
				.content(asJsonString(new AppointmentTakeRequestDto()
						.setId(1L)
						.setPatientName("")
						.setPatientPhone("09124574396")
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errorCode", is("101005")));
	}

	@Test
	public void givenInvalidPatientPhoneToTake_thenGetError101006() throws Exception {
		mvc.perform(post("/api/appointment/take")
				.content(asJsonString(new AppointmentTakeRequestDto()
						.setId(1L)
						.setPatientName("Sina Momken")
						.setPatientPhone("0912")
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errorCode", is("101006")));
	}

	@Test
	public void givenStartAndEnd_thenTake1stAppointmentTwice_thenGetError101007() throws Exception {
		ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
		String contentStr = mvc.perform(post("/api/appointment/add")
				.content(asJsonString(new AppointmentsAddRequestDto()
						.setDate(LocalDate.now())
						.setStartTime(LocalTime.of(16, 00))
						.setEndTime(LocalTime.of(17, 00))
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		List<Appointment> appointmentList = objectMapper.readValue(contentStr, new TypeReference<List<Appointment>>(){});

		mvc.perform(post("/api/appointment/take")
				.content(asJsonString(new AppointmentTakeRequestDto()
						.setId(appointmentList.get(0).getId())
						.setPatientName("Sina Momken")
						.setPatientPhone("09124574396")
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.isTaken", is(true)))
				.andExpect(jsonPath("$.patientName", is("Sina Momken")))
				.andExpect(jsonPath("$.patientPhone", is("09124574396")));

		mvc.perform(post("/api/appointment/take")
				.content(asJsonString(new AppointmentTakeRequestDto()
						.setId(appointmentList.get(0).getId())
						.setPatientName("Sina Momken")
						.setPatientPhone("09124574396")
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotAcceptable())
				.andExpect(jsonPath("$.errorCode", is("101007")));
	}

	@Test
	public void givenStartAndEnd_thenDelete1stAppointment_thenTake1stAppointment_thenGetError101007() throws Exception {
		ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
		String contentStr = mvc.perform(post("/api/appointment/add")
				.content(asJsonString(new AppointmentsAddRequestDto()
						.setDate(LocalDate.now())
						.setStartTime(LocalTime.of(16, 00))
						.setEndTime(LocalTime.of(17, 00))
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		List<Appointment> appointmentList = objectMapper.readValue(contentStr, new TypeReference<List<Appointment>>(){});

		mvc.perform(delete("/api/appointment/delete/{id}", appointmentList.get(0).getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNoContent());

		mvc.perform(post("/api/appointment/take")
				.content(asJsonString(new AppointmentTakeRequestDto()
						.setId(appointmentList.get(0).getId())
						.setPatientName("Sina Momken")
						.setPatientPhone("09124574396")
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotAcceptable())
				.andExpect(jsonPath("$.errorCode", is("101007")));
	}

	@Test
	public void givenStartAndEnd_thenViewEmptyListForMyPhone() throws Exception {
		mvc.perform(post("/api/appointment/add")
				.content(asJsonString(new AppointmentsAddRequestDto()
						.setDate(LocalDate.now())
						.setStartTime(LocalTime.of(16,00))
						.setEndTime(LocalTime.of(17,00))
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print());

		mvc.perform(get("/api/appointment/view-own/{phoneNumber}", "09124574396")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	public void givenStartAndEnd_thenTake2Appointments_thenView2AppointmentsForMyPhone() throws Exception {
		ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
		String contentStr = mvc.perform(post("/api/appointment/add")
				.content(asJsonString(new AppointmentsAddRequestDto()
						.setDate(LocalDate.now())
						.setStartTime(LocalTime.of(16, 00))
						.setEndTime(LocalTime.of(17, 00))
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		List<Appointment> appointmentList = objectMapper.readValue(contentStr, new TypeReference<List<Appointment>>(){});

		mvc.perform(post("/api/appointment/take")
				.content(asJsonString(new AppointmentTakeRequestDto()
						.setId(appointmentList.get(0).getId())
						.setPatientName("Sina Momken")
						.setPatientPhone("09124574396")
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.isTaken", is(true)))
				.andExpect(jsonPath("$.patientName", is("Sina Momken")))
				.andExpect(jsonPath("$.patientPhone", is("09124574396")));

		mvc.perform(post("/api/appointment/take")
				.content(asJsonString(new AppointmentTakeRequestDto()
						.setId(appointmentList.get(1).getId())
						.setPatientName("Sina Momken")
						.setPatientPhone("09124574396")
				)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.isTaken", is(true)))
				.andExpect(jsonPath("$.patientName", is("Sina Momken")))
				.andExpect(jsonPath("$.patientPhone", is("09124574396")));

		mvc.perform(get("/api/appointment/view-own/{phoneNumber}", "09124574396")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].isTaken", is(true)))
				.andExpect(jsonPath("$[0].patientName", is("Sina Momken")))
				.andExpect(jsonPath("$[0].patientPhone", is("09124574396")))
				.andExpect(jsonPath("$[0].startTime", is("16:00:00")))
				.andExpect(jsonPath("$[0].endTime", is("16:30:00")))
				.andExpect(jsonPath("$[0].date", is(LocalDate.now().toString())))
				.andExpect(jsonPath("$[1].isTaken", is(true)))
				.andExpect(jsonPath("$[1].patientName", is("Sina Momken")))
				.andExpect(jsonPath("$[1].patientPhone", is("09124574396")))
				.andExpect(jsonPath("$[1].startTime", is("16:30:00")))
				.andExpect(jsonPath("$[1].endTime", is("17:00:00")))
				.andExpect(jsonPath("$[1].date", is(LocalDate.now().toString())));
	}

	//### region private
	private static String asJsonString(final Object obj) {
		try {
			ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	//### endregion private
}
