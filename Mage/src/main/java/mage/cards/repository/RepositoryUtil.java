package mage.cards.repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import mage.util.DebugUtil;
import mage.util.JarVersion;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

/**
 * @author North, JayDi85
 */
public final class RepositoryUtil {

    private static final Logger logger = Logger.getLogger(RepositoryUtil.class);
    public static final boolean CARD_DB_RECREATE_BY_CLIENT_SIDE = true; // re-creates db from client (best performance) or downloads from server on connects (can be slow)

    public static void bootstrapLocalDb() {
        // call local db to init all sets and cards/tokens repository (need for correct updates cycle, not on random request)
        logger.info("Loading database...");
        ExpansionRepository.instance.getContentVersionConstant();
        CardRepository.instance.getContentVersionConstant();
        TokenRepository.instance.getAll().size();

        // stats
        int totalSets = ExpansionRepository.instance.getAll().size();
        int totalCards = CardRepository.instance.findCards(new CardCriteria().nightCard(false)).size()
                + CardRepository.instance.findCards(new CardCriteria().nightCard(true)).size();
        logger.info("Database stats:");
        logger.info(" - sets: " + (totalSets == 0 ? "updating" : totalSets));
        logger.info(" - cards: " + (totalCards == 0 ? "updating" : totalCards));
        logger.info(" - tokens: " + TokenRepository.instance.getByType(TokenType.TOKEN).size());
        logger.info(" - emblems: " + TokenRepository.instance.getByType(TokenType.EMBLEM).size());
        logger.info(" - planes: " + TokenRepository.instance.getByType(TokenType.PLANE).size());
        logger.info(" - dungeons: " + TokenRepository.instance.getByType(TokenType.DUNGEON).size());

        if (DebugUtil.DATABASE_SHOW_CACHE_AND_MEMORY_STATS_ON_STARTUP) {
            CardRepository.instance.printDatabaseStats("on startup");
        }
    }

    public static boolean isDatabaseObsolete(ConnectionSource connectionSource, String entityName, long version) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DatabaseVersion.class);
        Dao<DatabaseVersion, Object> versionDao = DaoManager.createDao(connectionSource, DatabaseVersion.class);

        QueryBuilder<DatabaseVersion, Object> queryBuilder = versionDao.queryBuilder();
        queryBuilder.where().eq("entity", new SelectArg(entityName))
                .and().eq("version", new SelectArg(version));
        List<DatabaseVersion> dbVersions = versionDao.query(queryBuilder.prepare());

        if (dbVersions.isEmpty()) {
            DatabaseVersion dbVersion = new DatabaseVersion();
            dbVersion.setEntity(entityName);
            dbVersion.setVersion(version);
            versionDao.create(dbVersion);
        }
        return dbVersions.isEmpty();
    }

    public static boolean isNewBuildRun(ConnectionSource connectionSource, String entityName, Class clazz) throws SQLException {
        // build time checks only for releases, not runtime (e.g. IDE debug)
        // that's check uses for cards db cleanup on new version/build
        String currentBuild = JarVersion.getBuildTime(clazz);
        if (!JarVersion.isBuildTimeOk(currentBuild)) {
            return false;
        }

        TableUtils.createTableIfNotExists(connectionSource, DatabaseBuild.class);
        Dao<DatabaseBuild, Object> buildDao = DaoManager.createDao(connectionSource, DatabaseBuild.class);

        QueryBuilder<DatabaseBuild, Object> queryBuilder = buildDao.queryBuilder();
        queryBuilder.where().eq("entity", new SelectArg(entityName))
                .and().eq("last_build", new SelectArg(currentBuild));
        List<DatabaseBuild> dbBuilds = buildDao.query(queryBuilder.prepare());

        if (dbBuilds.isEmpty()) {
            DatabaseBuild dbBuild = new DatabaseBuild();
            dbBuild.setEntity(entityName);
            dbBuild.setLastBuild(currentBuild);
            buildDao.create(dbBuild);
        }
        return dbBuilds.isEmpty();
    }

    public static void updateVersion(ConnectionSource connectionSource, String entityName, long version) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DatabaseVersion.class);
        Dao<DatabaseVersion, Object> versionDao = DaoManager.createDao(connectionSource, DatabaseVersion.class);

        QueryBuilder<DatabaseVersion, Object> queryBuilder = versionDao.queryBuilder();
        queryBuilder.where().eq("entity", new SelectArg(entityName));
        List<DatabaseVersion> dbVersions = versionDao.query(queryBuilder.prepare());

        if (!dbVersions.isEmpty()) {
            DeleteBuilder<DatabaseVersion, Object> deleteBuilder = versionDao.deleteBuilder();
            deleteBuilder.where().eq("entity", new SelectArg(entityName));
            deleteBuilder.delete();
        }
        DatabaseVersion databaseVersion = new DatabaseVersion();
        databaseVersion.setEntity(entityName);
        databaseVersion.setVersion(version);
        versionDao.create(databaseVersion);
    }

    public static long getDatabaseVersion(ConnectionSource connectionSource, String entityName) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DatabaseVersion.class);
        Dao<DatabaseVersion, Object> versionDao = DaoManager.createDao(connectionSource, DatabaseVersion.class);

        QueryBuilder<DatabaseVersion, Object> queryBuilder = versionDao.queryBuilder();
        queryBuilder.where().eq("entity", new SelectArg(entityName));
        List<DatabaseVersion> dbVersions = versionDao.query(queryBuilder.prepare());
        if (dbVersions.isEmpty()) {
            return 0;
        } else {
            return dbVersions.get(0).getVersion();
        }
    }

    public static boolean isDatabaseEmpty() {
        return ExpansionRepository.instance.getSetByCode("GRN") == null
                || CardRepository.instance.findCard("Island") == null;
    }

}
