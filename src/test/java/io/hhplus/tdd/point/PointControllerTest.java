package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PointController.class)
class PointControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    PointService pointService;

    @MockBean
    PointHistoryService pointHistoryService;

    @Test
    @DisplayName("GET /point/{id}: 사용자 point 조회")
    void getPoints() throws Exception {
        given(pointService.selectById(1)).willReturn(new UserPoint(1, 0, System.currentTimeMillis()));

        mockMvc.perform(get("/point/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.point", is(0)));
    }

    @Test
    @DisplayName("GET /point/{id}/histories : 특정 유저의 포인트 내역을 반환한다")
    void getHistories() throws Exception {
        // given
        int userId = 1;
        List<PointHistory> histories = List.of(
                new PointHistory(1L, userId, 500L, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, userId, -200L, TransactionType.USE, System.currentTimeMillis())
        );

        given(pointHistoryService.selectAllByUserId(userId)).willReturn(histories);

        // when & then
        mockMvc.perform(get("/point/{id}/histories", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].userId", is(userId)))
                .andExpect(jsonPath("$[0].amount", is(500)))
                .andExpect(jsonPath("$[0].type", is("CHARGE")))
                .andExpect(jsonPath("$[1].type", is("USE")));
    }
}