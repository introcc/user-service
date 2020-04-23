package com.intro.user.service;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.intro.proto.UserSrv;
import com.intro.proto.UserServiceGrpc;
import com.intro.user.entity.*;

import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class UserService extends UserServiceGrpc.UserServiceImplBase {

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public void register(UserSrv.RegisterRequest req, io.grpc.stub.StreamObserver<UserSrv.RegisterReply> observer) {
        if (req.getEmail() == null || req.getEmail().isEmpty()) {
            observer.onError(Errors.EmailRequired);
            return;
        }
        if (req.getEmail().length() > 256) {
            observer.onError(Errors.InvalidEmailAddress);
        }
        if (req.getUsername() == null || req.getUsername().isEmpty()) {
            observer.onError(Errors.UsernameRequired);
            return;
        }
        if (req.getUsername().length() > 32) {
            observer.onError(Errors.InvalidUsername);
            return;
        }
        if (req.getPassword() == null || req.getPassword().isEmpty()) {
            observer.onError(Errors.PasswordRequired);
            return;
        }
        if (req.getPassword().length() < 8) {
            observer.onError(Errors.InvalidPassword);
        }
        logger.info("register: {} {} {}", req.getUsername(), req.getEmail(), req.getPassword());
        try {
            if (this.userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getEmail, req.getEmail())) > 0) {
                observer.onError(Errors.EmailRegistered);
            }
            if (this.userMapper
                    .selectCount(Wrappers.<User>lambdaQuery().eq(User::getUsername, req.getUsername())) > 0) {
                observer.onError(Errors.UsernameRegistered);
            }
            User user = new User();
            user.setEmail(req.getEmail());
            user.setUsername(req.getUsername());
            user.setSalt(RandomStringUtils.random(12, true, true));
            user.setPassword(DigestUtils.md5DigestAsHex((req.getPassword() + user.getSalt()).getBytes()));
            Integer uid = this.userMapper.insert(user);
            String token = JWT.create().withClaim("uid", uid).sign(Algorithm.HMAC256(user.getPassword()));
            UserSrv.RegisterReply reply = UserSrv.RegisterReply.newBuilder().setToken(token).build();
            observer.onNext(reply);
            observer.onCompleted();
        } catch (Exception exception) {
            this.logger.error("register", exception);
            observer.onError(Errors.InternalServerError);
        }
    }

    @Override
    public void authenticate(UserSrv.AuthenticateRequest req,
            io.grpc.stub.StreamObserver<UserSrv.AuthenticateReply> observer) {
        if (req.getEmail() == null || req.getEmail().isEmpty()) {
            observer.onError(Errors.EmailRequired);
            return;
        }
        if (req.getPassword() == null || req.getPassword().isEmpty()) {
            observer.onError(Errors.PasswordRequired);
            return;
        }
        logger.info("authenticate: {} {}", req.getEmail(), req.getPassword());
        try {
            User user = this.userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, req.getEmail()));
            if (user == null || !DigestUtils.md5DigestAsHex((req.getPassword() + user.getSalt()).getBytes())
                    .equals(user.getPassword())) {
                observer.onError(Errors.EmailOrPasswordIncorrect);
            } else {
                String token = JWT.create().withClaim("uid", user.getId()).sign(Algorithm.HMAC256(user.getPassword()));
                UserSrv.AuthenticateReply reply = UserSrv.AuthenticateReply.newBuilder().setToken(token).build();
                observer.onNext(reply);
                observer.onCompleted();
            }
        } catch (Exception exception) {
            this.logger.error("authenticate", exception);
            observer.onError(Errors.InternalServerError);
        }
    }

    @Override
    public void verifyToken(UserSrv.VerifyTokenRequest req,
            io.grpc.stub.StreamObserver<UserSrv.VerifyTokenReply> observer) {
        if (req.getToken() == null || req.getToken().isEmpty()) {
            observer.onError(Errors.Unauthenticated);
        }
        logger.info("verifyToken: {}", req.getToken());
        try {
            Claim uid = JWT.decode(req.getToken()).getClaim("uid");
            if (uid.isNull() || uid.asInt() == null) {
                observer.onError(Errors.Unauthenticated);
            }
            UserSrv.VerifyTokenReply reply = UserSrv.VerifyTokenReply.newBuilder().setId(uid.asInt()).build();
            observer.onNext(reply);
            observer.onCompleted();
        } catch (Exception exception) {
            this.logger.error("verifyToken", exception);
            observer.onError(Errors.InternalServerError);
        }
    }

    @Override
    public void getProfile(UserSrv.GetProfileRequest req,
            io.grpc.stub.StreamObserver<UserSrv.GetProfileReply> observer) {
        logger.info("getProfile: {}", req.getId());
        try {
            User user = this.userMapper.selectOne(Wrappers.<User>lambdaQuery().select(User::getUsername).eq(User::getId, req.getId()));
            UserSrv.GetProfileReply reply = UserSrv.GetProfileReply.newBuilder().setUsername(user.getUsername()).build();
            observer.onNext(reply);
            observer.onCompleted();
        } catch (Exception exception) {
            this.logger.error("verifyToken", exception);
            observer.onError(Errors.InternalServerError);
        }
    }
}