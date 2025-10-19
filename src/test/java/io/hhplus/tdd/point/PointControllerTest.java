package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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
}