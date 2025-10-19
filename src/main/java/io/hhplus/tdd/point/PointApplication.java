package io.hhplus.tdd.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PointApplication {
    private final PointService pointService;
    private final PointHistoryService pointHistoryService;

    public UserPoint selectPointByUserId(long id) {
        return pointService.selectById(id);
    }

    public List<PointHistory> selectAllHistoryByUserId(long id) {
        return pointHistoryService.selectAllByUserId(id);
    }

    public UserPoint charge(long id, long amount) {
        pointHistoryService.insertCharge(id, amount);

        UserPoint userPoint = pointService.selectById(id);
        long newAmount = userPoint.point() + amount;
        return pointService.insertOrUpdate(id, newAmount);
    }
}
