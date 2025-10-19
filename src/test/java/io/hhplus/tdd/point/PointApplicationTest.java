package io.hhplus.tdd.point;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

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

    @Test
    @DisplayName("charge: 기존 잔고에 amount를 더해 insertOrUpdate(id, newAmount)로 저장한다")
    void charge_adds_to_existing_balance_and_persists() {
        // given
        long userId = 7L;
        long before = 700L;
        long amount = 300L;
        long expectedNewAmount = before + amount; // 1000

        UserPoint current = new UserPoint(userId, before, System.currentTimeMillis());
        UserPoint persisted = new UserPoint(userId, expectedNewAmount, System.currentTimeMillis());

        given(pointService.selectById(userId)).willReturn(current);
        given(pointService.insertOrUpdate(userId, expectedNewAmount)).willReturn(persisted);

        // when
        UserPoint result = pointApplication.charge(userId, amount);

        // then
        assertThat(result).isEqualTo(persisted);

        // 호출 순서 및 파라미터 검증
        InOrder inOrder = Mockito.inOrder(pointHistoryService, pointService);
        inOrder.verify(pointHistoryService).insertCharge(userId, amount);
        inOrder.verify(pointService).selectById(userId);
        inOrder.verify(pointService).insertOrUpdate(userId, expectedNewAmount);

        then(pointHistoryService).shouldHaveNoMoreInteractions();
        then(pointService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("charge: 기존 잔고가 0일 때 amount만큼 더해 저장한다")
    void charge_when_zero_balance() {
        // given
        long userId = 1L;
        long before = 0L;
        long amount = 500L;
        long expectedNewAmount = before + amount; // 500

        UserPoint current = new UserPoint(userId, before, System.currentTimeMillis());
        UserPoint persisted = new UserPoint(userId, expectedNewAmount, System.currentTimeMillis());

        given(pointService.selectById(userId)).willReturn(current);
        given(pointService.insertOrUpdate(userId, expectedNewAmount)).willReturn(persisted);

        // when
        UserPoint result = pointApplication.charge(userId, amount);

        // then
        assertThat(result.point()).isEqualTo(expectedNewAmount);

        InOrder inOrder = Mockito.inOrder(pointHistoryService, pointService);
        inOrder.verify(pointHistoryService).insertCharge(userId, amount);
        inOrder.verify(pointService).selectById(userId);
        inOrder.verify(pointService).insertOrUpdate(userId, expectedNewAmount);

        then(pointHistoryService).shouldHaveNoMoreInteractions();
        then(pointService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("use: 기존 잔고에서 amount를 빼서 insertOrUpdate(id, newAmount)로 저장한다")
    void use_subtracts_from_existing_balance_and_persists() {
        // given
        long userId = 7L;
        long before = 1000L;
        long amount = 300L;
        long expectedNewAmount = before - amount; // 700

        UserPoint current = new UserPoint(userId, before, System.currentTimeMillis());
        UserPoint persisted = new UserPoint(userId, expectedNewAmount, System.currentTimeMillis());

        given(pointService.selectById(userId)).willReturn(current);
        given(pointService.insertOrUpdate(userId, expectedNewAmount)).willReturn(persisted);

        // when
        UserPoint result = pointApplication.use(userId, amount);

        // then
        assertThat(result).isEqualTo(persisted);

        // 호출 순서 및 파라미터 검증
        InOrder inOrder = Mockito.inOrder(pointHistoryService, pointService);
        inOrder.verify(pointHistoryService).insertUse(userId, amount);
        inOrder.verify(pointService).selectById(userId);
        inOrder.verify(pointService).insertOrUpdate(userId, expectedNewAmount);

        then(pointHistoryService).shouldHaveNoMoreInteractions();
        then(pointService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("use: 기존 잔고가 amount와 같으면 0으로 저장된다")
    void use_when_equal_balance_becomes_zero() {
        // given
        long userId = 1L;
        long before = 500L;
        long amount = 500L;
        long expectedNewAmount = 0L;

        UserPoint current = new UserPoint(userId, before, System.currentTimeMillis());
        UserPoint persisted = new UserPoint(userId, expectedNewAmount, System.currentTimeMillis());

        given(pointService.selectById(userId)).willReturn(current);
        given(pointService.insertOrUpdate(userId, expectedNewAmount)).willReturn(persisted);

        // when
        UserPoint result = pointApplication.use(userId, amount);

        // then
        assertThat(result.point()).isEqualTo(expectedNewAmount);

        InOrder inOrder = Mockito.inOrder(pointHistoryService, pointService);
        inOrder.verify(pointHistoryService).insertUse(userId, amount);
        inOrder.verify(pointService).selectById(userId);
        inOrder.verify(pointService).insertOrUpdate(userId, expectedNewAmount);

        then(pointHistoryService).shouldHaveNoMoreInteractions();
        then(pointService).shouldHaveNoMoreInteractions();
    }
}