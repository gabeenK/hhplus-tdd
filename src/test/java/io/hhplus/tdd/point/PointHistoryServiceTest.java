package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class PointHistoryServiceTest {
    @Mock
    PointHistoryTable pointHistoryTable;

    @InjectMocks
    PointHistoryService pointHistoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("selectAllByUserId() 호출 시 PointHistoryTable의 결과를 그대로 반환한다")
    void selectAllByUserId_returnsValueFromTable() {
        // given
        long userId = 1L;
        List<PointHistory> expected = List.of(
                new PointHistory(1L, userId, 100L, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, userId, -50L, TransactionType.USE, System.currentTimeMillis())
        );

        given(pointHistoryTable.selectAllByUserId(userId)).willReturn(expected);

        // when
        List<PointHistory> result = pointHistoryService.selectAllByUserId(userId);

        // then
        assertThat(result).isEqualTo(expected);
        then(pointHistoryTable).should(times(1)).selectAllByUserId(userId);
    }

    @Test
    @DisplayName("insertCharge는 type을 CHARGE로 고정하여 insert를 호출한다")
    void insertCharge_calls_insert_with_charge_type() {
        // given
        long userId = 7L;
        long amount = 1000L;

        // when
        pointHistoryService.insertCharge(userId, amount);

        // then
        then(pointHistoryTable).should(times(1))
                .insert(eq(userId), eq(amount), eq(TransactionType.CHARGE), anyLong());
        then(pointHistoryTable).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("insert는 전달받은 type(USE 등) 그대로 insert를 호출한다")
    void insert_calls_table_with_given_type() {
        // given
        long userId = 9L;
        long amount = 300L;
        TransactionType type = TransactionType.USE;

        // when
        pointHistoryService.insert(userId, amount, type);

        // then
        then(pointHistoryTable).should(times(1))
                .insert(eq(userId), eq(amount), eq(TransactionType.USE), anyLong());
        then(pointHistoryTable).shouldHaveNoMoreInteractions();
    }
}