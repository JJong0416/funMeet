package com.funmeet.modules.club;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funmeet.infra.MockMvcTest;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountFactory;
import com.funmeet.modules.account.AccountRepository;
import com.funmeet.modules.city.City;
import com.funmeet.modules.city.CityForm;
import com.funmeet.modules.city.CityRepository;
import com.funmeet.modules.hobby.Hobby;
import com.funmeet.modules.hobby.HobbyForm;
import com.funmeet.modules.hobby.HobbyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Collectors;

import static com.funmeet.modules.club.ClubFactory.CORRECT_CLUB_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class ClubSettingsControllerTest {
    /**
     * 컨트롤러 통합 테스트
     */
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ClubRepository clubRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    HobbyRepository hobbyRepository;

    @Autowired
    ClubService clubService;

    @Autowired
    CityRepository cityRepository;

    // TODO: 2022-03-23 조금 더 개선할 수 있는 방법 생각해보기
    @BeforeEach
    void init() {
        final Account adminAccount = AccountFactory.createSuccessAccount();
        accountRepository.save(adminAccount);

        final Account guestAccount = AccountFactory.createGuestAccount();
        accountRepository.save(guestAccount);

        final Club club = ClubFactory.createMakeManagerClub(adminAccount);
        clubRepository.save(club);
    }

    @Test
    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모임 소개 보기")
    void URL을_입력해서_모임의_소개를_확인할수_있다() throws Exception {
        final Club club = clubRepository.findByClubPath(CORRECT_CLUB_PATH);

        mockMvc.perform(get("/club/" + club.getClubPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/settings/description"));
    }


    @Test
    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모임 소개 양식에 맞게 변경 - 성공")
    void updateDescriptionForm_right() throws Exception {
        final Club club = clubRepository.findByClubPath(CORRECT_CLUB_PATH);

        mockMvc.perform(post("/club/" + club.getClubPath() + "/settings/description")
                .param("shortDescription", "짧은 글 소개")
                .param("fullDescription", "긴 소개")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", "성공"))
                .andExpect(redirectedUrl("/club/" + club.getClubPath() + "/settings/description"));
    }

    @Test
    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("모임 소개 양식에 틀리게 변경 - 실패")
    void updateDescription_wrong() throws Exception {
        final Club club = clubRepository.findByClubPath(CORRECT_CLUB_PATH);

        mockMvc.perform(post("/club/" + club.getClubPath() + "/settings/description")
                .param("shortDescription", "") // NotBlank
                .param("fullDescription", "긴 소개 체크")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }

    @Test
    @WithUserDetails(value = "account002", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("권한이 없는 사람이 모임 정보를 변경할 때 - 실패")
    void updateNotAccessAccountDescription_wrong() throws Exception {
        final Club club = clubRepository.findByClubPath(CORRECT_CLUB_PATH);

        mockMvc.perform(post("/club/" + club.getClubPath() + "/settings/description")
                .param("shortDescription", "짧은 글 소개")
                .param("fullDescription", "긴 소개 체크")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors());
    }

    @Test
    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("권한이 있는 사람이 배너 모드에 들어갈 때 - 성공")
    void successCanAccessAccountReadBannerMode() throws Exception {
        final Club club = clubRepository.findByClubPath(CORRECT_CLUB_PATH);

        mockMvc.perform(get("/club/" + club.getClubPath() + "/settings/banner"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/settings/banner"));
    }

    @Test
    @WithUserDetails(value = "account002", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("권한이 없는 사람이 배너 모드에 들어갈 때 - 실패")
    void failNotAccessAccountReadBannerMode() throws Exception {
        final Club club = clubRepository.findByClubPath(CORRECT_CLUB_PATH);

        mockMvc.perform(get("/club/" + club.getClubPath() + "/settings/banner"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors());
    }

    @Test
    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("권한이 있는 사람이 취미를 조회했을 때 - 성공")
    void 권한이_있는_사람이_취미를_조회했을_때() throws Exception {
        final Club club = clubRepository.findByClubPath(CORRECT_CLUB_PATH);

        mockMvc.perform(get("/club/" + club.getClubPath() + "/settings/hobby"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/settings/hobby"));
    }

    @Test
    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("권한이 있는 사람이 취미를 추가했을 때 - 성공")
    void 권한이_있는_사람이_취미를_추가했을_때() throws Exception {
        final String HOBBY_TITLE = "취미1";
        final Club club = clubRepository.findByClubPath(CORRECT_CLUB_PATH);
        final HobbyForm hobbyForm = new HobbyForm(HOBBY_TITLE);

        mockMvc.perform(post("/club/" + club.getClubPath() + "/settings/hobby/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertThat(club.getHobby().size()).isEqualTo(1);
        assertThat(club.getHobby().stream().map(Hobby::getTitle).collect(Collectors.toList()).get(0)).isEqualTo(HOBBY_TITLE);
    }

    @Test
    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("권한이 있는 사람이 취미를 삭제했을 때 - 성공")
    void 권한이_있는_사람이_취미를_삭제했을_때() throws Exception {
        final String HOBBY_TITLE = "취미1";
        final HobbyForm hobbyForm = new HobbyForm(HOBBY_TITLE);
        final Club club = clubRepository.findByClubPath(CORRECT_CLUB_PATH);
        final Hobby hobby = hobbyRepository.save(Hobby.builder().title(HOBBY_TITLE).build());
        final Hobby findHobby = hobbyRepository.findByTitle(HOBBY_TITLE).orElseThrow();
        club.addHobby(findHobby);

        mockMvc.perform(post("/club/" + club.getClubPath() + "/settings/hobby/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertThat(club.getHobby()).isEmpty();
    }

    @Test
    @WithUserDetails(value = "account002", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("권한이 없는 사람이 취미를 삭제했을 때 - 성공")
    void 권한이_없_사람이_취미를_삭제했을_때() throws Exception {
        final String HOBBY_TITLE = "취미1";
        final HobbyForm hobbyForm = new HobbyForm(HOBBY_TITLE);
        final Club club = clubRepository.findByClubPath(CORRECT_CLUB_PATH);
        final Hobby hobby = hobbyRepository.save(Hobby.builder().title(HOBBY_TITLE).build());
        Hobby findHobby = hobbyRepository.findByTitle(HOBBY_TITLE).orElseThrow();
        club.addHobby(findHobby);

        mockMvc.perform(post("/club/" + club.getClubPath() + "/settings/hobby/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyForm))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors());

        assertThat(club.getHobby().size()).isEqualTo(1);
    }

    @Test
    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("권한이 있는 사람이 관심지역을 조회했을 때 - 성공")
    void 권한이_있는_사람이_관심지역을_조회했을_때() throws Exception {
        final Club club = clubRepository.findByClubPath(CORRECT_CLUB_PATH);

        mockMvc.perform(get("/club/" + club.getClubPath() + "/settings/city"))
                .andExpect(status().isOk())
                .andExpect(view().name("club/settings/city"));
    }

    @Test
    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("권한이 있는 사람이 관심지역을 추가했을 때 - 성공")
    void 권한이_있는_사람이_관심지역을_추가했을_때() throws Exception {
        final Club club = clubRepository.findByClubPath(CORRECT_CLUB_PATH);
        final City city = City.builder().enCity("Seoul").krCity("서울전체").build();
        final CityForm cityForm = CityForm.builder().cityName("Seoul(서울전체)").build();

        mockMvc.perform(post("/club/" + club.getClubPath() + "/settings/city/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cityForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertThat(club.getCity().size()).isEqualTo(1);
        assertThat(club.getCity().stream().map(City::getKrCity).collect(Collectors.toList()).get(0)).isEqualTo("서울전체");
    }

    @Test
    @WithUserDetails(value = "account001", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("권한이 있는 사람이 관심지역을 삭제했을 때 - 성공")
    void 권한이_있는_사람이_관심지역을_삭제했을_때() throws Exception {
        final String CITY_FULL_TITLE = "Seoul(서울전체)";
        final Club club = clubRepository.findByClubPath(CORRECT_CLUB_PATH);
        final CityForm cityForm = new CityForm(CITY_FULL_TITLE);
        final City city = cityRepository.save(new City(1L, "Seoul", "서울전체"));
        final City findCity = cityRepository.findByKrCity("서울전체").orElseThrow();
        club.addCity(findCity);

        mockMvc.perform(post("/club/" + club.getClubPath() + "/settings/city/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cityForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertThat(club.getHobby()).isEmpty();
    }
}
