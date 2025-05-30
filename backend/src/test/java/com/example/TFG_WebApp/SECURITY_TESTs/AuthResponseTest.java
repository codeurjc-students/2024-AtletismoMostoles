package com.example.TFG_WebApp.SECURITY_TESTs;

import com.example.TFG_WebApp.Security.JWT.AuthResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AuthResponseTest {

    @Test
    void defaultConstructor_and_setters_getters() {
        AuthResponse resp = new AuthResponse();
        // initial state null
        assertThat(resp.getStatus()).isNull();
        assertThat(resp.getMessage()).isNull();
        assertThat(resp.getError()).isNull();

        // set values
        resp.setStatus(AuthResponse.Status.SUCCESS);
        resp.setMessage("All good");
        resp.setError("No error");

        // verify getters
        assertThat(resp.getStatus()).isEqualTo(AuthResponse.Status.SUCCESS);
        assertThat(resp.getMessage()).isEqualTo("All good");
        assertThat(resp.getError()).isEqualTo("No error");
    }

    @Test
    void twoArgConstructor_and_toString_contains_fields() {
        AuthResponse resp = new AuthResponse(AuthResponse.Status.FAILURE, "Oops");
        // verify fields
        assertThat(resp.getStatus()).isEqualTo(AuthResponse.Status.FAILURE);
        assertThat(resp.getMessage()).isEqualTo("Oops");
        assertThat(resp.getError()).isNull();

        // toString should contain values
        String str = resp.toString();
        assertThat(str)
                .contains("status=FAILURE")
                .contains("message=Oops");
    }

    @Test
    void threeArgConstructor_and_toString_includes_error() {
        AuthResponse resp = new AuthResponse(AuthResponse.Status.FAILURE, "Bad thing", "Some error");
        // verify fields
        assertThat(resp.getStatus()).isEqualTo(AuthResponse.Status.FAILURE);
        assertThat(resp.getMessage()).isEqualTo("Bad thing");
        assertThat(resp.getError()).isEqualTo("Some error");

        // toString should include error
        String str = resp.toString();
        assertThat(str)
                .contains("status=FAILURE")
                .contains("message=Bad thing")
                .contains("error=Some error");
    }

    @Test
    void enum_Status_values() {
        // iterate enums
        AuthResponse.Status[] statuses = AuthResponse.Status.values();
        assertThat(statuses).containsExactlyInAnyOrder(
                AuthResponse.Status.SUCCESS,
                AuthResponse.Status.FAILURE
        );
        // name() and ordinal()
        assertThat(AuthResponse.Status.SUCCESS.name()).isEqualTo("SUCCESS");
        assertThat(AuthResponse.Status.FAILURE.ordinal()).isEqualTo(1);
    }
}
