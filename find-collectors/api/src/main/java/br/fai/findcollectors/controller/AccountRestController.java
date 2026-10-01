package br.fai.findcollectors.controller;

import br.fai.findcollectors.dto.request.AuthRequest;
import br.fai.findcollectors.dto.request.CreatePersonRequest;
import br.fai.findcollectors.dto.request.RefreshTokenRequest;
import br.fai.findcollectors.dto.response.AuthResponse;
import br.fai.findcollectors.dto.response.PersonResponse;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.exceptions.InvalidTokenException;
import br.fai.findcollectors.mapper.PersonMapper;
import br.fai.findcollectors.security.JwtTokenService;
import br.fai.findcollectors.usecases.auth.AuthenticateUserUseCase;
import br.fai.findcollectors.usecases.person.CreatePersonUseCase;
import br.fai.findcollectors.usecases.person.PersonQueryUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AccountRestController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final CreatePersonUseCase createPersonUseCase;
    private final PersonQueryUseCase personQueryUseCase;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/login")
    @SecurityRequirements
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest account) {

        Person person = authenticateUserUseCase.execute(account.email(), account.password());
        return ResponseEntity.ok(jwtTokenService.issueTokens(person));
    }

    @PostMapping("/refresh")
    @SecurityRequirements
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {

        String email = jwtTokenService.subjectFromRefreshToken(request.refreshToken());
        Person person = findTokenOwner(email);

        return ResponseEntity.ok(jwtTokenService.issueTokens(person));
    }

    @PostMapping("/signup")
    @SecurityRequirements
    public ResponseEntity<PersonResponse> signUp(@RequestBody @Valid CreatePersonRequest personRequest) {
        
        Person created = createPersonUseCase.execute(PersonMapper.toEntity(personRequest));

        PersonResponse response = PersonMapper.toResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<PersonResponse> me(@AuthenticationPrincipal Jwt jwt) {

        Person person = findTokenOwner(jwt.getSubject());
        return ResponseEntity.ok(PersonMapper.toResponse(person));
    }

    private Person findTokenOwner(String email) {
        Person person = personQueryUseCase.findPersonByEmail(email);
        if (person == null) {
            throw new InvalidTokenException("Token owner no longer exists");
        }
        return person;
    }
}
