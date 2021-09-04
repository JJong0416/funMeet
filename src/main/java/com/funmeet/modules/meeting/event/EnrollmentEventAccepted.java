package com.funmeet.modules.meeting.event;

import com.funmeet.modules.meeting.Enrollment;

public class EnrollmentEventAccepted extends EnrollmentEvent{

    public EnrollmentEventAccepted(Enrollment enrollment) {
        super(enrollment, "미팅 참가가 승인되었습니다. 확인하세요.");
    }
}
