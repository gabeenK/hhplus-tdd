package io.hhplus.tdd.point;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class PointApplicationTest {
    @Mock
    private PointService pointService;

    @Mock
    private PointHistoryService pointHistoryService;

    @InjectMocks
    private PointApplication pointApplication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("selectPointByUserId는 PointService.selectById 결과를 그대로 반환한다")
    void selectPointByUserId_delegates_to_service() {
        // given
        long userId = 10L;
        UserPoint expected = new UserPoint(userId, 1_000L, System.currentTimeMillis());
        given(pointService.selectById(userId)).willReturn(expected);

        // when
        UserPoint result = pointApplication.selectPointByUserId(userId);

        // then
        assertThat(result).isEqualTo(expected);
        then(pointService).should(times(1)).selectById(userId);
    }

    @Test
    @DisplayName("selectAllHistoryByUserId는 PointHistoryService.selectAllByUserId 결과를 그대로 반환한다")
    void selectAllHistoryByUserId_delegates_to_historyService() {
        // given
        long userId = 7L;
        List<PointHistory> expected = List.of(
                new PointHistory(1L, userId, 500L, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, userId, -200L, TransactionType.USE, System.currentTimeMillis())
        );
        given(pointHistoryService.selectAllByUserId(userId)).willReturn(expected);

        // when
        List<PointHistory> result = pointApplication.selectAllHistoryByUserId(userId);

        // then
        assertThat(result).isEqualTo(expected);
        then(pointHistoryService).should(times(1)).selectAllByUserId(userId);
    }
}