package com.funmeet.service;



import com.funmeet.domain.Account;
import com.funmeet.domain.Club;
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
        if (club == null) {
            throw new IllegalArgumentException(path + "에 해당하는 모임이 없습니다.");
        }

        return club;
    }

    public Club getClubUpdate(Account account, String path) {
        Club club = this.getClub(path);
        if (!account.isManagerOf(club)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }

        return club;
    }

    public void updateClub_fullDescription(Club club, ClubDescriptionForm clubDescriptionForm) {
        modelMapper.map(clubDescriptionForm, club);
    }
}
