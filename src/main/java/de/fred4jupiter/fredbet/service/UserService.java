package de.fred4jupiter.fredbet.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import de.fred4jupiter.fredbet.FredBetRole;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.web.user.ChangePasswordCommand;
import de.fred4jupiter.fredbet.web.user.UserCommand;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<AppUser> findAll() {
        return appUserRepository.findAll(new Sort(Direction.ASC, "username"));
    }

    public AppUser findByAppUserId(String userId) {
        return appUserRepository.findOne(userId);
    }

    public UserCommand findByUserId(String userId) {
        AppUser appUser = appUserRepository.findOne(userId);
        if (appUser == null) {
            return null;
        }
        UserCommand userCommand = new UserCommand();
        userCommand.setUserId(appUser.getId());
        userCommand.setUsername(appUser.getUsername());
        if (!CollectionUtils.isEmpty(appUser.getAuthorities())) {
            for (GrantedAuthority grantedAuthority : appUser.getAuthorities()) {
                userCommand.addRole(grantedAuthority.getAuthority());
            }
        }

        return userCommand;
    }

    public AppUser updateUser(UserCommand userCommand) {
        Assert.notNull(userCommand.getUserId());
        AppUser appUser = appUserRepository.findOne(userCommand.getUserId());
        appUser.setRoles(userCommand.getRoles());
        updateUser(appUser);
        return appUser;
    }

    public void insertUser(AppUser appUser) throws UserAlreadyExistsException {
        try {
            appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
            appUserRepository.insert(appUser);
        } catch (DuplicateKeyException e) {
            LOG.info("user with username={} still exists. skipping save...", appUser.getUsername());
            throw new UserAlreadyExistsException("User with username=" + appUser.getUsername() + " already exists.");
        }
    }

    public void updateUser(AppUser appUser) {
        AppUser userToBeUpdated = appUserRepository.findOne(appUser.getId());
        if (userToBeUpdated == null) {
            throw new IllegalArgumentException(
                    "Given user with username=" + appUser.getUsername() + " does not exists. ID=" + appUser.getId());
        }

        userToBeUpdated.setRoles(appUser.getRoles());
        appUserRepository.save(appUser);
    }

    public void deleteUser(String userId) {
        AppUser appUser = appUserRepository.findOne(userId);
        if (appUser == null) {
            LOG.info("Could not find user with id={}", userId);
            return;
        }

        if (!appUser.isDeletable()) {
            throw new UserNotDeletableException("Could not delete user with name={}, because its marked as not deletable");
        }

        appUserRepository.delete(userId);
    }

    public void createUser(UserCommand userCommand) {
        // create new user
        AppUser appUser = AppUserBuilder.create().withUsernameAndPassword(userCommand.getUsername(), userCommand.getPassword())
                .withRoles(FredBetRole.ROLE_USER).build();
        insertUser(appUser);
        return;
    }

    public void changePassword(AppUser appUser, ChangePasswordCommand changePasswordCommand) {
        if (appUser == null) {
            throw new IllegalArgumentException("Given user must not be null!");
        }

        final String enteredOldPasswordPlain = changePasswordCommand.getOldPassword();
        final String oldSavedEncryptedPassword = appUser.getPassword();

        if (!passwordEncoder.matches(enteredOldPasswordPlain, oldSavedEncryptedPassword)) {
            throw new OldPasswordWrongException("The old password is wrong!");
        }

        appUser.setPassword(changePasswordCommand.getNewPassword());
        updateUser(appUser);
    }

}
