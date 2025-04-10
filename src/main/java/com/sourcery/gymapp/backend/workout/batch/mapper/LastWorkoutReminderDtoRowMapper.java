package com.sourcery.gymapp.backend.workout.batch.mapper;

import com.sourcery.gymapp.backend.workout.batch.dto.LastUserWorkoutDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.UUID;

public class LastWorkoutReminderDtoRowMapper implements RowMapper<LastUserWorkoutDto> {
    @Override
    public LastUserWorkoutDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        LastUserWorkoutDto dto = new LastUserWorkoutDto();
        dto.setUserId(UUID.fromString(rs.getString("userId")));
        dto.setDateTime(rs.getTimestamp("dateTime").toInstant().atZone(ZoneId.systemDefault()));
        return dto;
    }
}
