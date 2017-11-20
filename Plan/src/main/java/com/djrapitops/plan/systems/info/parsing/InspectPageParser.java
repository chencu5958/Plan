/* 
 * Licence is provided in the jar as license.yml also here:
 * https://github.com/Rsl1122/Plan-PlayerAnalytics/blob/master/Plan/src/main/resources/license.yml
 */
package main.java.com.djrapitops.plan.systems.info.parsing;

import com.djrapitops.plugin.api.Benchmark;
import com.djrapitops.plugin.api.TimeAmount;
import com.djrapitops.plugin.api.utility.log.Log;
import main.java.com.djrapitops.plan.Settings;
import main.java.com.djrapitops.plan.api.IPlan;
import main.java.com.djrapitops.plan.api.exceptions.ParseException;
import main.java.com.djrapitops.plan.data.Action;
import main.java.com.djrapitops.plan.data.GeoInfo;
import main.java.com.djrapitops.plan.data.PlayerProfile;
import main.java.com.djrapitops.plan.data.Session;
import main.java.com.djrapitops.plan.data.time.WorldTimes;
import main.java.com.djrapitops.plan.database.Database;
import main.java.com.djrapitops.plan.utilities.FormatUtils;
import main.java.com.djrapitops.plan.utilities.MiscUtils;
import main.java.com.djrapitops.plan.utilities.analysis.AnalysisUtils;
import main.java.com.djrapitops.plan.utilities.analysis.MathUtils;
import main.java.com.djrapitops.plan.utilities.comparators.SessionLengthComparator;
import main.java.com.djrapitops.plan.utilities.comparators.SessionStartComparator;
import main.java.com.djrapitops.plan.utilities.file.FileUtil;
import main.java.com.djrapitops.plan.utilities.html.HtmlStructure;
import main.java.com.djrapitops.plan.utilities.html.HtmlUtils;
import main.java.com.djrapitops.plan.utilities.html.graphs.PunchCardGraphCreator;
import main.java.com.djrapitops.plan.utilities.html.graphs.ServerPreferencePieCreator;
import main.java.com.djrapitops.plan.utilities.html.graphs.WorldPieCreator;
import main.java.com.djrapitops.plan.utilities.html.tables.ActionsTableCreator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Used for parsing Inspect page out of database data and the html.
 *
 * @author Rsl1122
 */
public class InspectPageParser extends PageParser {

    private final UUID uuid;
    private final IPlan plugin;

    public InspectPageParser(UUID uuid, IPlan plugin) {
        this.uuid = uuid;
        this.plugin = plugin;
    }

    public String parse() throws ParseException {
        try {
            // TODO Player is online parts
            Log.logDebug("Database", "Inspect Parse Fetch");
            Benchmark.start("Inspect Parse, Fetch");
            Database db = plugin.getDB();

            long now = MiscUtils.getTime();

            addValue("refresh", FormatUtils.formatTimeStamp(now));
            addValue("version", MiscUtils.getPlanVersion());
            addValue("timeZone", MiscUtils.getTimeZoneOffsetHours());

            PlayerProfile profile = db.getPlayerProfile(uuid);

            Benchmark.stop("Inspect Parse, Fetch");

            String playerName = profile.getName();
            long registered = profile.getRegistered();
            int timesKicked = profile.getTimesKicked();
            long lastSeen = profile.getLastSeen();

            addValue("registered", FormatUtils.formatTimeStampYear(registered));
            addValue("playerName", playerName);
            addValue("kickCount", timesKicked);
            if (lastSeen != 0) {
                addValue("lastSeen", FormatUtils.formatTimeStampYear(lastSeen));
            } else {
                addValue("lastSeen", "-");
            }

            Map<UUID, String> serverNames = db.getServerTable().getServerNames();

            Map<UUID, WorldTimes> worldTimesPerServer = profile.getWorldTimesPerServer();
            addValue("serverPieSeries", ServerPreferencePieCreator.createSeriesData(serverNames, worldTimesPerServer));
            addValue("worldPieColors", Settings.THEME_GRAPH_WORLD_PIE.toString());
            addValue("gmPieColors", Settings.THEME_GRAPH_GM_PIE.toString());
            addValue("serverPieColors", Settings.THEME_GRAPH_SERVER_PREF_PIE.toString());

            String favoriteServer = serverNames.get(profile.getFavoriteServer());
            addValue("favoriteServer", favoriteServer != null ? favoriteServer : "Unknown");

            // TODO IP Timestamp table
            List<String> nicknames = profile.getNicknames().stream()
                    .map(HtmlUtils::swapColorsToSpan)
                    .collect(Collectors.toList());
            List<String> geoLocations = profile.getGeoInformation().stream()
                    .map(GeoInfo::getGeolocation)
                    .collect(Collectors.toList());

            // TODO REMOVE after 4.1.0
            addValue("nicknames", HtmlStructure.createDotList(nicknames.toArray(new String[nicknames.size()])));
            addValue("geolocations", HtmlStructure.createDotList(geoLocations.toArray(new String[geoLocations.size()])));

            Map<UUID, List<Session>> sessions = profile.getSessions();
            Map<String, List<Session>> sessionsByServerName = sessions.entrySet().stream()
                    .collect(Collectors.toMap(entry -> serverNames.get(entry.getKey()), Map.Entry::getValue));

            List<Session> allSessions = profile.getAllSessions()
                    .sorted(new SessionStartComparator())
                    .collect(Collectors.toList());

            String[] sessionsTabContent = HtmlStructure.createSessionsTabContentInspectPage(sessionsByServerName, allSessions, uuid);
            addValue("contentSessions", sessionsTabContent[0]);
            addValue("sessionTabGraphViewFunctions", sessionsTabContent[1]);
            addValue("contentServerOverview", HtmlStructure.createServerOverviewColumn(sessionsByServerName));

            long dayAgo = now - TimeAmount.DAY.ms();
            long weekAgo = now - TimeAmount.WEEK.ms();
            long monthAgo = now - TimeAmount.MONTH.ms();

            List<Session> sessionsDay = profile.getSessions(dayAgo, now).collect(Collectors.toList());
            List<Session> sessionsWeek = profile.getSessions(weekAgo, now).collect(Collectors.toList());
            List<Session> sessionsMonth = profile.getSessions(monthAgo, now).collect(Collectors.toList());

            long playtime = PlayerProfile.getPlaytime(allSessions.stream());
            long playtimeDay = PlayerProfile.getPlaytime(sessionsDay.stream());
            long playtimeWeek = PlayerProfile.getPlaytime(sessionsWeek.stream());
            long playtimeMonth = PlayerProfile.getPlaytime(sessionsMonth.stream());

            long longestSession = PlayerProfile.getLongestSession(allSessions.stream());
            long longestSessionDay = PlayerProfile.getLongestSession(sessionsDay.stream());
            long longestSessionWeek = PlayerProfile.getLongestSession(sessionsWeek.stream());
            long longestSessionMonth = PlayerProfile.getLongestSession(sessionsMonth.stream());

            long sessionMedian = PlayerProfile.getSessionMedian(allSessions.stream());
            long sessionMedianDay = PlayerProfile.getSessionMedian(sessionsDay.stream());
            long sessionMedianWeek = PlayerProfile.getSessionMedian(sessionsWeek.stream());
            long sessionMedianMonth = PlayerProfile.getSessionMedian(sessionsMonth.stream());

            int sessionCount = allSessions.size();
            int sessionCountDay = sessionsDay.size();
            int sessionCountWeek = sessionsWeek.size();
            int sessionCountMonth = sessionsMonth.size();

            long sessionAverage = MathUtils.averageLong(playtime, sessionCount);
            long sessionAverageDay = MathUtils.averageLong(playtimeDay, sessionCountDay);
            long sessionAverageWeek = MathUtils.averageLong(playtimeWeek, sessionCountWeek);
            long sessionAverageMonth = MathUtils.averageLong(playtimeMonth, sessionCountMonth);

            addValue("playtimeTotal", playtime > 0L ? FormatUtils.formatTimeAmount(playtime) : "-");
            addValue("playtimeDay", playtimeDay > 0L ? FormatUtils.formatTimeAmount(playtimeDay) : "-");
            addValue("playtimeWeek", playtimeWeek > 0L ? FormatUtils.formatTimeAmount(playtimeWeek) : "-");
            addValue("playtimeMonth", playtimeMonth > 0L ? FormatUtils.formatTimeAmount(playtimeMonth) : "-");

            addValue("sessionLengthLongest", longestSession > 0L ? FormatUtils.formatTimeAmount(longestSession) : "-");
            addValue("sessionLongestDay", longestSessionDay > 0L ? FormatUtils.formatTimeAmount(longestSessionDay) : "-");
            addValue("sessionLongestWeek", longestSessionWeek > 0L ? FormatUtils.formatTimeAmount(longestSessionWeek) : "-");
            addValue("sessionLongestMonth", longestSessionMonth > 0L ? FormatUtils.formatTimeAmount(longestSessionMonth) : "-");

            addValue("sessionLengthMedian", sessionMedian > 0L ? FormatUtils.formatTimeAmount(sessionMedian) : "-");
            addValue("sessionMedianDay", sessionMedianDay > 0L ? FormatUtils.formatTimeAmount(sessionMedianDay) : "-");
            addValue("sessionMedianWeek", sessionMedianWeek > 0L ? FormatUtils.formatTimeAmount(sessionMedianWeek) : "-");
            addValue("sessionMedianMonth", sessionMedianMonth > 0L ? FormatUtils.formatTimeAmount(sessionMedianMonth) : "-");

            addValue("sessionAverage", sessionAverage > 0L ? FormatUtils.formatTimeAmount(sessionAverage) : "-");
            addValue("sessionAverageDay", sessionAverageDay > 0L ? FormatUtils.formatTimeAmount(sessionAverageDay) : "-");
            addValue("sessionAverageWeek", sessionAverageWeek > 0L ? FormatUtils.formatTimeAmount(sessionAverageWeek) : "-");
            addValue("sessionAverageMonth", sessionAverageMonth > 0L ? FormatUtils.formatTimeAmount(sessionAverageMonth) : "-");


            addValue("sessionCount", sessionCount);
            addValue("sessionCountDay", sessionCountDay);
            addValue("sessionCountWeek", sessionCountWeek);
            addValue("sessionCountMonth", sessionCountMonth);

            List<Action> actions = profile.getAllActions();
            addValue("tableBodyActions", ActionsTableCreator.createTable(actions));

            String punchCardData = PunchCardGraphCreator.createDataSeries(allSessions);
            WorldTimes worldTimes = profile.getWorldTimes();
            AnalysisUtils.addMissingWorlds(worldTimes);

            String[] worldPieData = WorldPieCreator.createSeriesData(worldTimes);

            addValue("worldPieSeries", worldPieData[0]);
            addValue("gmSeries", worldPieData[1]);

            addValue("punchCardSeries", punchCardData);

            List<Session> sessionsInLengthOrder = allSessions.stream()
                    .sorted(new SessionLengthComparator())
                    .collect(Collectors.toList());
            if (sessionsInLengthOrder.isEmpty()) {
                addValue("sessionLengthMedian", "-");
                addValue("sessionLengthLongest", "-");
            } else {
                Session medianSession = sessionsInLengthOrder.get(sessionsInLengthOrder.size() / 2);
                addValue("sessionLengthMedian", FormatUtils.formatTimeAmount(medianSession.getLength()));
                addValue("sessionLengthLongest", FormatUtils.formatTimeAmount(sessionsInLengthOrder.get(0).getLength()));
            }

            long playerKillCount = allSessions.stream().map(Session::getPlayerKills).mapToLong(Collection::size).sum();
            long mobKillCount = allSessions.stream().mapToLong(Session::getMobKills).sum();
            long deathCount = allSessions.stream().mapToLong(Session::getDeaths).sum();

            addValue("playerKillCount", playerKillCount);
            addValue("mobKillCount", mobKillCount);
            addValue("deathCount", deathCount);

            boolean isActive = AnalysisUtils.isActive(MiscUtils.getTime(), lastSeen, playtime, sessionCount);
            String active = isActive ? "Active" : "Inactive";
            playerClassification(profile, active);

            if (!plugin.getInfoManager().isUsingAnotherWebServer()) {
                addValue("networkName", Settings.SERVER_NAME.toString());
            }

            return HtmlUtils.replacePlaceholders(FileUtil.getStringFromResource("web/player.html"), placeHolders);
        } catch (Exception e) {
            Log.toLog(this.getClass().getName(), e);
            throw new ParseException(e);
        }
    }

    private void playerClassification(PlayerProfile profile, String active) {
        boolean isBanned = profile.isBanned();
        boolean isOP = profile.isOp();

        String banned = isBanned ? "Banned" : "";
        String op = isOP ? "Operator (OP)" : "";

        addValue("playerClassification", HtmlStructure.separateWithDots(active, banned, op));
    }
}
