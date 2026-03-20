package de.fred4jupiter.fredbet.admin;

import de.fred4jupiter.fredbet.common.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

@UnitTest
class CacheAdministrationServiceUT {

    private CacheManager cacheManager;
    private CacheAdministrationService service;

    @BeforeEach
    void setUp() {
        cacheManager = mock(CacheManager.class);
        service = new CacheAdministrationService(cacheManager);
    }

    @Test
    void clearCaches_withCaches_shouldClearEachCache() {
        when(cacheManager.getCacheNames()).thenReturn(Arrays.asList("cache1", "cache2"));

        Cache cache1 = mock(Cache.class);
        Cache cache2 = mock(Cache.class);

        when(cacheManager.getCache("cache1")).thenReturn(cache1);
        when(cacheManager.getCache("cache2")).thenReturn(cache2);

        service.clearCaches();

        verify(cache1).clear();
        verify(cache2).clear();
    }

    @Test
    void clearCaches_noCaches_shouldNotThrow() {
        when(cacheManager.getCacheNames()).thenReturn(Collections.emptyList());
        service.clearCaches();
        // No exception expected, no further verification needed
    }

    @Test
    void clearCacheByCacheName_cacheExists_shouldClearCache() {
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("testCache")).thenReturn(cache);

        service.clearCacheByCacheName("testCache");

        verify(cache).clear();
    }

    @Test
    void clearCacheByCacheName_cacheDoesNotExist_shouldNotThrow() {
        when(cacheManager.getCache("unknown")).thenReturn(null);
        service.clearCacheByCacheName("unknown");
        // No exception expected, no further verification needed
    }
}
