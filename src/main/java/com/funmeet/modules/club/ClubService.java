package com.funmeet.modules.club;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.city.City;
import com.funmeet.modules.club.event.ClubCreatedEvent;
import com.funmeet.modules.club.event.ClubUpdateEvent;
import com.funmeet.modules.club.form.ClubDescriptionForm;
import com.funmeet.modules.hobby.Hobby;
import com.funmeet.modules.hobby.HobbyRepository;
import com.funmeet.modules.hobby.HobbyService;
import com.funmeet.modules.meeting.MeetingRepository;
import com.funmeet.modules.meeting.MeetingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.HashSet;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j

public class ClubService {

    private final ClubRepository clubRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final HobbyService hobbyService;

    public Club createNewClub(Club club, Account account){
        Club createClub = clubRepository.save(club);
        createClub.addManager(account);
        return createClub;
    }

    public Club getClub(String path) {
        Club club = clubRepository.findByClubPath(path);
        checkIfExistingClub(path,club);
        return club;
    }

    public Club getClubUpdate(Account account, String path) {
        Club club = this.getClub(path);
        checkIfManager(account,club);
        return club;
    }

    public void updateClubDescription(Club club, ClubDescriptionForm clubDescriptionForm) {
        modelMapper.map(clubDescriptionForm, club);
        applicationEventPublisher.publishEvent(new ClubUpdateEvent(club,"모임 소개를 수정했습니다."));

    }

    public void updateClubImage(Club club, String image) {
        club.setBanner(image);
    }

    public void enableClubBanner(Club club) {
        club.setUseBanner(true);
    }

    public void disableClubBanner(Club club) {
        club.setUseBanner(false);
    }


    public void addHobby(Club club, Hobby hobby) {
        club.getHobby().add(hobby);
    }

    public void removeHobby(Club club, Hobby hobby) {
        club.getHobby().remove(hobby);
    }

    public void addCity(Club club, City city) {
        club.getCity().add(city);

    }

    public void removeCity(Club club, City city) {
        club.getCity().remove(city);
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
        Club club = clubRepository.findByClubPath(path);
        checkIfExistingClub(path, club);
        checkIfManager(account, club);
        return club;
    }

    private void checkIfManager(Account account, Club club) {
        if (!club.isManagerOfBy(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    private void checkIfExistingClub(String path, Club club) {
        if (club == null) {
            throw new IllegalArgumentException(path + "에 해당하는 모임이 없습니다.");
        }
    }

    public void publish(Club club) {
        club.publish();
        applicationEventPublisher.publishEvent(new ClubCreatedEvent(club)); // Async

    }

    public void close(Club club) {
        club.close();
        applicationEventPublisher.publishEvent(new ClubUpdateEvent(club,"모임을 종료합니다."));
    }

    public boolean isValidPath(String newPath) {
        if (!newPath.matches("^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$")) {
            return false;
        }

        return !clubRepository.existsByClubPath(newPath);
    }



    public void updateClubPath(Club club, String newPath) {
        club.setClubPath(newPath);
    }

    public boolean isValidTitle(String newTitle) { // TODO validation new Title
        return newTitle.length() <= 30;
    }

    public void updateClubTitle(Club club, String newTitle) {
        club.setTitle(newTitle);
    }

    public void remove(Club club) {
        clubRepository.delete(club);
    }

    public void addMember(Club club, Account account) {
        club.addMember(account);
    }

    public void removeMember(Club club, Account account) {
        club.removeMember(account);
    }

    public Club getClubToEnroll(String path){
        Club club = clubRepository.findClubOnlyByClubPath(path);
        checkIfExistingClub(path,club);
        return club;
    }

    /* Club 만들기 위한 Test Service */
    public void generateTestClub(Account account) {
        for ( int i = 0; i < 15 ; i++){
            String randomValue = RandomString.make(5);
            Club club = Club.builder()
                    .title("테스트 스터디" + randomValue)
                    .clubPath("test" + randomValue)
                    .shortDescription("테스트용 테스트입니다")
                    .fullDescription("test")
                    .hobby(new ArrayList<>())
                    .managers(new HashSet<>())
                    .build();

            club.publish();
            Club newClub = this.createNewClub(club, account);

            Hobby hobby = hobbyService.findOrCreateHobby("JPA");
            newClub.getHobby().add(hobby);
        }
    }
}
