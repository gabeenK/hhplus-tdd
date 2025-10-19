package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.hhplus.tdd.point.TransactionType.CHARGE;

@Service
@RequiredArgsConstructor
public class PointHistoryService {
    private final PointHistoryTable pointHistoryTable;

    public List<PointHistory> selectAllByUserId(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    public void insertCharge(
            long userId,
            long amount
    ) {
        this.insert(userId, amount, CHARGE);
    }

    void insert(
            long userId,
            long amount,
            TransactionType type
    ) {
        pointHistoryTable.insert(userId, amount, type, System.currentTimeMillis());
    }
}
