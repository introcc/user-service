package com.intro.user.service;

import io.grpc.*;

public class Errors {

        static final Metadata.Key<String> ERROR_CODE = Metadata.Key.of("error_code", Metadata.ASCII_STRING_MARSHALLER);

        static final StatusRuntimeException InternalServerError = Status.INTERNAL.asRuntimeException();
        static final StatusRuntimeException Unauthenticated = Status.UNAUTHENTICATED.asRuntimeException();

        static final StatusRuntimeException EmailRequired = newError(Status.INVALID_ARGUMENT, "1001", "Email required");
        static final StatusRuntimeException UsernameRequired = newError(Status.INVALID_ARGUMENT, "1002",
                        "Username required");
        static final StatusRuntimeException PasswordRequired = newError(Status.INVALID_ARGUMENT, "1003",
                        "Password required");
        static final StatusRuntimeException InvalidEmailAddress = newError(Status.INVALID_ARGUMENT, "1004",
                        "Invalid email address");
        static final StatusRuntimeException EmailRegistered = newError(Status.INVALID_ARGUMENT, "1005",
                        "Email already registered");
        static final StatusRuntimeException UsernameRegistered = newError(Status.INVALID_ARGUMENT, "1006",
                        "Username already registered");
        static final StatusRuntimeException InvalidPassword = newError(Status.INVALID_ARGUMENT, "1007",
                        "Password must have at least 8 characters");
        static final StatusRuntimeException InvalidUsername = newError(Status.INVALID_ARGUMENT, "1008",
                        "Username must be less than 32 characters");
        static final StatusRuntimeException EmailOrPasswordIncorrect = newError(Status.INVALID_ARGUMENT, "1009",
                        "Email or password incorrect");
        static final StatusRuntimeException OldPasswordIncorrect = newError(Status.INVALID_ARGUMENT, "1010",
                        "Old password is incorrect ");

        static StatusRuntimeException newError(Status status, String code, String description) {
                Metadata trailers = new Metadata();
                trailers.put(ERROR_CODE, code);
                return status.withDescription(description).asRuntimeException(trailers);
        }
}