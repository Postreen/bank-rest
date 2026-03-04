package com.example.bankcards.controller.transfer;

import com.example.bankcards.dto.transfer.TransferRequest;
import com.example.bankcards.dto.transfer.TransferResponse;
import com.example.bankcards.exception.api.ApiErrorDto;
import com.example.bankcards.security.CurrentUser;
import com.example.bankcards.security.jwt.JwtPrincipal;
import com.example.bankcards.service.transfer.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Transfers",
        description = "Money transfers between user's cards"
)
public class TransferController {
    private final TransferService transferService;
    private final CurrentUser currentUser;

    @PostMapping
    @Operation(summary = "Transfer money between cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer completed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TransferResponse.class))),
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
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., insufficient funds, invalid card status)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    public TransferResponse transfer(
            @RequestBody @Valid TransferRequest transferRequest,
            @Parameter(hidden = true) Authentication authentication
    ) {
        JwtPrincipal principal = currentUser.require(authentication);

        log.info("Request start: method=POST endpoint=/transfers userId={} username={} fromCardId={} toCardId={}",
                principal.userId(), principal.username(), transferRequest.fromCardId(), transferRequest.toCardId());

        TransferResponse response = transferService.transfer(principal.userId(), transferRequest);

        log.info("Request end: method=POST endpoint=/transfers userId={} username={} fromCardId={} toCardId={} result=success",
                principal.userId(), principal.username(), transferRequest.fromCardId(), transferRequest.toCardId());
        return response;
    }
}
