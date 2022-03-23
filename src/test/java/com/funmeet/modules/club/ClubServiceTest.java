package com.funmeet.modules.club;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountFactory;
import com.funmeet.modules.city.City;
import com.funmeet.modules.city.CityRepository;
import com.funmeet.modules.city.CityService;
import com.funmeet.modules.club.form.ClubDescriptionForm;
import com.funmeet.modules.hobby.Hobby;
import com.funmeet.modules.hobby.HobbyRepository;
import com.funmeet.modules.hobby.HobbyService;
import com.funmeet.modules.meeting.MeetingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

/**
 * 검증은 Builder를 통해 엔티티 생성 후 서비스 호출
 * 검증이 아닌 DB를 찔러 데이터를 가져온 후, 가공후 내보내는 것은 Mockito
 */
@ExtendWith(MockitoExtension.class)
public class ClubServiceTest {

    @InjectMocks
    ClubService clubService;

    @Mock
    ClubRepository clubRepository;

    @Mock
    HobbyRepository hobbyRepository;

    @Mock
    MeetingRepository meetingRepository;

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @Mock
    HobbyService hobbyService;

    @Mock
    CityService cityService;

    @Mock
    CityRepository cityRepository;

    @Test
    @DisplayName("getClubTestCode")
    void getClubTestCode() throws Exception {
        final Club club = ClubFactory.createCorrectClub();
        Mockito.when(clubRepository.findByClubPath(any())).thenReturn(club);
        Mockito.when(clubRepository.existsByClubPath(any())).thenReturn(true);

        final Club getClub = clubService.getClub(club.getClubPath());

        assertThat(getClub).isEqualTo(club);
    }

    @Test
    @DisplayName("getClubUpdateCode - 성공")
    void getClubUpdateCode() {
        final Account account = AccountFactory.createHaveIdAdminAccount(1L);
        final Club club = ClubFactory.createMakeManagerClub(account);
        Mockito.when(clubRepository.findByClubPath(any())).thenReturn(club);
        Mockito.when(clubRepository.existsByClubPath(any())).thenReturn(true);

        final Club getClub = clubService.getClubUpdate(account, club.getClubPath());

        assertThat(getClub).isEqualTo(club);
        assertThat(getClub.getManagers()).contains(account);
    }

    @Test
    @DisplayName("getClubUpdateCode - 실패")
        // 뭐가 문제냐면, EqualsAndHashCode가 id값으로 비교하기 때문에 생기는 문제
    void failGetClubUpdateCode() {
        final String EXCEPTION_MESSAGE = "해당 기능을 사용할 수 없습니다.";
        final Account adminAccount = AccountFactory.createHaveIdAdminAccount(1L);
        final Account guestAccount = AccountFactory.createHaveIdGuestAccount(2L);
        final Club club = ClubFactory.createMakeManagerClub(adminAccount);

        Mockito.when(clubRepository.findByClubPath(any())).thenReturn(club);
        Mockito.when(clubRepository.existsByClubPath(any())).thenReturn(true);

        assertThatThrownBy(() -> clubService.getClubUpdate(guestAccount, club.getClubPath()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage(EXCEPTION_MESSAGE);
    }

    @Test
    @DisplayName("mappingDescriptionForm")
    void mappingDescriptionForm() {
        final Club club = ClubFactory.createCorrectClub();

        final ClubDescriptionForm clubDescriptionForm = clubService.mappingDescriptionForm(club);

        assertThat(clubDescriptionForm.getFullDescription()).isEqualTo(club.getFullDescription());
        assertThat(clubDescriptionForm.getShortDescription()).isEqualTo(club.getShortDescription());
    }

    @Test
    @DisplayName("getClubOnlyByPath")
    void getClubOnlyByPath() {
        final Club club = ClubFactory.createCorrectClub();
        final String CLUB_PATH = "test-url001";
        Mockito.when(clubRepository.findByClubPath(any())).thenReturn(club);

        final Club findClub = clubService.getClubOnlyByPath(CLUB_PATH);

        assertThat(findClub).isEqualTo(club);
    }

    @Test
    @DisplayName("updateClubDescription")
    void updateClubDescription() {
        final Club club = ClubFactory.createCorrectClub();
        final ClubDescriptionForm clubDescriptionForm = ClubFactory.createCorrectClubDescriptionForm();
        final String shortDescription = club.getShortDescription();
        final String fullDescription = club.getFullDescription();

        clubService.updateClubDescription(club, clubDescriptionForm);

        assertThat(club.getShortDescription()).isNotEqualTo(shortDescription);
        assertThat(club.getFullDescription()).isNotEqualTo(fullDescription);
    }

    @Test
    @DisplayName("getClubUpdateCity")
    void getClubUpdateCity() {
        final Account adminAccount = AccountFactory.createHaveIdAdminAccount(1L);
        final Club club = ClubFactory.createMakeManagerClub(adminAccount);
        Mockito.when(clubRepository.existsByClubPath(any())).thenReturn(true);
        Mockito.when(clubRepository.findClubWithCityByClubPath(any())).thenReturn(club);

        final Club findClub = clubService.getClubUpdateCity(adminAccount, club.getClubPath());

        assertThat(findClub.getTitle()).isEqualTo(club.getTitle());
    }

    @Test
    @DisplayName("getClubUpdateStatus")
    void getClubUpdateStatus() {
        final Account adminAccount = AccountFactory.createHaveIdAdminAccount(1L);
        final Club club = ClubFactory.createMakeManagerClub(adminAccount);
        Mockito.when(clubRepository.existsByClubPath(any())).thenReturn(true);
        Mockito.when(clubRepository.findClubWithManagersByClubPath(any())).thenReturn(club);

        final Club findClub = clubService.getClubUpdateStatus(adminAccount, club.getClubPath());

        assertThat(findClub.getTitle()).isEqualTo(club.getTitle());
    }

    @Test
    @DisplayName("publish - 성공")
    void publish() {
        final Club club = Club.builder()
                .hobby(new HashSet<>())
                .city(new HashSet<>())
                .closed(false)
                .published(false)
                .build();
        club.getHobby().add(new Hobby(1L, "Test1"));
        club.getCity().add(new City(1L, "test1", "테스트1"));

        clubService.publish(club);

        assertThat(club.getPublishDateTime()).isNotNull();
        assertThat(club.isClosed()).isFalse();
        assertThat(club.isPublish()).isTrue();
    }

    @Test
    @DisplayName("publish - 실패")
    void failPublish() {
        final String EXCEPTION_MESSAGE = "취미가 종료되었거나, 출시 준비 상태입니다.";
        final Club club = Club.builder()
                .hobby(new HashSet<>())
                .city(new HashSet<>())
                .closed(true)
                .published(false)
                .build();
        club.getHobby().add(new Hobby(1L, "Test1"));
        club.getCity().add(new City(1L, "test1", "테스트1"));

        assertThatThrownBy(() -> clubService.publish(club))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(EXCEPTION_MESSAGE);
    }

    @Test
    @DisplayName("isValidPath - 성공")
    void isValidPath() {
        final String NEW_PATH = "(&%)#_@";
        final Club club = ClubFactory.createCorrectClub();

        assertThat(clubService.isValidPath(NEW_PATH)).isFalse();
    }

    @Test
    @DisplayName("isValidPath - 실패")
    void failIsValidPath() {
        final String NEW_PATH = "test-url002";
        final Club club = ClubFactory.createCorrectClub();
        Mockito.when(clubRepository.existsByClubPath(any())).thenReturn(false);

        assertThat(clubService.isValidPath(NEW_PATH)).isTrue();
    }

    @Test
    @DisplayName("addMember")
    void addMember() {
        final Account adminAccount = AccountFactory.createHaveIdAdminAccount(1L);
        final Account guestAccount = AccountFactory.createHaveIdGuestAccount(2L);
        final Club club = ClubFactory.createMakeManagerClub(adminAccount);
        Mockito.when(clubRepository.findClubWithMembersByClubPath(any())).thenReturn(club);

        final Club findClub = clubService.addMember(guestAccount, club.getClubPath());

        assertThat(findClub.getMemberCount()).isEqualTo(2); // 멤버 + 관리자
        assertThat(findClub.getMembers()).contains(guestAccount);
    }

    @Test
    @DisplayName("removeMember")
    void removeMember() {
        final Account adminAccount = AccountFactory.createHaveIdAdminAccount(1L);
        final Account guestAccount = AccountFactory.createHaveIdGuestAccount(2L);
        final Club club = ClubFactory.createMakeManagerClub(adminAccount);
        Mockito.when(clubRepository.findClubWithMembersByClubPath(any())).thenReturn(club);
        club.addManager(guestAccount);

        final Club findClub = clubService.removeMember(guestAccount, club.getClubPath());

        assertThat(findClub.getMemberCount()).isEqualTo(1); // 관리자
        assertThat(findClub.getMembers()).doesNotContain(guestAccount);
    }

    @Test
    @DisplayName("addHobby")
    void addHobby() {
        final String hobbyTitle = "취미1";
        final Account account = AccountFactory.createSuccessAccount();
        final Club club = ClubFactory.createMakeManagerClub(account);
        final Hobby hobby = new Hobby(1L, hobbyTitle);
        Mockito.when(clubRepository.findClubWithHobbyByClubPath(any())).thenReturn(club);
        Mockito.when(clubRepository.existsByClubPath(any())).thenReturn(true);
        Mockito.when(hobbyService.findOrCreateHobby(any())).thenReturn(hobby);

        clubService.addHobby(account, club.getClubPath(), hobbyTitle);

        assertThat(club.getHobby()).contains(hobby);
        assertThat(club.getHobby().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("removeHobby")
    void removeHobby() {
        final String hobbyTitle = "취미1";
        final Account account = AccountFactory.createSuccessAccount();
        final Club club = ClubFactory.createMakeManagerClub(account);
        final Hobby hobby = new Hobby(1L, hobbyTitle);
        Mockito.when(clubRepository.findClubWithHobbyByClubPath(any())).thenReturn(club);
        Mockito.when(clubRepository.existsByClubPath(any())).thenReturn(true);
        Mockito.when(hobbyRepository.findByTitle(any())).thenReturn(Optional.of(hobby));

        club.addHobby(hobby);
        clubService.removeHobby(account, club.getClubPath(), hobbyTitle);

        assertThat(club.getHobby()).doesNotContain(hobby);
        assertThat(club.getHobby().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("addCity")
    void addCity() {
        final String enCityTitle = "testCity";
        final String krCityTitle = "테스트시";
        final Account account = AccountFactory.createSuccessAccount();
        final Club club = ClubFactory.createMakeManagerClub(account);
        final City city = new City(1L, enCityTitle, krCityTitle);
        Mockito.when(clubRepository.existsByClubPath(any())).thenReturn(true);
        Mockito.when(clubRepository.findClubWithCityByClubPath(any())).thenReturn(club);
        Mockito.when(cityService.getCityByKrCity(any())).thenReturn(city);

        clubService.addCity(account, club.getClubPath(), city.getKrCity());

        assertThat(club.getCity()).contains(city);
        assertThat(club.getCity().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("removeCity")
    void removeCity() {
        final String enCityTitle = "testCity";
        final String krCityTitle = "테스트시";
        final Account account = AccountFactory.createSuccessAccount();
        final Club club = ClubFactory.createMakeManagerClub(account);
        final City city = new City(1L, enCityTitle, krCityTitle);
        Mockito.when(clubRepository.existsByClubPath(any())).thenReturn(true);
        Mockito.when(clubRepository.findClubWithCityByClubPath(any())).thenReturn(club);
        Mockito.when(cityService.getCityByKrCity(any())).thenReturn(city);

        club.addCity(city);
        clubService.removeCity(account, club.getClubPath(), krCityTitle);

        assertThat(club.getCity()).doesNotContain(city);
        assertThat(club.getCity().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("getClubCity")
    void getClubCity() {
        final String enCityTitle = "testCity";
        final String krCityTitle = "테스트시";
        final Account account = AccountFactory.createSuccessAccount();
        final Club club = ClubFactory.createMakeManagerClub(account);
        final City city = new City(1L, enCityTitle, krCityTitle);
        final String cityToString = city.toString();
        Mockito.when(clubRepository.findByClubPath(any())).thenReturn(club);
        Mockito.when(clubRepository.existsByClubPath(any())).thenReturn(true);

        club.addCity(city);
        List<String> clubCityLists = clubService.getClubCity(account, club.getClubPath());

        assertThat(clubCityLists.size()).isEqualTo(1);
        assertThat(clubCityLists.get(0)).isEqualTo(cityToString);
    }

    @Test
    @DisplayName("getClubHobby")
    void getClubHobby() {
        final String hobbyTitle = "취미1";
        final Account account = AccountFactory.createSuccessAccount();
        final Club club = ClubFactory.createMakeManagerClub(account);
        final Hobby hobby = new Hobby(1L, hobbyTitle);
        Mockito.when(clubRepository.findByClubPath(any())).thenReturn(club);
        Mockito.when(clubRepository.existsByClubPath(any())).thenReturn(true);

        club.addHobby(hobby);
        List<String> clubCityLists = clubService.getClubHobby(account, club.getClubPath());

        assertThat(clubCityLists.size()).isEqualTo(1);
        assertThat(clubCityLists.get(0)).isEqualTo(hobbyTitle);
    }
}
