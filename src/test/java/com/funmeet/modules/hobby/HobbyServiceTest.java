package com.funmeet.modules.hobby;

import com.funmeet.infra.MockMvcTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class HobbyServiceTest {

    @InjectMocks
    HobbyService hobbyService;

    @Mock
    HobbyRepository hobbyRepository;

    @Test
    @DisplayName("취미를 찾아보고 없으면 취미를 생성해 DB에 넣는다. - 취미 생성")
    void 취미를_찾아보고_없으면_취미를_생성해서_DB에_넣는다(){
        final String HOBBY_TITLE = "취미1";
        final Hobby hobby = new Hobby(1L, HOBBY_TITLE);
        Mockito.when(hobbyRepository.findByTitle(any())).thenReturn(Optional.of(hobby));

        final Hobby findHobby = hobbyService.findOrCreateHobby(HOBBY_TITLE);

        assertThat(findHobby).isEqualTo(hobby);
    }

    @Test
    @DisplayName("취미를 찾아보고 없으면 취미를 생성해 DB에 넣는다. - 취미반환")
    void 취미를_찾아보고_있으므로_취미를_반환한다(){
        final String HOBBY_TITLE = "취미1";
        Mockito.when(hobbyRepository.save(any())).thenReturn(new Hobby(1L, HOBBY_TITLE));

        final Hobby findHobby = hobbyService.findOrCreateHobby(HOBBY_TITLE);

        assertThat(findHobby.getId()).isEqualTo(findHobby.getId());
        assertThat(findHobby.getTitle()).isEqualTo(findHobby.getTitle());
    }
}
