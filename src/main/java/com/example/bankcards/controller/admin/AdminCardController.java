package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.card.admin.CardResponse;
import com.example.bankcards.dto.card.admin.CreateCardRequest;
import com.example.bankcards.dto.card.admin.UpdateCardStatusRequest;
import com.example.bankcards.exception.api.ApiErrorDto;
import com.example.bankcards.service.admin.AdminCardService;
import com.example.bankcards.util.LogAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(
        name = "Admin Cards",
        description = "Admin operations for managing bank cards (create, list, update status, delete)"
)
public class AdminCardController {

    private final AdminCardService adminCardService;

    @GetMapping
    @Operation(summary = "Get all cards (admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards page returned",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public Page<CardResponse> getCards(@ParameterObject Pageable pageable) {
        log.info("Request start: method=GET endpoint=/admin/cards user={}", LogAuthUtil.principal());
        Page<CardResponse> response = adminCardService.getCards(pageable);
        log.info("Request end: method=GET endpoint=/admin/cards user={} result=success", LogAuthUtil.principal());
        return response;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a card for a user (admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error / invalid request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Owner user not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate PAN / conflict",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public CardResponse createCard(@RequestBody @Valid CreateCardRequest request) {
        log.info("Request start: method=POST endpoint=/admin/cards/create user={} ownerId={}",
                LogAuthUtil.principal(), request.ownerId());
        CardResponse response = adminCardService.createCardForOwner(request);
        log.info("Request end: method=POST endpoint=/admin/cards/create user={} cardId={} ownerId={} result=success",
                LogAuthUtil.principal(), response.id(), response.ownerId());
        return response;
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change card status (admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card status updated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error / invalid request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
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
    public CardResponse changeCardStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCardStatusRequest request
    ) {
        log.info("Request start: method=PATCH endpoint=/admin/cards/{}/status user={} cardId={} status={}",
                id, LogAuthUtil.principal(), id, request.status());
        CardResponse response = adminCardService.changeCardStatus(id, request.status());
        log.info("Request end: method=PATCH endpoint=/admin/cards/{}/status user={} cardId={} result=success",
                id, LogAuthUtil.principal(), id);
        return response;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete card by id (admin, hard delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., FK constraint)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteCardById(@PathVariable Long id) {
        log.info("Request start: method=DELETE endpoint=/admin/cards/{} user={} cardId={}",
                id, LogAuthUtil.principal(), id);
        adminCardService.deleteCardById(id);
        log.info("Request end: method=DELETE endpoint=/admin/cards/{} user={} cardId={} result=success",
                id, LogAuthUtil.principal(), id);
    }
}
