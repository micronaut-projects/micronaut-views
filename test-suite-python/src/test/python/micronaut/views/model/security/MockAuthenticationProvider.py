from jakarta.inject import Singleton
from micronaut.http import HttpRequest
from micronaut.security.authentication import AuthenticationRequest, AuthenticationResponse
from micronaut.security.authentication.provider import HttpRequestAuthenticationProvider


@Singleton
class MockAuthenticationProvider(HttpRequestAuthenticationProvider):

    def authenticate(self, httpRequest: HttpRequest | None, authenticationRequest: AuthenticationRequest) -> AuthenticationResponse:
        return AuthenticationResponse.success(authenticationRequest.getIdentity(), {"email": "john@email.com"})
