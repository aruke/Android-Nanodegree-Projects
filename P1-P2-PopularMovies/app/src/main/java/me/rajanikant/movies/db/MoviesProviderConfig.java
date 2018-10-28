package me.rajanikant.movies.db;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;
import me.rajanikant.movies.BuildConfig;
import me.rajanikant.movies.Constants;

/**
 * Created by : rk
 * Project : UAND-P2
 * Date : 20 May 2016
 */

@SimpleSQLConfig(
        name = Constants.CONTENT_PROVIDER_NAME,
        authority = BuildConfig.APPLICATION_ID,
        database = Constants.DATABASE_NAME,
        version = BuildConfig.VERSION_CODE
)
public class MoviesProviderConfig implements ProviderConfig {

    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }

}
