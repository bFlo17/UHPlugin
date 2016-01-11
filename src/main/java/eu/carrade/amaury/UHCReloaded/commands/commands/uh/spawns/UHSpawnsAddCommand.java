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
package eu.carrade.amaury.UHCReloaded.commands.commands.uh.spawns;

import eu.carrade.amaury.UHCReloaded.UHCReloaded;
import eu.carrade.amaury.UHCReloaded.commands.core.AbstractCommand;
import eu.carrade.amaury.UHCReloaded.commands.core.annotations.Command;
import eu.carrade.amaury.UHCReloaded.commands.core.exceptions.CannotExecuteCommandException;
import eu.carrade.amaury.UHCReloaded.i18n.I18n;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Command (name = "add")
public class UHSpawnsAddCommand extends AbstractCommand
{

    UHCReloaded p;
    I18n i;

    public UHSpawnsAddCommand(UHCReloaded plugin)
    {
        p = plugin;
        i = p.getI18n();
    }

    /**
     * Runs the command.
     *
     * @param sender The sender of the command.
     * @param args   The arguments passed to the command.
     *
     * @throws eu.carrade.amaury.UHCReloaded.commands.core.exceptions.CannotExecuteCommandException If the command cannot be executed.
     */
    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException
    {

        // World?
        World world;
        if (sender instanceof Player)
        {
            world = ((Player) sender).getWorld();
        }
        else if (sender instanceof BlockCommandSender)
        {
            world = ((BlockCommandSender) sender).getBlock().getWorld();
        }
        else
        {
            world = p.getServer().getWorlds().get(0);
        }

        if (args.length == 0)
        { // /uh spawns add
            if (!(sender instanceof Player))
            {
                throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.ONLY_AS_A_PLAYER);
            }
            else
            {
                Player pl = (Player) sender; // Just a way to avoid casts everywhere.
                try
                {
                    p.getSpawnsManager().addSpawnPoint(pl.getLocation());
                    sender.sendMessage(i.t("spawns.add.added", world.getName(), String.valueOf(pl.getLocation().getBlockX()), String.valueOf(pl.getLocation().getBlockZ())));
                }
                catch (IllegalArgumentException e)
                {
                    sender.sendMessage(i.t("spawns.add.outOfLimits"));
                }
                catch (RuntimeException e)
                {
                    sender.sendMessage(i.t("spawns.add.noSafeSpot"));
                }
            }
        }
        else if (args.length == 1)
        { // /uh spawns add <x>: Two coordinates needed!
            sender.sendMessage(i.t("spawns.error2Coords"));
        }
        else
        { // /uh spawns add <x> <z>
            try
            {
                p.getSpawnsManager().addSpawnPoint(world, Double.parseDouble(args[0]), Double.parseDouble(args[1]));
                sender.sendMessage(i.t("spawns.add.added", world.getName(), args[0], args[1]));
            }
            catch (NumberFormatException e)
            {
                sender.sendMessage(i.t("spawns.NaN"));
            }
            catch (IllegalArgumentException e)
            {
                sender.sendMessage(i.t("spawns.add.outOfLimits"));
            }
            catch (RuntimeException e)
            {
                sender.sendMessage(i.t("spawns.add.noSafeSpot"));
            }
        }
    }

    /**
     * Tab-completes this command.
     *
     * @param sender The sender.
     * @param args   The arguments passed to the command.
     *
     * @return A list of suggestions.
     */
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args)
    {
        return null;
    }

    @Override
    public List<String> help(CommandSender sender)
    {
        return Collections.singletonList(i.t("cmd.spawnsHelpAdd"));
    }

    @Override
    public List<String> onListHelp(CommandSender sender)
    {
        return null;
    }
}