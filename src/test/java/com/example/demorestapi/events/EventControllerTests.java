package com.example.demorestapi.events;

import com.example.demorestapi.accounts.Account;
import com.example.demorestapi.accounts.AccountRepository;
import com.example.demorestapi.accounts.AccountRole;
import com.example.demorestapi.accounts.AccountService;
import com.example.demorestapi.common.BaseControllerTest;
import com.example.demorestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Before
    public void setUp() {
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("description")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("location")
                .build();

        mockMvc.perform(post("/api/events/")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect((jsonPath("id").value(Matchers.not(100))))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing"),
                                linkWithRel("profile").description("profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new envet"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime;"),
                                fieldWithPath("location").description("location"),
                                fieldWithPath("basePrice").description("basePrice"),
                                fieldWithPath("maxPrice").description("maxPrice"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment")

                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header URL"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type")
                        ),
                        //relaxedResponseFields : 문서에 일부분을 확인한다.
                        //responseFields : 모든 요소가 다 들어가 있어야 한다.
                        relaxedResponseFields(
                                fieldWithPath("id").description("id"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new envet"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime;"),
                                fieldWithPath("location").description("location"),
                                fieldWithPath("basePrice").description("basePrice"),
                                fieldWithPath("maxPrice").description("maxPrice"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment"),
                                fieldWithPath("free").description("free"),
                                fieldWithPath("offline").description("offline"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("self links"),
                                fieldWithPath("_links.query-events.href").description("query-events links"),
                                fieldWithPath("_links.update-event.href").description("update-event href"),
                                fieldWithPath("_links.profile.href").description("profile")
                        )
                ));
    }

    private String getAccessToken() throws Exception{
        String username = "a@test.com";
        String password = "1234";

        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(account);

        String clientId = "myApp";
        String clientSecret = "pass";

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password"));
        String response = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser jackson2JsonParser = new Jackson2JsonParser();
        return jackson2JsonParser.parseMap(response).get("access_token").toString();
    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_badRequest() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("description")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("location")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 비어 있는 경우에 발생하는 테스트")
    public void createEvent_bad_request_empty_input() throws Exception{
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @TestDescription("입력값이 잘못된 경우에 발생하는 테스트")
    public void createEvent_bad_request_wrong_input() throws Exception{
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("description")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2019, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("location")
                .build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$[0].objectName").exists())
//                .andExpect(jsonPath("$[0].defaultMessage").exists())
//                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {

        IntStream.range(0, 30).forEach(this::generateEvent);
        this.mockMvc.perform(get("/api/events")
                    .param("page","1")
                    .param("size","10")
                    .param("sort","name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventResourceList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"));
    }

    @Test
    @TestDescription("기존의 이벤트를 하나 조회하기")
    public void queryEvent() throws Exception {

        Event event = this.generateEvent(100);
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name").exists())
                    .andExpect(jsonPath("id").exists())
                    .andExpect(jsonPath("_links.self").exists())
                    .andExpect(jsonPath("_links.profile").exists())
                    .andDo(document("get-an-event"));
    }

    @Test
    @TestDescription("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {

        Event event = this.generateEvent(200);
        String eventName = "Updated Event";
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setName(eventName);

        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists());
    }

    @Test
    @TestDescription("입력값이 비어 있는 경우 이벤트 수정 실패")
    public void updateEventBadRequestEmpty() throws Exception {

        Event event = this.generateEvent(200);

        EventDto eventDto = new EventDto();

        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEventBadRequest() throws Exception {

        Event event = this.generateEvent(200);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(100000);
        eventDto.setMaxPrice(100);

        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("존재하지 않는 이벤트를 수정 실패")
    public void updateEventBadRequestNotFound() throws Exception {

        Event event = this.generateEvent(200);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        this.mockMvc.perform(put("/api/events/243513441", event.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @TestDescription("없는 이벤트를 조회했을때 404 응답받기")
    public void getEvent404() throws Exception {
        this.mockMvc.perform(get("/api/events/2592529"))
                .andExpect(status().isNotFound());
    }

    private Event generateEvent(int i) {
        Event event = Event.builder()
                .name("event" + i)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();
        return this.eventRepository.save(event);
    }
}
