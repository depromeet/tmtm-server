package net.teumteum.user.integration;

import java.util.List;
import net.teumteum.core.error.ErrorResponse;
import net.teumteum.user.domain.response.UserGetResponse;
import net.teumteum.user.domain.response.UsersGetByIdResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("유저 통합테스트의")
class UserIntegrationTest extends IntegrationTest {

    private static final String VALID_TOKEN = "VALID_TOKEN";
    private static final String INVALID_TOKEN = "IN_VALID_TOKEN";

    @Nested
    @DisplayName("유저 조회 API는")
    class Find_user_api {

        @Test
        @DisplayName("존재하는 유저의 id가 주어지면, 유저 정보를 응답한다.")
        void Return_user_info_if_exist_user_id_received() {
            // given
            var user = repository.saveAndGetUser();
            var expected = UserGetResponse.of(user);

            // when
            var result = api.getUser(VALID_TOKEN, user.getId());

            // then
            Assertions.assertThat(
                    result.expectStatus().isOk()
                        .expectBody(UserGetResponse.class)
                        .returnResult().getResponseBody())
                .usingRecursiveComparison()
                .isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 유저의 id가 주어지면, 400 Bad Request를 응답한다.")
        void Return_400_bad_request_if_not_exists_user_id_received() {
            // given
            var notExistUserId = 1L;

            // when
            var result = api.getUser(VALID_TOKEN, notExistUserId);

            // then
            result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class);
        }
    }

    @Nested
    @DisplayName("유저 id들로 유저들을 조회하는 API 는")
    class Find_users_by_user_ids_api {

        @Test
        @DisplayName("존재하는 유저의 id들로만 요청이 들어오면, 유저 정보를 응답한다.")
        void Return_user_info_if_exist_user_ids_received() {
            // given
            var user1 = repository.saveAndGetUser();
            var user2 = repository.saveAndGetUser();

            var expected = UsersGetByIdResponse.of(List.of(user1, user2));

            // when
            var result = api.getUsersById(VALID_TOKEN, user1.getId() + "," + user2.getId());

            // then
            Assertions.assertThat(result.expectStatus().isOk()
                .expectBody(UsersGetByIdResponse.class)
                .returnResult()
                .getResponseBody()
            ).usingRecursiveComparison().isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 유저의 id가 포함되어 있다면, 400 Bad Request 를 응답한다.")
        void Return_400_bad_request_if_not_exists_user_id_include() {
            // given
            var exist = repository.saveAndGetUser();
            var notExist = 999L;

            // when
            var result = api.getUsersById(VALID_TOKEN, exist.getId() + "," + notExist);

            // then
            result.expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("id가 비어있으면, 400 Bad Request 를 응답한다.")
        void Return_400_bad_request_if_empty_user_ids_input() {
            // when
            var result = api.getUsersById(VALID_TOKEN, "");

            // then
            result.expectStatus().isBadRequest();
        }
    }
}
