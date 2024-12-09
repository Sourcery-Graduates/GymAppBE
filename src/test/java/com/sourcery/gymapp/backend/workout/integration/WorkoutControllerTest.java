package com.sourcery.gymapp.backend.workout.integration;

import com.sourcery.gymapp.backend.workout.dto.ResponseWorkoutDto;
import com.sourcery.gymapp.backend.workout.dto.ResponseWorkoutGridGroupedByDate;
import com.sourcery.gymapp.backend.workout.exception.ErrorCode;
import com.sourcery.gymapp.backend.workout.exception.ErrorResponse;
import com.sourcery.gymapp.backend.workout.factory.WorkoutFactory;
import com.sourcery.gymapp.backend.workout.mapper.WorkoutMapper;
import com.sourcery.gymapp.backend.workout.model.Workout;
import com.sourcery.gymapp.backend.workout.repository.WorkoutRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class WorkoutControllerTest extends BaseWorkoutIntegrationTest {

    @Autowired
    WorkoutRepository workoutRepository;

    @Autowired
    WorkoutMapper workoutMapper;

    @Nested
    @DisplayName("getWorkoutGridByDate endpoint")
    public class getWorkoutGridByDate {

        ZonedDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0).atZone(ZoneId.of("UTC"));
        ZonedDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59).atZone(ZoneId.of("UTC"));

        Workout workout1 = WorkoutFactory.createWorkout(
                "workout1",
                LocalDateTime.of(2024, 1, 22, 13, 14)
                .atZone(ZoneOffset.UTC), "workout1");
        Workout workout2 = WorkoutFactory.createWorkout(
                "workout2",
                LocalDateTime.of(2024, 1, 22, 15, 55)
                .atZone(ZoneOffset.UTC), "workout2");
        Workout workout3 = WorkoutFactory.createWorkout(
                "workout3",
                LocalDateTime.of(2024, 1, 31, 23, 59)
                .atZone(ZoneOffset.UTC), "workout3");

        Workout workout4 = WorkoutFactory.createWorkout(
                "workout4",
                LocalDateTime.of(2024, 2, 1, 0, 0)
                        .atZone(ZoneOffset.UTC), "workout4");

        ResponseWorkoutDto workoutDto1 = workoutMapper.toDto(workout1);
        ResponseWorkoutDto workoutDto2 = workoutMapper.toDto(workout2);
        ResponseWorkoutDto workoutDto3 = workoutMapper.toDto(workout3);

        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        DateTimeFormatter dateWithoutTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        @BeforeEach
        void setUpWorkouts() {
            workout1.setUserId(userId);
            workout2.setUserId(userId);
            workout3.setUserId(userId);
            workout4.setUserId(userId);

            workoutRepository.saveAll(List.of(workout1, workout2, workout3, workout4));
        }

        @Test
        void givenMissingAuthenticationHeader_shouldReturnUnauthorized() {
            unauthorizedCallGetWorkoutGridByDate(startDate.toString(), endDate.toString())
                    .expectStatus().isUnauthorized();
        }

        @Test
        void givenInvalidStartDateFormat_shouldReturnMethodArgumentTypeMismatchException() {
            ErrorResponse responseBody = callGetWorkoutGridByDate("123", endDate.toString())
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .returnResult().getResponseBody();;

            assertThat(responseBody).isNotNull();

            assertThat(responseBody.code()).isEqualTo(ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH);
            assertThat(responseBody.message()).contains("startDate");
        }

        @Test
        void givenInvalidEndDateFormat_shouldReturnMethodArgumentTypeMismatchException() {
            ErrorResponse responseBody = callGetWorkoutGridByDate(startDate.toString(), "123")
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .returnResult().getResponseBody();

            assertThat(responseBody).isNotNull();

            assertThat(responseBody.code()).isEqualTo(ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH);
            assertThat(responseBody.message()).contains("endDate");
        }


        @Test
        void givenValidDateSlot_shouldReturnWorkoutGrid() {
            ResponseWorkoutGridGroupedByDate responseBody = callGetWorkoutGridByDate(startDate.toString(), endDate.toString())
                    .expectStatus().is2xxSuccessful()
                            .expectBody(ResponseWorkoutGridGroupedByDate.class)
                                    .returnResult().getResponseBody();
            assertThat(responseBody).isNotNull();

            assertEquals(2, responseBody.workouts().size());
            assertTrue(responseBody.workouts().containsKey(workout1.getDate().format(dateWithoutTimeFormatter)));
            assertTrue(responseBody.workouts().containsKey(workout3.getDate().format(dateWithoutTimeFormatter)));
            ResponseWorkoutDto firstResponseWorkoutDto = responseBody.workouts().get(workout3.getDate().format(dateWithoutTimeFormatter)).get(0);
            assertAll(
                    () -> assertEquals(workoutDto3.name(), firstResponseWorkoutDto.name()),
                    () -> assertEquals(workoutDto3.date(), firstResponseWorkoutDto.date())
            );

        }

        @Test
        void givenValidDateSlot_shouldReturnWorkoutGridGroupedByDate() {
            ResponseWorkoutGridGroupedByDate responseBody = callGetWorkoutGridByDate(startDate.toString(), endDate.toString())
                    .expectStatus().is2xxSuccessful()
                    .expectBody(ResponseWorkoutGridGroupedByDate.class)
                    .returnResult().getResponseBody();
            assertThat(responseBody).isNotNull();

            List<ResponseWorkoutDto> workouts = responseBody.workouts().get(workout1.getDate().format(dateWithoutTimeFormatter));
            ResponseWorkoutDto firstResponseWorkoutDto = workouts.get(0);
            ResponseWorkoutDto secondResponseWorkoutDto = workouts.get(1);

            assertEquals(2, workouts.size());

            assertAll(
                    () -> assertEquals(workoutDto1.name(), firstResponseWorkoutDto.name()),
                    () -> assertEquals(workoutDto1.date(), firstResponseWorkoutDto.date())
            );

            assertAll(
                    () -> assertEquals(workoutDto2.name(), secondResponseWorkoutDto.name()),
                    () -> assertEquals(workoutDto2.date(), secondResponseWorkoutDto.date())
            );
        }

        @Test
        void givenDateSlotWithoutWorkouts_shouldReturnEmptyWorkoutGrid(){
            ResponseWorkoutGridGroupedByDate responseBody = callGetWorkoutGridByDate(
                    LocalDateTime.of(1970, 1, 1, 0, 0).atZone(ZoneId.of("UTC")).toString(),
                    LocalDateTime.of(1970, 1, 31, 23, 59).atZone(ZoneId.of("UTC")).toString())
                    .expectStatus().is2xxSuccessful()
                    .expectBody(ResponseWorkoutGridGroupedByDate.class)
                    .returnResult().getResponseBody();
            assertThat(responseBody).isNotNull();
            assertEquals(0, responseBody.workouts().size());
        }
    }

    private WebTestClient.ResponseSpec callGetWorkoutGridByDate(String startDate, String endDate) {
        return webTestClient.get().uri(
                uriBuilder -> uriBuilder
                        .path("/api/workout/workout/date")
                        .queryParam("startDate", startDate)
                        .queryParam("endDate", endDate)
                        .build()
                )
                .header("Authorization", "Bearer " + jwtToken)
                .exchange();
    }

    private WebTestClient.ResponseSpec unauthorizedCallGetWorkoutGridByDate(String startDate, String endDate) {
        return webTestClient.get().uri(
                        uriBuilder -> uriBuilder
                                .path("/api/workout/workout/date")
                                .queryParam("startDate", startDate)
                                .queryParam("endDate", endDate)
                                .build()
                )
                .exchange();
    }
}
