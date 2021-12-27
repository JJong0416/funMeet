package com.funmeet.modules.mapper;

import com.funmeet.modules.meeting.Meeting;
import com.funmeet.modules.meeting.form.MeetingForm;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetingMapper {

    MeetingMapper INSTANCE = Mappers.getMapper(MeetingMapper.class);

    MeetingForm MeetingToMeetingForm(Meeting meeting);

    Meeting MeetingFormToEntity(MeetingForm meetingForm);

    void updateToExistingEntity(@MappingTarget Meeting meeting, MeetingForm meetingForm);
}
