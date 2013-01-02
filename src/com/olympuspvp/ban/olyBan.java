package com.olympuspvp.ban;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class olyBan extends JavaPlugin{

	protected FileConfiguration config;
	private File configFile = new File("plugins" + File.separator + "olyBan" + File.separator + "config.yml");
	protected String tag = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "olyBan" + ChatColor.DARK_GRAY + "] " + ChatColor.RED;


	@Override
	public void onEnable(){
		if(!configFile.exists()){
			List<String> ips = new ArrayList<String>();
			ips.add("193.163.3.103");
			config = getConfig();
			config.set("BanLogs.IP.1",ips);
			config.set("BanLogs.IP.2",ips);
			config.set("BanLogs.CalDaFaggot.reason", "Homosexual");
			config.set("BanLogs.CalDaFaggot.reasonext", "We do not allow your kind.");
			config.set("BanLogs.CalDaFaggot.ip", "193.163.3.103");
			config.set("BanLogs.CalDaFaggot.banner", "CalDaBeast");
			saveConfig();
			System.out.println("[olyBan] Config created!");
		}config = getConfig();
		new LoginListener(this);
	}

	/**
	 * Sets a player to being banned in the bans.yml
	 * @param name The full name of the player to be banned
	 * @param reason The one-word reason for the ban
	 * @param description The extended reason for the ban
	 * @param ip The ip of the user
	 * @return True if the user's ip has now been banned twice.
	 */
	public boolean setBan(String name, String reason, String description, String banner, String ip, boolean handleIP){
		config.set("BanLogs." + name + ".reason", reason);
		config.set("BanLogs." + name + ".reasonext", description);
		config.set("BanLogs." + name + ".banner", banner);
		config.set("BanLogs." + name + ".ip", ip);
		if(!handleIP){
			saveConfig();
			return false;
		}List<String> oneBan = config.getStringList("BanLogs.IP.1");
		List<String> twoBan = config.getStringList("BanLogs.IP.2");
		if(oneBan.contains(ip)){
			twoBan.add(ip);
			oneBan.remove(ip);
			config.set("BanLogs.IP.1", oneBan);
			config.set("BanLogs.IP.2", twoBan);
			saveConfig();
			return true;
		}else if(!twoBan.contains(ip)){
			oneBan.add(ip);
			config.set("BanLogs.IP.1", oneBan);
			saveConfig();
		}return false;
	}

	/**
	 * Removes a player's status as banned
	 * @param name The full name of the player to unban
	 * @return The reason the player was originally banned
	 */
	public String unBan(String name){
		String reason = config.getString("BanLogs." + name + ".reason");
		if(reason == null) return null;
		String ip = config.getString("BanLogs." + name + ".ip");
		config.set("BanLogs." + name + ".reason", null);
		config.set("BanLogs." + name + ".reasonext", null);
		config.set("BanLogs." + name + ".ip", null);
		config.set("BanLogs." + name + ".banner", null);
		List<String> oneBan = config.getStringList("BanLogs.IP.1");
		List<String> twoBan = config.getStringList("BanLogs.IP.2");
		if(oneBan.contains(ip)){
			oneBan.remove(ip);
			config.set("BanLogs.IP.1", oneBan);
			config.set("BanLogs." + name, null);
			saveConfig();
		}else if(twoBan.contains(ip)){
			oneBan.add(ip);
			twoBan.remove(ip);
			config.set("BanLogs.IP.1", oneBan);
			config.set("BanLogs.IP.2", twoBan);
			config.set("BanLogs." + name, null);
			saveConfig();
		}return reason;
	}

	public static String strFromIP(byte[] ip){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < ip.length; i++){
			sb.append(ip[i]);
			if(i != (ip.length - 1)) sb.append(".");
		}return sb.toString();
	}

	public String getBanReason(String player, boolean useYou){
		String reason = config.getString("BanLogs." + player + ".reason");
		String reasonext = config.getString("BanLogs." + player + ".reasonext");
		String banner = config.getString("BanLogs." + player + ".banner");

		StringBuilder sb = new StringBuilder();
		sb.append(tag);
		if(useYou) sb.append("You have ");
		else sb.append(ChatColor.DARK_RED + player + ChatColor.RED + " has ");
		sb.append("been banned by " + ChatColor.DARK_RED + banner + ChatColor.RED + " for the offense " + ChatColor.DARK_RED + reason + "\n");
		sb.append(ChatColor.DARK_RED + "Extended reason: " + ChatColor.RED + reasonext);
		if(useYou){
			sb.append(ChatColor.RED + "\nIf you would like to dispute this ban, visit www.olympuspvp.com");
		}return sb.toString();
	}

	public boolean isBanned(String player){
		String reason = config.getString("BanLogs." + player + ".reason");
		if(reason == null) return false;
		return true;
	}

	public boolean onCommand(CommandSender s, Command cmd, String c, String[] args){

		if(!s.hasPermission("olyban.use")){
			s.sendMessage(tag + "You do not have permission to do this.");
			return true;
		}

		if(c.equalsIgnoreCase("ban")){
			//   /ban <name> <reason> <description>

			if(args.length < 3){
				s.sendMessage(tag + "Incorrect usage. /ban <name> <reason> <desc>");
				return true;
			}

			String name = args[0];
			Player ban = Bukkit.getPlayer(name);
			String reason = args[1];
			StringBuilder sb = new StringBuilder();
			for(int i = 2; i < args.length; i++){
				sb.append(args[i] + " ");
			}String desc = sb.toString();

			if(ban == null){
				for(Player p : Bukkit.getOnlinePlayers()){
					if(p.hasPermission("olyban.use")){
						p.sendMessage(tag + "Banning offline player " + ChatColor.DARK_RED + name + ChatColor.RED + " for the offense " + ChatColor.DARK_RED + reason);
						p.sendMessage(tag + ChatColor.DARK_RED + "Extended reason: " + ChatColor.RED + desc);
					}
				}System.out.println("[olyBan] Banning offline player " + name + " for the offense " + reason);
				this.setBan(name, reason, desc, s.getName(), null, false);
				return true;
			}else{
				name = ban.getName();
				String ip = strFromIP(ban.getAddress().getAddress().getAddress());
				boolean twoBans = this.setBan(name, reason, desc, s.getName(), ip, true);
				for(Player p : Bukkit.getOnlinePlayers()){
					p.sendMessage(tag + "Banning " + ChatColor.DARK_RED + name + ChatColor.RED + " for offense " + ChatColor.DARK_RED + reason);
					if(p.hasPermission("olyban.use")){
						p.sendMessage(tag + ChatColor.DARK_RED + "Extended reason: " + ChatColor.RED + desc);
						if(twoBans){ 
							p.sendMessage(tag + "IP Address " + ChatColor.DARK_RED + ip + ChatColor.RED + " has been banned twice.");
							p.sendMessage(tag + "All future connection attemps will be blocked.");
						}else{
							p.sendMessage(tag + "IP Address " + ChatColor.DARK_RED + ip + ChatColor.RED + " has been banned once.");
						}
					}
				}System.out.println("[olyBan] Banning player " + name + " for the offense " + reason);
				ban.kickPlayer(tag + "You have been banned by " + ChatColor.DARK_RED + s.getName() + ChatColor.RED + " for the offense " + ChatColor.DARK_RED + reason 
						+ "\nExtended reason: " + ChatColor.RED + desc + "\nIf you would like to dispute this ban, visit www.olympuspvp.com");
			}
		}else if(c.equalsIgnoreCase("getban")){
			//   /getban <full_name>
			if(args.length != 1){
				s.sendMessage(tag + "Incorrect usage. /getban <name>");
				return true;
			}String name = args[0];
			if(isBanned(name)){
				s.sendMessage(getBanReason(name, false));
			}else s.sendMessage(tag + "That player is not banned.");
			
			
		}else if(c.equalsIgnoreCase("unban")){
			if(args.length != 1){
				s.sendMessage(tag + "Incorrect usage. /unban <name>");
				return true;
			}String name = args[0];
			if(isBanned(name)){
				unBan(name);
				System.out.println("[olyBan] " + name + " has been unbanned by " + s.getName());
				for(Player p : Bukkit.getOnlinePlayers()){
					if(p.hasPermission("olyban.use")){
						p.sendMessage(tag + ChatColor.DARK_RED + name + ChatColor.RED + " has been unbanned by " + ChatColor.DARK_RED + s.getName());
					}
				}
			}else s.sendMessage(tag + "That player is not banned.");

		}else if(c.equalsIgnoreCase("kick")){
			//   /kick <name> <reason>
			String name = args[0];
			Player kick = Bukkit.getPlayer(name);
			StringBuilder sb = new StringBuilder();
			for(int i = 1; i < args.length; i++){
				sb.append(args[i] + " ");
			}String desc = sb.toString();

			if(kick == null){
				s.sendMessage(tag + "That player is not on the server.");
			}else{
				name = kick.getName();
				Bukkit.getServer().broadcastMessage(tag + "Kicked " + ChatColor.DARK_RED + name + ChatColor.RED + " for offense " + ChatColor.DARK_RED + desc);
				kick.kickPlayer(tag + "You have been kicked by " + ChatColor.DARK_RED + s.getName() + ChatColor.DARK_RED + "\nReason: " + ChatColor.RED + desc);
			}

		}return true;

	}
}