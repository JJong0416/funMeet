package com.funmeet.service;

import com.funmeet.domain.Account;
import com.funmeet.domain.City;
import com.funmeet.domain.Club;
import com.funmeet.domain.Hobby;
import com.funmeet.form.ClubDescriptionForm;
import com.funmeet.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ModelMapper modelMapper;

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

    public void updateClub_fullDescription(Club club, ClubDescriptionForm clubDescriptionForm) {
        modelMapper.map(clubDescriptionForm, club);
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
        Club club = clubRepository.findByClubPath(path); // 여기 부분 시간효율 추가
        checkIfExistingClub(path, club);
        checkIfManager(account, club);
        return club;
    }

    public Club getClubUpdateCity(Account account, String path) {
        Club club = clubRepository.findByClubPath(path); // 여기 부분도!
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
        if (!account.isManagerOf(club)) {
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
    }

    public void close(Club club) {
        club.close();
    }

    public void startRecruit(Club club) {
        club.startRecruit();
    }

    public void stopRecruit(Club club) {
        club.stopRecruit();
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
        if (club.isRemovable()) {
            clubRepository.delete(club);
        } else {
            throw new IllegalArgumentException("클럽을 삭제할 수 없습니다.");
        }
    }
    public void addMember(Club club, Account account) {
        club.addMember(account);
    }

    public void removeMember(Club club, Account account) {
        club.removeMember(account);
    }   
}
