package com.funmeet.modules.club;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funmeet.modules.account.Account;
import com.funmeet.modules.city.City;
import com.funmeet.modules.city.CityService;
import com.funmeet.modules.club.event.ClubCreatedEvent;
import com.funmeet.modules.club.event.ClubUpdateEvent;
import com.funmeet.modules.club.form.ClubDescriptionForm;
import com.funmeet.modules.club.form.ClubForm;
import com.funmeet.modules.hobby.Hobby;
import com.funmeet.modules.hobby.HobbyRepository;
import com.funmeet.modules.hobby.HobbyService;
import com.funmeet.modules.mapper.ClubMapper;
import com.funmeet.modules.meeting.Meeting;
import com.funmeet.modules.meeting.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final HobbyRepository hobbyRepository;
    private final MeetingRepository meetingRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final HobbyService hobbyService;
    private final CityService cityService;
    private final ObjectMapper objectMapper;

    public Club createNewClub(Account account, ClubForm clubForm) {
        Club club = clubRepository.save(ClubMapper.INSTANCE.clubFormToEntity(clubForm));
        club.addManager(account);
        return club;
    }

    public ClubDescriptionForm mappingClubDescription(Club club) {
        return ClubMapper.INSTANCE.ClubToDescriptionForm(club);
    }

    public Club getClub(String path) {
        Club club = clubRepository.findByClubPath(path);
        checkIfExistingClub(path, club);
        return club;
    }

    public Club getClubUpdate(Account account, String path) {
        Club club = this.getClub(path);
        checkIfManager(account, club);
        return club;
    }

    public ClubDescriptionForm mappingDescriptionForm(Club club) {
        return ClubMapper.INSTANCE.ClubToDescriptionForm(club);
    }

    public Club getClubOnlyByPath(String path) {
        return clubRepository.findByClubPath(path);
    }

    public void updateClubDescription(Club club, ClubDescriptionForm clubDescriptionForm) {
        club.updateClubIntroduce(clubDescriptionForm.getShortDescription(), clubDescriptionForm.getFullDescription());
        applicationEventPublisher.publishEvent(new ClubUpdateEvent(club, "모임 소개를 수정했습니다."));
    }

    public Hobby findHobbyByTitle(String title) {
        return hobbyRepository.findByTitle(title).orElseThrow();
    }

    public List<String> getAllHobbyTitles() {
        return hobbyRepository.findAll().stream()
                .map(Hobby::getTitle).collect(Collectors.toList());
    }

    public Club getClubUpdateHobby(Account account, String path) {
        Club club = clubRepository.findClubWithHobbyByClubPath(path);
        checkIfExistingClub(path, club);
        checkIfManager(account, club);

        return club;
    }

    public Club getClubUpdateCity(Account account, String path) {
        Club club = clubRepository.findClubWithCityByClubPath(path);
        checkIfExistingClub(path, club);
        checkIfManager(account, club);
        return club;
    }

    public Club getClubUpdateStatus(Account account, String path) {
        Club club = clubRepository.findClubWithManagersByClubPath(path);
        checkIfExistingClub(path, club);
        checkIfManager(account, club);
        return club;
    }

    public void publish(Club club) {
        club.publish();
        applicationEventPublisher.publishEvent(new ClubCreatedEvent(club));
    }

    public boolean isValidPath(String newPath) {
        if (!newPath.matches("^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$")) {
            return false;
        }
        return !clubRepository.existsByClubPath(newPath);
    }

    public Club addMember(Account account, String path) {
        Club club = clubRepository.findClubWithMembersByClubPath(path);
        club.addMember(account);
        return club;
    }

    public Club removeMember(Account account, String path) {
        Club club = clubRepository.findClubWithMembersByClubPath(path);
        club.removeMember(account);
        return club;
    }

    public Club getClubToEnroll(String path) {
        Club club = clubRepository.findClubOnlyByClubPath(path);
        checkIfExistingClub(path, club);
        return club;
    }

    public String getWhiteListHobby() throws JsonProcessingException {
        List<String> allHobbyTitles = this.getAllHobbyTitles();
        return objectMapper.writeValueAsString(allHobbyTitles);
    }

    public String getWhiteListCity() throws JsonProcessingException {
        List<String> allCityList = cityService.getAllCity();
        return objectMapper.writeValueAsString(allCityList);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() <= 30;
    }

    private void checkIfManager(Account account, Club club) {
        if (!club.isManagerOfBy(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    private void checkIfExistingClub(String path, Club club) {
        if (!clubRepository.existsByClubPath(path)) {
            throw new IllegalArgumentException(path + "에 해당하는 모임이 존재하지 않습니다.");
        }
    }

    public Club updateClubImage(Account account, String path, String image) {
        Club club = this.getClubUpdate(account, path);
        club.updateBannerImage(image);
        return club;
    }

    public Club checkUseClubBanner(Account account, String path, boolean check) {
        Club club = this.getClubUpdate(account, path);
        club.updateClubBanner(true);
        return club;
    }

    public void updateClubPath(Club club, String newPath) {
        club.updateClubPath(newPath);
    }

    public void updateClubTitle(Club club, String newTitle) {
        club.updateClubTitle(newTitle);
    }

    public void disableClubBanner(Club club) {
        club.updateClubBanner(false);
    }

    public void addHobby(Account account, String path, String hobbyTitle) {
        Club club = this.getClubUpdateHobby(account, path);
        Hobby hobby = hobbyService.findOrCreateHobby(hobbyTitle);
        club.addHobby(hobby);
    }

    public void removeHobby(Account account, String path, String hobbyTitle) {
        Club club = this.getClubUpdateHobby(account, path);
        Hobby hobby = this.findHobbyByTitle(hobbyTitle);
        club.removeHobby(hobby);
    }

    public void addCity(Account account, String path, String krCity) {
        Club club = this.getClubUpdateCity(account, path);
        City city = cityService.getCityByKrCity(krCity);
        club.addCity(city);
    }

    public void removeCity(Account account, String path, String krCity) {
        Club club = this.getClubUpdateCity(account, path);
        City city = cityService.getCityByKrCity(krCity);
        club.removeCity(city);
    }

    public List<String> getClubCity(Account account, String path) {
        Club club = this.getClubUpdate(account, path);
        return club.getCity().stream()
                .map(City::toString).collect(Collectors.toList());
    }

    public List<String> getClubHobby(Account account, String path) {
        Club club = this.getClubUpdate(account, path);
        return club.getHobby().stream()
                .map(Hobby::getTitle).collect(Collectors.toList());
    }

    public boolean isPublish(Account account, Club club) {
        return club.isPublish();
    }

    public boolean isNotZeroMeetings(Club club) {
        List<Meeting> meetings = meetingRepository.findByClub(club);
        return meetings.size() != 0;
    }

    public void removeClub(Club club) {
        club.close();
        applicationEventPublisher.publishEvent(new ClubUpdateEvent(club, "모임을 종료합니다."));
        clubRepository.delete(club);
    }
}
