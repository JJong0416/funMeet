package com.funmeet.modules.meeting.event;

import com.funmeet.modules.enrollment.Enrollment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EnrollmentEvent {
    public final Enrollment enrollment;
    public final String message;
}
