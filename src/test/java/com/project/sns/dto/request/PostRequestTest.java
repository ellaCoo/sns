package com.project.sns.dto.request;

import com.project.sns.dto.HashtagDto;
import com.project.sns.dto.UserAccountDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("DTO - PostRequest")
class PostRequestTest {


    @DisplayName("공백+',' 로 이루어져 있는 해시태그 파싱")
    @MethodSource
    @ParameterizedTest(name = "[{index}] \"{0}\" => {1}")
    void givenHashtagString_whenToDto_thenReturnsParsingUniqueHashtagNames(String input, Set<String> expected) {
        // Given
        PostRequest postRequest = PostRequest.of("title", "content", input);


        // When
        Set<String> actual = postRequest.toDto(createUserAccountDto()).hashtagDtos().stream()
                .map(HashtagDto::hashtagName)
                .collect(Collectors.toSet());

        // Then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    static Stream<Arguments> givenHashtagString_whenToDto_thenReturnsParsingUniqueHashtagNames() {
        return Stream.of(
                arguments("", Set.of()),
                arguments("   ", Set.of()),
                arguments("1", Set.of("1")),
                arguments("1,2", Set.of("1", "2")),
                arguments("1, 2", Set.of("1", "2")),
                arguments("1,2 ", Set.of("1", "2")),
                arguments(" 1, 2 ", Set.of("1", "2"))
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "ella",
                "pw",
                "ella@mail.com",
                "ella",
                "memo"
        );
    }
}