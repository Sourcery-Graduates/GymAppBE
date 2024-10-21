package com.sourcery.gymapp.backend.workout;

import com.sourcery.gymapp.backend.factory.RoutineFactory;
import com.sourcery.gymapp.backend.workout.dto.CreateRoutineDto;
import com.sourcery.gymapp.backend.workout.dto.ResponseRoutineDto;
import com.sourcery.gymapp.backend.workout.exception.RoutineNotFoundException;
import com.sourcery.gymapp.backend.workout.exception.UserNotFoundException;
import com.sourcery.gymapp.backend.workout.mapper.RoutineMapper;
import com.sourcery.gymapp.backend.workout.model.Routine;
import com.sourcery.gymapp.backend.workout.repository.RoutineRepository;
import com.sourcery.gymapp.backend.workout.service.RoutineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.AuditorAware;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoutineServiceTest {

    @Mock
    private RoutineRepository routineRepository;

    @Mock
    private RoutineMapper routineMapper;

    @Mock
    private AuditorAware<UUID> auditorAware;

    @InjectMocks
    private RoutineService routineService;

    private UUID userId;
    private UUID routineId;
    private Routine routine;
    private CreateRoutineDto createRoutineDto;
    private ResponseRoutineDto responseRoutineDto;

    @BeforeEach
    void setUp() {
        routine = RoutineFactory.createRoutine();
        routineId = routine.getId();
        userId = routine.getUserId();
        createRoutineDto = RoutineFactory.createRoutineDto();
        responseRoutineDto = RoutineFactory.createResponseRoutineDto();
    }


    @Nested
    @DisplayName("Create Routine Tests")
    public class CreateRoutineTests {

        @Test
        void shouldCreateRoutineSuccessfully() {
            // Arrange
            when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(userId));
            when(routineMapper.toEntity(createRoutineDto, userId)).thenReturn(routine);
            when(routineRepository.save(routine)).thenReturn(routine);
            when(routineMapper.toDto(routine)).thenReturn(responseRoutineDto);

            // Act
            ResponseRoutineDto result = routineService.createRoutine(createRoutineDto);

            // Assert
            assertEquals(responseRoutineDto, result);
            verify(routineRepository, times(1)).save(routine);
        }

        @Test
        void shouldThrowExceptionWhenUserNotAuthenticated() {
            // Arrange
            when(auditorAware.getCurrentAuditor()).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> routineService.createRoutine(createRoutineDto));
        }

    }

    @Nested
    @DisplayName("Get Routine Tests")
    public class GetRoutineTests {

        @Test
        void shouldGetRoutineByIdSuccessfully() {
            // Arrange
            when(routineRepository.findById(routineId)).thenReturn(Optional.of(routine));
            when(routineMapper.toDto(routine)).thenReturn(responseRoutineDto);

            // Act
            ResponseRoutineDto result = routineService.getRoutineById(routineId);

            // Assert
            assertEquals(responseRoutineDto, result);
            verify(routineRepository, times(1)).findById(routineId);
        }

        @Test
        void shouldThrowExceptionWhenRoutineNotFound() {
            // Arrange
            when(routineRepository.findById(routineId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RoutineNotFoundException.class, () -> routineService.getRoutineById(routineId));
        }

        @Test
        void shouldGetRoutinesByUserIdSuccessfully() {
            // Arrange
            List<Routine> routines = List.of(routine);
            when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(userId));
            when(routineRepository.findByUserId(userId)).thenReturn(routines);
            when(routineMapper.toDto(routine)).thenReturn(responseRoutineDto);

            // Act
            List<ResponseRoutineDto> result = routineService.getRoutinesByUserId();

            // Assert
            assertEquals(1, result.size());
            assertEquals(responseRoutineDto, result.getFirst());
            verify(routineRepository, times(1)).findByUserId(userId);
        }
    }

    @Nested
    @DisplayName("Update Routine Tests")
    public class UpdateRoutineTests {

        @Test
        void shouldUpdateRoutineSuccessfully() {
            // Arrange
            when(routineRepository.findById(routineId)).thenReturn(Optional.of(routine));
            doNothing().when(routineMapper).updateEntity(routine, createRoutineDto);
            when(routineRepository.save(routine)).thenReturn(routine);
            when(routineMapper.toDto(routine)).thenReturn(responseRoutineDto);

            // Act
            ResponseRoutineDto result = routineService.updateRoutine(routineId, createRoutineDto);

            // Assert
            assertEquals(responseRoutineDto, result);
            verify(routineRepository, times(1)).save(routine);
        }

        @Test
        void shouldThrowExceptionWhenRoutineNotFound() {
            // Arrange
            when(routineRepository.findById(routineId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RoutineNotFoundException.class, () -> routineService.updateRoutine(routineId, createRoutineDto));
        }
    }

    @Nested
    @DisplayName("Delete Routine Tests")
    public class DeleteRoutineTests {

        @Test
        void shouldDeleteRoutineSuccessfully() {
            // Arrange
            when(routineRepository.findById(routineId)).thenReturn(Optional.of(routine));
            doNothing().when(routineRepository).delete(routine);

            // Act
            routineService.deleteRoutine(routineId);

            // Assert
            verify(routineRepository, times(1)).delete(routine);
        }
    }
}