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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PointController.class)
class PointControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    PointApplication pointApplication;

    @Test
    @DisplayName("GET /point/{id}: 사용자 point 조회")
    void getPoints() throws Exception {
        given(pointApplication.selectPointByUserId(1)).willReturn(new UserPoint(1, 0, System.currentTimeMillis()));

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

        given(pointApplication.selectAllHistoryByUserId(userId)).willReturn(histories);

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

    @Test
    @DisplayName("PATCH /point/{id}/charge : amount를 충전 후 UserPoint를 반환한다")
    void charge_success() throws Exception {
        // given
        long userId = 5L;
        long amount = 300L;
        UserPoint response = new UserPoint(userId, amount, System.currentTimeMillis());

        given(pointApplication.charge(userId, amount)).willReturn(response);

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(amount)))  // JSON number 바디 (예: 300)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) userId)))
                .andExpect(jsonPath("$.point", is((int) amount)));
    }

    @Test
    @DisplayName("PATCH /point/{id}/use : 요청 바디 숫자(amount)로 포인트 사용 후 UserPoint 반환")
    void use_success() throws Exception {
        // given
        long userId = 1L;
        long amount = 50L;
        UserPoint response = new UserPoint(userId, 950L, System.currentTimeMillis());

        given(pointApplication.use(userId, amount)).willReturn(response);

        // when & then
        mockMvc.perform(patch("/point/{id}/use", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(amount))) // 숫자 그대로 전달 ("50" 아님)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) userId)))
                .andExpect(jsonPath("$.point", is(950)));
    }
}