package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static io.hhplus.tdd.point.TransactionType.USE;

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

    public void insertUse(
            long userId,
            long amount
    ) {
        this.insert(userId, amount, USE);
    }

    void insert(
            long userId,
            long amount,
            TransactionType type
    ) {
        pointHistoryTable.insert(userId, amount, type, System.currentTimeMillis());
    }
}
