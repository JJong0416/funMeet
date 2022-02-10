package com.funmeet.modules.meeting.event;

import com.funmeet.modules.enrollment.Enrollment;

public class EnrollmentEventRejected extends EnrollmentEvent {
    public EnrollmentEventRejected(Enrollment enrollment) {
        super(enrollment, "미팅 참가가 거절되었습니다.");
    }
}
