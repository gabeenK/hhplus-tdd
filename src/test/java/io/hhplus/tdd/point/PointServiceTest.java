package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class PointServiceTest {
    @Mock
    UserPointTable userPointTable;

    @InjectMocks
    PointService pointService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("selectById() 호출 시 UserPointTable의 selectById() 결과를 그대로 반환한다")
    void selectById_returnsValueFromTable() {
        // given
        long userId = 1L;
        UserPoint expected = new UserPoint(userId, 1000L, System.currentTimeMillis());
        given(userPointTable.selectById(userId)).willReturn(expected);

        // when
        UserPoint result = pointService.selectById(userId);

        // then
        assertThat(result).isEqualTo(expected);
        then(userPointTable).should(times(1)).selectById(userId);
    }

    @Test
    @DisplayName("insertOrUpdate는 UserPointTable.insertOrUpdate 결과를 그대로 반환한다")
    void insertOrUpdate_delegates_and_returns() {
        // given
        long userId = 3L;
        long amount = 700L;
        UserPoint expected = new UserPoint(userId, amount, System.currentTimeMillis());

        given(userPointTable.insertOrUpdate(userId, amount)).willReturn(expected);

        // when
        UserPoint result = pointService.insertOrUpdate(userId, amount);

        // then
        assertThat(result).isEqualTo(expected);
        then(userPointTable).should(times(1)).insertOrUpdate(eq(userId), eq(amount));
        then(userPointTable).shouldHaveNoMoreInteractions();
    }
}