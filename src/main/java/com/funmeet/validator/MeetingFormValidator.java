package com.funmeet.validator;

import com.funmeet.domain.Meeting;
import com.funmeet.form.MeetingForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class MeetingFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return MeetingForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MeetingForm meetingForm = (MeetingForm) target;

        if (isNotValidEndDateTime(meetingForm)) {
            errors.rejectValue("endDateTime", "wrong.datetime", "모임 종료 일시를 정확히 입력하세요.");
        }

        if (isNotValidStartDateTime(meetingForm)) {
            errors.rejectValue("startDateTime", "wrong.datetime", "모임 시작 일시를 정확히 입력하세요.");
        }
    }

    private boolean isNotValidStartDateTime(MeetingForm meetingForm){
        return meetingForm.getStartDateTime().isBefore(meetingForm.getEndDateTime());
    }

    private boolean isNotValidEndDateTime(MeetingForm meetingForm){
        LocalDateTime endDateTime = meetingForm.getEndDateTime();
        return endDateTime.isAfter(meetingForm.getStartDateTime());
    }

    public void validateUpdateForm(MeetingForm meetingForm, Meeting meeting, Errors errors) {
        if (meetingForm.getLimitOfEnrollments() < meeting.getNumberOfAcceptedEnrollments()) {
            errors.rejectValue("limitOfEnrollments", "wrong.value", "확인된 참기 신청보다 모집 인원 수가 커야 합니다.");
        }
    }
}
