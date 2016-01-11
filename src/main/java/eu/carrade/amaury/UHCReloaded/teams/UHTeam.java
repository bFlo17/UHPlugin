/*
 * Copyright or © or Copr. Amaury Carrade (2014 - 2016)
 *
 * http://amaury.carrade.eu
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */

package eu.carrade.amaury.UHCReloaded.teams;

import eu.carrade.amaury.UHCReloaded.UHCReloaded;
import eu.carrade.amaury.UHCReloaded.i18n.I18n;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;


public class UHTeam
{
    private UHCReloaded plugin = null;
    private I18n i = null;

    private String name = null;
    private String internalName = null;
    private String displayName = null;
    private TeamColor color = null;

    private HashSet<UUID> players = new HashSet<>();


    public UHTeam(String name, TeamColor color)
    {
        this(name, color, UHCReloaded.get());
    }

    /**
     * @deprecated Use {@link #UHTeam(String, TeamColor)} instead.
     */
    @Deprecated
    public UHTeam(String name, TeamColor color, UHCReloaded plugin)
    {
        Validate.notNull(name, "The name cannot be null.");
        Validate.notNull(plugin, "The plugin cannot be null.");

        this.plugin = plugin;
        this.i = plugin.getI18n();

        this.name = name;

        // We don't use generateColor directly because we want to keep the "null" color.
        if (color == TeamColor.RANDOM) this.color = plugin.getTeamManager().generateColor(color);
        else this.color = color;


        // We use a random internal name because the name of a team, in Minecraft vanilla, is limited
        // (16 characters max).
        Random rand = new Random();
        this.internalName = String.valueOf(rand.nextInt(99999999)) + String.valueOf(rand.nextInt(99999999));

        if (this.color != null)
        {
            this.displayName = this.color.toChatColor() + name + ChatColor.RESET;
        }
        else
        {
            this.displayName = name;
        }

        Scoreboard sb = this.plugin.getScoreboardManager().getScoreboard();

        Team t = sb.registerNewTeam(this.internalName);
        t.setDisplayName(displayName.substring(0, Math.min(displayName.length(), 32)));

        if (this.color != null)
        {
            t.setPrefix(this.color.toChatColor().toString());
            t.setSuffix(ChatColor.RESET.toString());
        }

        t.setCanSeeFriendlyInvisibles(plugin.getConfig().getBoolean("teams-options.canSeeFriendlyInvisibles", true));
        t.setAllowFriendlyFire(plugin.getConfig().getBoolean("teams-options.allowFriendlyFire", true));
    }

    /**
     * Returns the name of the team.
     *
     * Can include spaces.
     *
     * @return The name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the display name of the team.
     *
     * This name is:
     *  - if the team is uncolored, the name of the team;
     *  - else, the name of the team with:
     *     - before, the color of the team;
     *     - after, the "reset" formatting mark (§r).
     *
     * @return The display name.
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * Returns the players inside this team.
     *
     * @return The players.
     */
    public Set<OfflinePlayer> getPlayers()
    {
        HashSet<OfflinePlayer> playersList = new HashSet<OfflinePlayer>();

        for (UUID id : players)
        {
            Player player = plugin.getServer().getPlayer(id);
            if (player != null)
            {
                playersList.add(player);
            }
            else
            {
                playersList.add(plugin.getServer().getOfflinePlayer(id));
            }
        }

        return playersList;
    }

    /**
     * Returns the online players inside this team.
     *
     * @return The online players.
     */
    public Set<Player> getOnlinePlayers()
    {
        HashSet<Player> playersList = new HashSet<Player>();

        for (UUID id : players)
        {
            Player player = plugin.getServer().getPlayer(id);
            if (player != null && player.isOnline())
            {
                playersList.add(player);
            }
        }

        return playersList;
    }

    /**
     * Returns the UUIDs of the players inside this team.
     *
     * @return The UUIDs of the players.
     */
    @SuppressWarnings ("unchecked")
    public Set<UUID> getPlayersUUID()
    {
        return Collections.unmodifiableSet(players);
    }

    /**
     * Returns the UUIDs of the online players inside this team.
     *
     * @return The UUID of the online players.
     */
    public Set<UUID> getOnlinePlayersUUID()
    {
        HashSet<UUID> playersList = new HashSet<UUID>();

        for (UUID id : players)
        {
            Player player = plugin.getServer().getPlayer(id);
            if (player != null && player.isOnline())
            {
                playersList.add(id);
            }
        }

        return playersList;
    }

    /**
     * Returns the size of this team.
     *
     * @return The size.
     */
    public int getSize()
    {
        return players.size();
    }

    /**
     * Returns true if the team is empty.
     *
     * @return The emptiness.
     */
    public boolean isEmpty()
    {
        return getSize() == 0;
    }

    /**
     * Adds a player inside this team.
     *
     * @param player The player to add.
     */
    public void addPlayer(OfflinePlayer player)
    {
        addPlayer(player, false);
    }

    /**
     * Adds a player inside this team.
     *
     * @param player The player to add.
     * @param silent If true, the player will not be notified about this.
     */
    public void addPlayer(OfflinePlayer player, boolean silent)
    {
        Validate.notNull(player, "The player cannot be null.");

        if (plugin.getTeamManager().getMaxPlayersPerTeam() != 0
                && this.players.size() >= plugin.getTeamManager().getMaxPlayersPerTeam())
        {
            throw new RuntimeException("The team " + getName() + " is full");
        }

        plugin.getTeamManager().removePlayerFromTeam(player, true);

        players.add(player.getUniqueId());
        plugin.getScoreboardManager().getScoreboard().getTeam(this.internalName).addPlayer(player);

        plugin.getTeamManager().colorizePlayer(player);

        if (!silent && player.isOnline())
        {
            ((Player) player).sendMessage(i.t("team.addplayer.added", getDisplayName()));
        }
    }

    /**
     * Removes a player from this team.
     *
     * Nothing is done if the player wasn't in this team.
     *
     * @param player The player to remove.
     */
    public void removePlayer(OfflinePlayer player)
    {
        removePlayer(player, false);
    }

    /**
     * Removes a player from this team.
     *
     * Nothing is done if the player wasn't in this team.
     *
     * @param player The player to remove.
     * @param silent If true, the player will not be notified.
     */
    public void removePlayer(OfflinePlayer player, boolean silent)
    {
        Validate.notNull(player, "The player cannot be null.");

        players.remove(player.getUniqueId());
        unregisterPlayer(player);

        if (!silent && player.isOnline())
        {
            ((Player) player).sendMessage(i.t("team.removeplayer.removed", getDisplayName()));
        }
    }

    /**
     * Unregisters a player from the scoreboard and uncolorizes the pseudo.
     *
     * Internal use, avoids a ConcurrentModificationException in this.deleteTeam()
     * (this.players is listed and emptied simultaneously, else).
     *
     * @param player
     */
    private void unregisterPlayer(OfflinePlayer player)
    {
        if (player == null) return;

        plugin.getScoreboardManager().getScoreboard().getTeam(this.internalName).removePlayer(player);
        plugin.getTeamManager().colorizePlayer(player);
    }

    /**
     * Deletes this team.
     *
     * The players inside the team are left without any team.
     */
    public void deleteTeam()
    {
        // We removes the players from the team (scoreboard team too)
        for (UUID id : players)
        {
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(id);

            if (player != null && player.isOnline())
            {
                ((Player) player).sendMessage(plugin.getI18n().t("team.removeplayer.removed", getDisplayName()));
            }

            unregisterPlayer(player);
        }

        this.players.clear();

        // Then the scoreboard team is deleted.
        plugin.getScoreboardManager().getScoreboard().getTeam(this.internalName).unregister();

    }

    /**
     * Returns true if the given player is in this team.
     *
     * @param player The player to check.
     * @return true if the given player is in this team.
     */
    public boolean containsPlayer(Player player)
    {
        Validate.notNull(player, "The player cannot be null.");

        return players.contains(player.getUniqueId());
    }

    /**
     * Returns true if the player with the given UUID is in this team.
     *
     * @param id The UUID of the player to check.
     * @return true if the given player is in this team.
     */
    public boolean containsPlayer(UUID id)
    {
        Validate.notNull(id, "The player cannot be null.");

        return players.contains(id);
    }

    /**
     * Teleports the entire team to the given location.
     *
     * @param lo
     */
    public void teleportTo(Location lo)
    {
        Validate.notNull(lo, "The location cannot be null.");

        for (UUID id : players)
        {
            Player player = plugin.getServer().getPlayer(id);
            if (player != null && player.isOnline())
            {
                player.teleport(lo, TeleportCause.PLUGIN);
            }
        }
    }

    /**
     * Returns the color of the team.
     *
     * @return
     */
    public TeamColor getColor()
    {
        return color;
    }


    @Override
    public int hashCode()
    {
        return ((name == null) ? 0 : name.hashCode());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof UHTeam))
            return false;
        UHTeam other = (UHTeam) obj;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        return true;
    }
}