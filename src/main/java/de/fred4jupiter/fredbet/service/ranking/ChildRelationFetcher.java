package de.fred4jupiter.fredbet.service.ranking;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ChildRelationFetcher {

    private static final Logger LOG = LoggerFactory.getLogger(ChildRelationFetcher.class);

    private final AppUserRepository appUserRepository;

    public ChildRelationFetcher(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Cacheable(CacheNames.CHILD_RELATION)
    public Map<String, Boolean> fetchUserIsChildRelation() {
        LOG.info("fetching user child relations from DB...");
        List<AppUser> allUsers = appUserRepository.findAll();
        return allUsers.stream().collect(Collectors.toMap(AppUser::getUsername, AppUser::isChild));
    }

}
