package com.example.bankcards.controller.user;

import com.example.bankcards.dto.card.user.BalanceCardResponse;
import com.example.bankcards.dto.card.user.UserCardResponse;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.api.ApiErrorDto;
import com.example.bankcards.security.CurrentUser;
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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
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
        long userId = currentUser.require(authentication).userId();
        return userCardService.getAllOwnedCards(userId, status, last4, pageable);
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
        long userId = currentUser.require(authentication).userId();
        return userCardService.getOwnedCard(id, userId);
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
        long userId = currentUser.require(authentication).userId();
        return userCardService.getCardBalance(id, userId);
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
        long userId = currentUser.require(authentication).userId();
        return userCardService.requestCardBlock(id, userId);
    }
}