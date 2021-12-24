package com.funmeet.modules.club;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.city.City;
import com.funmeet.modules.club.event.ClubCreatedEvent;
import com.funmeet.modules.club.event.ClubUpdateEvent;
import com.funmeet.modules.club.form.ClubDescriptionForm;
import com.funmeet.modules.hobby.Hobby;
import com.funmeet.modules.hobby.HobbyRepository;
import com.funmeet.modules.hobby.HobbyService;
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

    public Club getClubOnlyByPath(String path){
        return clubRepository.findByClubPath(path);
    }

    public void updateClubDescription(Club club, ClubDescriptionForm clubDescriptionForm) {
        club.updateClubIntroduce(clubDescriptionForm.getShortDescription(), clubDescriptionForm.getFullDescription());
        applicationEventPublisher.publishEvent(new ClubUpdateEvent(club,"모임 소개를 수정했습니다."));
    }

    public Hobby findHobbyByTitle(String title){
        return hobbyRepository.findByTitle(title).orElseThrow();
    }

    public List<String> getAllHobbyTitles(){
        return hobbyRepository.findAll().stream()
                .map(Hobby::getTitle).collect(Collectors.toList());
    }

    public void updateClubImage(Club club, String image) {
        club.updateBannerImage(image);
    }

    public void enableClubBanner(Club club) {
        club.updateClubBanner(true);
    }

    public void disableClubBanner(Club club) {
        club.updateClubBanner(false);
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
        Club club = clubRepository.findClubWithManagersByClubPath(path);
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
        applicationEventPublisher.publishEvent(new ClubCreatedEvent(club));

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
        club.updateClubPath(newPath);
    }

    public boolean isValidTitle(String newTitle) { // TODO validation new Title
        return newTitle.length() <= 30;
    }

    public void updateClubTitle(Club club, String newTitle) {
        club.updateClubTitle(newTitle);
    }

    public void remove(Club club) {
        clubRepository.delete(club);
    }

    public Club addMember(Account account, String path) {
        Club club = clubRepository.findClubWithMembersByClubPath(path);
        club.addMember(account);
        return club;
    }

    public Club removeMember(Account account,String path) {
        Club club = clubRepository.findClubWithMembersByClubPath(path);
        club.removeMember(account);
        return club;
    }

    public Club getClubToEnroll(String path){
        Club club = clubRepository.findClubOnlyByClubPath(path);
        checkIfExistingClub(path,club);
        return club;
    }
}
