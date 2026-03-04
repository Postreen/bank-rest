package com.example.bankcards.controller.user;

import com.example.bankcards.dto.card.user.BalanceCardResponse;
import com.example.bankcards.dto.card.user.UserCardResponse;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.api.ApiErrorDto;
import com.example.bankcards.security.CurrentUser;
import com.example.bankcards.security.jwt.JwtPrincipal;
import com.example.bankcards.service.user.UserCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "User Cards",
        description = "User operations for viewing cards, checking balances, and requesting card blocking"
)
public class UserCardsController {

    private final UserCardService userCardService;
    private final CurrentUser currentUser;

    @GetMapping
    @Operation(summary = "Get owned cards with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards page returned",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public Page<UserCardResponse> getAllOwnedCards(
            @RequestParam(required = false) CardStatus status,
            @RequestParam(required = false) String last4,
            @ParameterObject Pageable pageable,
            @Parameter(hidden = true) Authentication authentication
    ) {
        JwtPrincipal principal = currentUser.require(authentication);
        log.info("Request start: method=GET endpoint=/cards userId={} username={}",
                principal.userId(), principal.username());

        Page<UserCardResponse> response = userCardService.getAllOwnedCards(principal.userId(), status, last4, pageable);

        log.info("Request end: method=GET endpoint=/cards userId={} username={} result=success",
                principal.userId(), principal.username());
        return response;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get owned card by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card returned",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserCardResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public UserCardResponse getOwnedCard(
            @PathVariable long id,
            @Parameter(hidden = true) Authentication authentication
    ) {
        JwtPrincipal principal = currentUser.require(authentication);
        log.info("Request start: method=GET endpoint=/cards/{} userId={} username={} cardId={}",
                id, principal.userId(), principal.username(), id);

        UserCardResponse response = userCardService.getOwnedCard(id, principal.userId());

        log.info("Request end: method=GET endpoint=/cards/{} userId={} username={} cardId={} result=success",
                id, principal.userId(), principal.username(), id);
        return response;
    }

    @GetMapping("/{id}/balance")
    @Operation(summary = "Get card balance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance returned",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BalanceCardResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public BalanceCardResponse getCardBalance(
            @PathVariable long id,
            @Parameter(hidden = true) Authentication authentication
    ) {
        JwtPrincipal principal = currentUser.require(authentication);
        log.info("Request start:method=GET endpoint=/cards/{}/balance userId={} username={} cardId={}",
                id, principal.userId(), principal.username(), id);

        BalanceCardResponse response = userCardService.getCardBalance(id, principal.userId());

        log.info("Request end:method=GET endpoint=/cards/{}/balance userId={} username={} cardId={} result=success",
                id, principal.userId(), principal.username(), id);
        return response;
    }

    @PostMapping("/{id}/block-request")
    @Operation(summary = "Request card blocking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Block request submitted",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserCardResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "409", description = "Invalid card status / already blocked",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public UserCardResponse requestCardBlock(
            @PathVariable long id,
            @Parameter(hidden = true) Authentication authentication
    ) {
        JwtPrincipal principal = currentUser.require(authentication);
        log.info("Request start: method=POST endpoint=/cards/{}/block-request userId={} username={} cardId={}",
                id, principal.userId(), principal.username(), id);

        UserCardResponse response = userCardService.requestCardBlock(id, principal.userId());

        log.info("Request end: method=POST endpoint=/cards/{}/block-request userId={} username={} cardId={} result=success",
                id, principal.userId(), principal.username(), id);
        return response;
    }
}