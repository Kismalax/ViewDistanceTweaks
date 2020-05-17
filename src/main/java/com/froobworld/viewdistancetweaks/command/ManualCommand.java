package com.froobworld.viewdistancetweaks.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.froobworld.viewdistancetweaks.ViewDistanceTweaks;
import com.froobworld.viewdistancetweaks.util.FakeWorld;
import com.froobworld.viewdistancetweaks.util.ViewDistanceUtils;

public class ManualCommand implements CommandExecutor {
	private ViewDistanceTweaks viewDistanceTweaks;

	public ManualCommand(ViewDistanceTweaks viewDistanceTweaks) {
		this.viewDistanceTweaks = viewDistanceTweaks;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			boolean notick = args[0].equalsIgnoreCase("manualnotick") || args[0].equalsIgnoreCase("mant");
			World world = new FakeWorld();
			
			int distance;
			try {
				distance = Integer.parseInt(args[1]);
				distance = ViewDistanceUtils.clampViewDistance(distance);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "The view distance have to be a numeric value!");
				return true;
			}
			
			if (args.length > 2) {
				world = Bukkit.getWorld(args[2]);
				if (world == null) {
					sender.sendMessage(ChatColor.RED + "The specified world is not exists!");
					return true;
				}
			}
			if (notick) {
				viewDistanceTweaks.getVdtConfig().paperSettings.worldSettings.of(world).minimumNoTickViewDistance.setValue(distance);
				viewDistanceTweaks.getVdtConfig().paperSettings.worldSettings.of(world).maximumNoTickViewDistance.setValue(distance);
			} else {
				viewDistanceTweaks.getVdtConfig().worldSettings.of(world).minimumViewDistance.setValue(distance);
				viewDistanceTweaks.getVdtConfig().worldSettings.of(world).maximumViewDistance.setValue(distance);
			}
			viewDistanceTweaks.apply();
			sender.sendMessage(ChatColor.YELLOW + (world instanceof FakeWorld ? "Default " : "The ")
					+ (notick ? "no-tick " : "") + "view distance setting"
					+ (world instanceof FakeWorld ? "" : " for the world " + ChatColor.RED + world.getName() + ChatColor.YELLOW) + " is now "
					+ ChatColor.RED + distance);
			
			sender.sendMessage(ChatColor.GRAY + "Please use " + ChatColor.RED + "/" + label + " reload " + ChatColor.GRAY
					+ "to revert manual changes.");
		} catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Something went wrong reloading the plugin, see the console for more.");
            e.printStackTrace();
        }
		return true;
	}

}
